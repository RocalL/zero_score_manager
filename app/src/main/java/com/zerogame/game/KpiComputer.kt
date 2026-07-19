package com.zerogame.game

import com.zerogame.data.model.Game
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.GameType
import com.zerogame.data.model.PlayerGameKpi

object KpiComputer {

    fun computeAfterGame(
        playerId: Long,
        gameType: GameType,
        finishedGame: Game,
        allGamePlayersInGame: List<GamePlayer>,
        allGamesOfType: List<Game>,
        allGamePlayersOfType: List<GamePlayer>,
        isTrigger: Boolean = false
    ): PlayerGameKpi {
        val playerGames = allGamesOfType
            .filter { game -> allGamePlayersOfType.any { it.gameId == game.id && it.playerId == playerId } }
            .sortedBy { it.createdAt }

        val totalGames = playerGames.size

        val playerGpsByGame = mutableMapOf<Long, GamePlayer>()
        for (gp in allGamePlayersOfType.filter { it.playerId == playerId }) {
            playerGpsByGame[gp.gameId] = gp
        }

        var wins = 0
        var totalPts = 0
        var totalZeros = 0
        var triggerCount = 0
        var streak = 0
        var maxStreak = 0

        for (game in playerGames) {
            val gp = playerGpsByGame[game.id] ?: continue
            val score = gp.totalScore
            totalPts += score
            totalZeros += gp.extras["zerosAchieved"]?.toIntOrNull() ?: 0
            triggerCount += if (gp.extras["isTrigger"] == "true") 1 else 0

            val allInGame = allGamePlayersOfType.filter { it.gameId == game.id }
            val minScore = allInGame.minOfOrNull { it.totalScore }
            val isWin = minScore != null && score == minScore && allInGame.size > 1

            if (isWin) {
                wins++
                streak++
                if (streak > maxStreak) maxStreak = streak
            } else {
                streak = 0
            }
        }

        val winRate = if (totalGames > 0) wins.toFloat() / totalGames else 0f
        val avgPts = if (totalGames > 0) totalPts.toFloat() / totalGames else 0f

        return PlayerGameKpi(
            playerId = playerId,
            gameType = gameType,
            totalGames = totalGames,
            totalWins = wins,
            winRate = winRate,
            currentWinStreak = streak,
            maxWinStreak = maxStreak,
            totalPoints = totalPts,
            averagePoints = avgPts,
            totalZeros = totalZeros,
            triggerCount = triggerCount
        )
    }
}
