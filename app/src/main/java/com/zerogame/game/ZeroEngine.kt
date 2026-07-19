package com.zerogame.game

import com.zerogame.data.model.GameType

class ZeroEngine : GameEngine {
    override val gameType = GameType.ZERO
    override val minPlayers = 2
    override val maxPlayers = 7
    override val defaultTargetScore = 0

    override fun createGameConfig(targetScore: Int): Map<String, String> {
        return emptyMap()
    }

    override fun getTargetScore(config: Map<String, String>): Int = 0

    override fun isGameEndConditionMet(cumulativeScores: List<Int>, config: Map<String, String>): Boolean {
        return false
    }

    override fun formatRoundScore(score: Int): String = "$score pts"
}
