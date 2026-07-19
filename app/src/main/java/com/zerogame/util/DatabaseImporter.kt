package com.zerogame.util

import android.content.Context
import android.net.Uri
import com.zerogame.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object DatabaseImporter {

    suspend fun importFromJson(context: Context, json: String) = withContext(Dispatchers.IO) {
        val root = JSONObject(json)
        val db = AppDatabase.getDatabase(context)
        val database = db.openHelper.writableDatabase

        database.execSQL("PRAGMA foreign_keys = OFF")

        database.beginTransaction()
        try {
            // Clear existing data
            database.execSQL("DELETE FROM player_game_kpis")
            database.execSQL("DELETE FROM round_scores")
            database.execSQL("DELETE FROM game_players")
            database.execSQL("DELETE FROM games")
            database.execSQL("DELETE FROM players")

            // Reset auto-increment
            database.execSQL("DELETE FROM sqlite_sequence WHERE name IN ('players', 'games', 'game_players', 'round_scores')")

            // Import Players
            val playersArr = root.getJSONArray("players")
            for (i in 0 until playersArr.length()) {
                val p = playersArr.getJSONObject(i)
                database.execSQL(
                    "INSERT INTO players (id, name, createdAt) VALUES (?, ?, ?)",
                    arrayOf(p.getLong("id"), p.getString("name"), p.getLong("createdAt"))
                )
            }

            // Import Games
            val gamesArr = root.getJSONArray("games")
            for (i in 0 until gamesArr.length()) {
                val g = gamesArr.getJSONObject(i)
                database.execSQL(
                    "INSERT INTO games (id, gameType, createdAt, numberOfRounds, isFinished, config) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(
                        g.getLong("id"),
                        g.getString("gameType"),
                        g.getLong("createdAt"),
                        g.getInt("numberOfRounds"),
                        g.getInt("isFinished"),
                        g.optString("config", "{}")
                    )
                )
            }

            // Import Game Players
            val gpArr = root.getJSONArray("gamePlayers")
            for (i in 0 until gpArr.length()) {
                val gp = gpArr.getJSONObject(i)
                database.execSQL(
                    "INSERT INTO game_players (id, gameId, playerId, totalScore, roundsPlayed, extras) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(
                        gp.getLong("id"),
                        gp.getLong("gameId"),
                        gp.getLong("playerId"),
                        gp.getInt("totalScore"),
                        gp.getInt("roundsPlayed"),
                        gp.optString("extras", "{}")
                    )
                )
            }

            // Import Round Scores
            val rsArr = root.getJSONArray("roundScores")
            for (i in 0 until rsArr.length()) {
                val rs = rsArr.getJSONObject(i)
                database.execSQL(
                    "INSERT INTO round_scores (id, gameId, playerId, roundNumber, score, extras) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(
                        rs.getLong("id"),
                        rs.getLong("gameId"),
                        rs.getLong("playerId"),
                        rs.getInt("roundNumber"),
                        rs.getInt("score"),
                        rs.optString("extras", "{}")
                    )
                )
            }

            // Import Player Game KPIs
            val kpiArr = root.getJSONArray("playerGameKpis")
            for (i in 0 until kpiArr.length()) {
                val kpi = kpiArr.getJSONObject(i)
                database.execSQL(
                    "INSERT INTO player_game_kpis (playerId, gameType, totalGames, totalWins, winRate, currentWinStreak, maxWinStreak, totalPoints, averagePoints, totalZeros, triggerCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    arrayOf(
                        kpi.getLong("playerId"),
                        kpi.getString("gameType"),
                        kpi.getInt("totalGames"),
                        kpi.getInt("totalWins"),
                        kpi.getDouble("winRate"),
                        kpi.getInt("currentWinStreak"),
                        kpi.getInt("maxWinStreak"),
                        kpi.getInt("totalPoints"),
                        kpi.getDouble("averagePoints"),
                        kpi.getInt("totalZeros"),
                        kpi.getInt("triggerCount")
                    )
                )
            }

            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
            database.execSQL("PRAGMA foreign_keys = ON")
        }
    }

    fun importFromFile(context: Context, uri: Uri) {
        val json = context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).readText()
        } ?: throw Exception("Could not read file")
        kotlinx.coroutines.runBlocking { importFromJson(context, json) }
    }
}
