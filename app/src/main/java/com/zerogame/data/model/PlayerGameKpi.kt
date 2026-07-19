package com.zerogame.data.model

import androidx.room.Entity

@Entity(
    tableName = "player_game_kpis",
    primaryKeys = ["playerId", "gameType"]
)
data class PlayerGameKpi(
    val playerId: Long,
    val gameType: GameType,
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val winRate: Float = 0f,
    val currentWinStreak: Int = 0,
    val maxWinStreak: Int = 0,
    val totalPoints: Int = 0,
    val averagePoints: Float = 0f,
    val totalZeros: Int = 0,
    val triggerCount: Int = 0
)
