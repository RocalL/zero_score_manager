package com.zerogame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zerogame.data.dao.GameDao
import com.zerogame.data.dao.GamePlayerDao
import com.zerogame.data.dao.PlayerDao
import com.zerogame.data.dao.PlayerGameKpiDao
import com.zerogame.data.dao.RoundScoreDao
import com.zerogame.data.model.Game
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.Player
import com.zerogame.data.model.PlayerGameKpi
import com.zerogame.data.model.RoundScore

@Database(
    entities = [Player::class, Game::class, GamePlayer::class, RoundScore::class, PlayerGameKpi::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun gamePlayerDao(): GamePlayerDao
    abstract fun roundScoreDao(): RoundScoreDao
    abstract fun playerGameKpiDao(): PlayerGameKpiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE games ADD COLUMN gameType TEXT NOT NULL DEFAULT 'ZERO'")
                database.execSQL("ALTER TABLE games ADD COLUMN config TEXT NOT NULL DEFAULT '{}'")

                database.execSQL("CREATE TABLE game_players_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, gameId INTEGER NOT NULL, playerId INTEGER NOT NULL, totalScore INTEGER NOT NULL DEFAULT 0, roundsPlayed INTEGER NOT NULL DEFAULT 0, extras TEXT NOT NULL DEFAULT '{}', FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE, FOREIGN KEY (playerId) REFERENCES players(id) ON DELETE CASCADE)")
                database.execSQL("INSERT INTO game_players_new (id, gameId, playerId, totalScore, roundsPlayed, extras) SELECT id, gameId, playerId, totalScore, roundsPlayed, '{}' FROM game_players")
                database.execSQL("DROP TABLE game_players")
                database.execSQL("ALTER TABLE game_players_new RENAME TO game_players")

                database.execSQL("CREATE TABLE round_scores_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, gameId INTEGER NOT NULL, playerId INTEGER NOT NULL, roundNumber INTEGER NOT NULL, score INTEGER NOT NULL, extras TEXT NOT NULL DEFAULT '{}', FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE, FOREIGN KEY (playerId) REFERENCES players(id) ON DELETE CASCADE)")
                database.execSQL("INSERT INTO round_scores_new (id, gameId, playerId, roundNumber, score, extras) SELECT id, gameId, playerId, roundNumber, score, '{}' FROM round_scores")
                database.execSQL("DROP TABLE round_scores")
                database.execSQL("ALTER TABLE round_scores_new RENAME TO round_scores")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS player_game_kpis (
                        playerId INTEGER NOT NULL,
                        gameType TEXT NOT NULL,
                        totalGames INTEGER NOT NULL DEFAULT 0,
                        totalWins INTEGER NOT NULL DEFAULT 0,
                        winRate REAL NOT NULL DEFAULT 0,
                        currentWinStreak INTEGER NOT NULL DEFAULT 0,
                        maxWinStreak INTEGER NOT NULL DEFAULT 0,
                        totalPoints INTEGER NOT NULL DEFAULT 0,
                        averagePoints REAL NOT NULL DEFAULT 0,
                        totalZeros INTEGER NOT NULL DEFAULT 0,
                        triggerCount INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(playerId, gameType)
                    )
                """)

                backfillKpis(database)
            }

            private fun backfillKpis(db: SupportSQLiteDatabase) {
                val gamePlayerCursor = db.query("SELECT gameId, playerId, totalScore, extras FROM game_players")
                val gameMap = mutableMapOf<Long, Pair<String, Long>>()
                val cursor = db.query("SELECT id, gameType, createdAt FROM games")
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(0)
                    val type = cursor.getString(1)
                    val created = cursor.getLong(2)
                    gameMap[id] = Pair(type, created)
                }
                cursor.close()

                data class PlayerGameInfo(val gameId: Long, val gameType: String, val createdAt: Long, val totalScore: Int, val zeros: Int)
                val playerGames = mutableMapOf<Long, MutableList<PlayerGameInfo>>()

                while (gamePlayerCursor.moveToNext()) {
                    val gameId = gamePlayerCursor.getLong(0)
                    val playerId = gamePlayerCursor.getLong(1)
                    val totalScore = gamePlayerCursor.getInt(2)
                    val extrasStr = gamePlayerCursor.getString(3) ?: "{}"
                    val zeros = try {
                        val json = org.json.JSONObject(extrasStr)
                        json.optInt("zerosAchieved", 0)
                    } catch (_: Exception) { 0 }

                    val gameInfo = gameMap[gameId] ?: continue
                    playerGames.getOrPut(playerId) { mutableListOf() }.add(
                        PlayerGameInfo(gameId, gameInfo.first, gameInfo.second, totalScore, zeros)
                    )
                }
                gamePlayerCursor.close()

                for ((playerId, games) in playerGames) {
                    val byType = games.groupBy { it.gameType }
                    for ((gameType, typeGames) in byType) {
                        val sorted = typeGames.sortedBy { it.createdAt }
                        val totalGames = sorted.size
                        var wins = 0
                        var totalPts = 0
                        var zeros = 0
                        var triggers = 0
                        var streak = 0
                        var maxStreak = 0

                        for (info in sorted) {
                            totalPts += info.totalScore
                            zeros += info.zeros
                            val isWin = sorted.filter { it.gameId != info.gameId }
                                .none { it.totalScore < info.totalScore && sorted.size > 1 }
                            if (isWin && totalGames > 1) {
                                wins++
                                streak++
                                if (streak > maxStreak) maxStreak = streak
                            } else {
                                streak = 0
                            }
                        }

                        val winRate = if (totalGames > 0) wins.toFloat() / totalGames else 0f
                        val avgPts = if (totalGames > 0) totalPts.toFloat() / totalGames else 0f

                        db.execSQL(
                            "INSERT OR REPLACE INTO player_game_kpis (playerId, gameType, totalGames, totalWins, winRate, currentWinStreak, maxWinStreak, totalPoints, averagePoints, totalZeros, triggerCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            arrayOf(playerId, gameType, totalGames, wins, winRate, streak, maxStreak, totalPts, avgPts, zeros, triggers)
                        )
                    }
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zero_game_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
