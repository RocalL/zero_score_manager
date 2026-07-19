package com.zerogame.game

import com.zerogame.data.model.GameType

class SkyjoEngine : GameEngine {
    override val gameType = GameType.SKYJO
    override val minPlayers = 2
    override val maxPlayers = 8
    override val defaultTargetScore = 100

    override fun createGameConfig(targetScore: Int): Map<String, String> {
        return mapOf("targetScore" to targetScore.toString())
    }

    override fun getTargetScore(config: Map<String, String>): Int {
        return config["targetScore"]?.toIntOrNull() ?: defaultTargetScore
    }

    override fun isGameEndConditionMet(cumulativeScores: List<Int>, config: Map<String, String>): Boolean {
        val target = getTargetScore(config)
        return cumulativeScores.any { it >= target }
    }

    override fun formatRoundScore(score: Int): String = "$score pts"

    fun hasTriggerPenalty(triggerPlayerScore: Int, allScores: List<Int>): Boolean {
        val minScore = allScores.minOrNull() ?: 0
        return triggerPlayerScore > minScore
    }

    fun calculateRoundScore(
        roundScore: Int,
        triggerPlayerScore: Int,
        allScores: List<Int>
    ): Int {
        return if (hasTriggerPenalty(triggerPlayerScore, allScores)) {
            roundScore * 2
        } else {
            roundScore
        }
    }
}
