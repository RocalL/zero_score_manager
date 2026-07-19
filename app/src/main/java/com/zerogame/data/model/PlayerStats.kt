package com.zerogame.data.model

data class GameWithPlayers(
    val game: Game,
    val players: List<PlayerWithScore>
)

data class PlayerWithScore(
    val player: Player,
    val totalScore: Int,
    val roundsPlayed: Int,
    val zerosAchieved: Int
)

data class PlayerStats(
    val playerId: Long,
    val playerName: String,
    val totalGames: Int,
    val totalWins: Int,
    val totalRounds: Int,
    val totalScore: Int,
    val totalZeros: Int,
    val averageScore: Double
)
