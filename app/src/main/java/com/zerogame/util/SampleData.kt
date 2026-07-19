package com.zerogame.util

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.zerogame.data.AppDatabase
import com.zerogame.data.model.Game
import com.zerogame.data.model.GameType
import com.zerogame.data.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.random.Random

object SampleData {

    suspend fun load(context: Context) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val database = db.openHelper.writableDatabase
        val rng = Random(42)

        database.execSQL("PRAGMA foreign_keys = OFF")
        database.beginTransaction()
        try {
            database.execSQL("DELETE FROM player_game_kpis")
            database.execSQL("DELETE FROM round_scores")
            database.execSQL("DELETE FROM game_players")
            database.execSQL("DELETE FROM games")
            database.execSQL("DELETE FROM players")
            database.execSQL("DELETE FROM sqlite_sequence WHERE name IN ('players', 'games', 'game_players', 'round_scores')")

            val base = 1680000000000L
            val playerNames = listOf(
                "Alice", "Bob", "Charlie", "Diana", "Edward",
                "Fiona", "George", "Hannah", "Ivan", "Julia"
            )
            val players = playerNames.mapIndexed { i, name ->
                Player(id = (i + 1).toLong(), name = name, createdAt = base + i * 86400000L)
            }
            players.forEach { p ->
                database.execSQL("INSERT INTO players (id, name, createdAt) VALUES (?, ?, ?)",
                    arrayOf(p.id, p.name, p.createdAt))
            }

            var gameId = 1L
            var gpId = 1L
            var rsId = 1L
            val allGames = mutableListOf<Game>()
            val allGpRows = mutableListOf<GpRow>()

            // Generate 60 Zero games
            for (g in 1..60) {
                val numPlayers = rng.nextInt(2, 6)
                val playerIds = players.shuffled(rng).take(numPlayers).map { it.id }
                val createdAt = base + (g.toLong() * 7200000L)
                val rounds = numPlayers

                val roundScores = mutableMapOf<Long, MutableList<Int>>()
                playerIds.forEach { roundScores[it] = mutableListOf() }
                val zerosAchieved = mutableMapOf<Long, Int>()
                playerIds.forEach { zerosAchieved[it] = 0 }

                for (round in 1..rounds) {
                    for (pid in playerIds) {
                        val isZero = rng.nextInt(100) < 25
                        val score = if (isZero) {
                            zerosAchieved[pid] = zerosAchieved[pid]!! + 1
                            0
                        } else {
                            rng.nextInt(-2, 13).coerceAtLeast(0)
                        }
                        roundScores[pid]!!.add(score)

                        val extras = if (isZero) """{"achievedZero":"true"}""" else """{}"""
                        database.execSQL(
                            "INSERT INTO round_scores (id, gameId, playerId, roundNumber, score, extras) VALUES (?, ?, ?, ?, ?, ?)",
                            arrayOf(rsId++, gameId, pid, round, score, extras)
                        )
                    }
                }

                for (pid in playerIds) {
                    val total = roundScores[pid]!!.sum()
                    val zeros = zerosAchieved[pid]!!
                    val extras = if (zeros > 0) """{"zerosAchieved":"$zeros"}""" else """{}"""
                    database.execSQL(
                        "INSERT INTO game_players (id, gameId, playerId, totalScore, roundsPlayed, extras) VALUES (?, ?, ?, ?, ?, ?)",
                        arrayOf(gpId++, gameId, pid, total, rounds, extras)
                    )
                    allGpRows.add(GpRow(gameId, pid, total, zeros, 0))
                }

                database.execSQL(
                    "INSERT INTO games (id, gameType, createdAt, numberOfRounds, isFinished, config) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(gameId, GameType.ZERO.name, createdAt, rounds, 1, "{}")
                )
                allGames.add(Game(id = gameId, gameType = GameType.ZERO, createdAt = createdAt, numberOfRounds = rounds, isFinished = true))
                gameId++
            }

            // Generate 60 Skyjo games
            for (g in 1..60) {
                val numPlayers = rng.nextInt(2, 6)
                val playerIds = players.shuffled(rng).take(numPlayers).map { it.id }
                val createdAt = base + (60L * 7200000L) + (g.toLong() * 7200000L)
                var rounds = 0
                val cumulativeScores = mutableMapOf<Long, Int>()
                playerIds.forEach { cumulativeScores[it] = 0 }
                val triggerCount = mutableMapOf<Long, Int>()
                playerIds.forEach { triggerCount[it] = 0 }

                while (cumulativeScores.values.none { it >= 100 } && rounds < 15) {
                    rounds++
                    for (pid in playerIds) {
                        val isTrigger = rng.nextInt(100) < 20
                        if (isTrigger) triggerCount[pid] = triggerCount[pid]!! + 1

                        val roundScore = rng.nextInt(-3, 16).coerceAtLeast(-3)
                        val finalScore = if (isTrigger && roundScore > 0) roundScore * 2 else roundScore
                        cumulativeScores[pid] = cumulativeScores[pid]!! + finalScore

                        val extras = if (isTrigger) """{"isTrigger":"true"}""" else """{}"""
                        database.execSQL(
                            "INSERT INTO round_scores (id, gameId, playerId, roundNumber, score, extras) VALUES (?, ?, ?, ?, ?, ?)",
                            arrayOf(rsId++, gameId, pid, rounds, finalScore, extras)
                        )
                    }
                }

                for (pid in playerIds) {
                    val total = cumulativeScores[pid]!!
                    val tc = triggerCount[pid]!!
                    val extras = if (tc > 0) """{"isTrigger":"$tc"}""" else """{}"""
                    database.execSQL(
                        "INSERT INTO game_players (id, gameId, playerId, totalScore, roundsPlayed, extras) VALUES (?, ?, ?, ?, ?, ?)",
                        arrayOf(gpId++, gameId, pid, total, rounds, extras)
                    )
                    allGpRows.add(GpRow(gameId, pid, total, 0, tc))
                }

                database.execSQL(
                    "INSERT INTO games (id, gameType, createdAt, numberOfRounds, isFinished, config) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(gameId, GameType.SKYJO.name, createdAt, rounds, 1, """{"targetScore":"100"}""")
                )
                allGames.add(Game(id = gameId, gameType = GameType.SKYJO, createdAt = createdAt, numberOfRounds = rounds, isFinished = true, config = mapOf("targetScore" to "100")))
                gameId++
            }

            // Compute and insert KPIs
            for (player in players) {
                for (gameType in GameType.values()) {
                    val typeGameIds = allGames.filter { it.gameType == gameType }.map { it.id }.toSet()
                    val playerGps = allGpRows.filter { it.playerId == player.id && it.gameId in typeGameIds }

                    if (playerGps.isEmpty()) continue

                    var wins = 0; var totalPts = 0; var totalZeros = 0; var triggers = 0
                    var streak = 0; var maxStreak = 0

                    for (gp in playerGps) {
                        totalPts += gp.totalScore
                        totalZeros += gp.zeros
                        triggers += gp.trigger

                        val allInGame = allGpRows.filter { it.gameId == gp.gameId }
                        val minScore = allInGame.minOfOrNull { it.totalScore }
                        val isWin = minScore != null && gp.totalScore == minScore && allInGame.size > 1

                        if (isWin) { wins++; streak++; if (streak > maxStreak) maxStreak = streak }
                        else { streak = 0 }
                    }

                    val totalGames = playerGps.size
                    database.execSQL(
                        "INSERT OR REPLACE INTO player_game_kpis (playerId, gameType, totalGames, totalWins, winRate, currentWinStreak, maxWinStreak, totalPoints, averagePoints, totalZeros, triggerCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        arrayOf(
                            player.id, gameType.name, totalGames, wins,
                            if (totalGames > 0) wins.toFloat() / totalGames else 0f,
                            streak, maxStreak, totalPts,
                            if (totalGames > 0) totalPts.toFloat() / totalGames else 0f,
                            totalZeros, triggers
                        )
                    )
                }
            }

            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
            database.execSQL("PRAGMA foreign_keys = ON")
        }
    }

    private data class GpRow(val gameId: Long, val playerId: Long, val totalScore: Int, val zeros: Int, val trigger: Int)
}
