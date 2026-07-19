package com.zerogame.game

import com.zerogame.data.model.GameType

interface GameEngine {
    val gameType: GameType
    val minPlayers: Int
    val maxPlayers: Int
    val defaultTargetScore: Int

    fun createGameConfig(targetScore: Int = defaultTargetScore): Map<String, String>
    fun getTargetScore(config: Map<String, String>): Int
    fun isGameEndConditionMet(cumulativeScores: List<Int>, config: Map<String, String>): Boolean
    fun formatRoundScore(score: Int): String
}
