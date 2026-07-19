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

object DatabaseExporter {

    suspend fun exportToJson(context: Context): String = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val root = JSONObject()
        root.put("version", 1)

        // Players
        val playersArr = JSONArray()
        val playersCursor = db.openHelper.readableDatabase.query("SELECT * FROM players")
        while (playersCursor.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", playersCursor.getLong(playersCursor.getColumnIndexOrThrow("id")))
            obj.put("name", playersCursor.getString(playersCursor.getColumnIndexOrThrow("name")))
            obj.put("createdAt", playersCursor.getLong(playersCursor.getColumnIndexOrThrow("createdAt")))
            playersArr.put(obj)
        }
        playersCursor.close()
        root.put("players", playersArr)

        // Games
        val gamesArr = JSONArray()
        val gamesCursor = db.openHelper.readableDatabase.query("SELECT * FROM games")
        while (gamesCursor.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow("id")))
            obj.put("gameType", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow("gameType")))
            obj.put("createdAt", gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow("createdAt")))
            obj.put("numberOfRounds", gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow("numberOfRounds")))
            obj.put("isFinished", gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow("isFinished")))
            obj.put("config", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow("config")))
            gamesArr.put(obj)
        }
        gamesCursor.close()
        root.put("games", gamesArr)

        // Game Players
        val gpArr = JSONArray()
        val gpCursor = db.openHelper.readableDatabase.query("SELECT * FROM game_players")
        while (gpCursor.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", gpCursor.getLong(gpCursor.getColumnIndexOrThrow("id")))
            obj.put("gameId", gpCursor.getLong(gpCursor.getColumnIndexOrThrow("gameId")))
            obj.put("playerId", gpCursor.getLong(gpCursor.getColumnIndexOrThrow("playerId")))
            obj.put("totalScore", gpCursor.getInt(gpCursor.getColumnIndexOrThrow("totalScore")))
            obj.put("roundsPlayed", gpCursor.getInt(gpCursor.getColumnIndexOrThrow("roundsPlayed")))
            obj.put("extras", gpCursor.getString(gpCursor.getColumnIndexOrThrow("extras")))
            gpArr.put(obj)
        }
        gpCursor.close()
        root.put("gamePlayers", gpArr)

        // Round Scores
        val rsArr = JSONArray()
        val rsCursor = db.openHelper.readableDatabase.query("SELECT * FROM round_scores")
        while (rsCursor.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", rsCursor.getLong(rsCursor.getColumnIndexOrThrow("id")))
            obj.put("gameId", rsCursor.getLong(rsCursor.getColumnIndexOrThrow("gameId")))
            obj.put("playerId", rsCursor.getLong(rsCursor.getColumnIndexOrThrow("playerId")))
            obj.put("roundNumber", rsCursor.getInt(rsCursor.getColumnIndexOrThrow("roundNumber")))
            obj.put("score", rsCursor.getInt(rsCursor.getColumnIndexOrThrow("score")))
            obj.put("extras", rsCursor.getString(rsCursor.getColumnIndexOrThrow("extras")))
            rsArr.put(obj)
        }
        rsCursor.close()
        root.put("roundScores", rsArr)

        // Player Game KPIs
        val kpiArr = JSONArray()
        val kpiCursor = db.openHelper.readableDatabase.query("SELECT * FROM player_game_kpis")
        while (kpiCursor.moveToNext()) {
            val obj = JSONObject()
            obj.put("playerId", kpiCursor.getLong(kpiCursor.getColumnIndexOrThrow("playerId")))
            obj.put("gameType", kpiCursor.getString(kpiCursor.getColumnIndexOrThrow("gameType")))
            obj.put("totalGames", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("totalGames")))
            obj.put("totalWins", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("totalWins")))
            obj.put("winRate", kpiCursor.getFloat(kpiCursor.getColumnIndexOrThrow("winRate")))
            obj.put("currentWinStreak", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("currentWinStreak")))
            obj.put("maxWinStreak", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("maxWinStreak")))
            obj.put("totalPoints", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("totalPoints")))
            obj.put("averagePoints", kpiCursor.getFloat(kpiCursor.getColumnIndexOrThrow("averagePoints")))
            obj.put("totalZeros", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("totalZeros")))
            obj.put("triggerCount", kpiCursor.getInt(kpiCursor.getColumnIndexOrThrow("triggerCount")))
            kpiArr.put(obj)
        }
        kpiCursor.close()
        root.put("playerGameKpis", kpiArr)

        root.toString(2)
    }

    fun exportToFile(context: Context, uri: Uri) {
        val json = kotlinx.coroutines.runBlocking { exportToJson(context) }
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(json.toByteArray())
        }
    }
}
