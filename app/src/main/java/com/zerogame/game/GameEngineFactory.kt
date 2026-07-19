package com.zerogame.game

import com.zerogame.data.model.GameType

object GameEngineFactory {
    private val engines = mapOf(
        GameType.ZERO to ZeroEngine(),
        GameType.SKYJO to SkyjoEngine()
    )

    fun getEngine(gameType: GameType): GameEngine {
        return engines[gameType] ?: throw IllegalArgumentException("Unknown game type: $gameType")
    }

    fun getSkyjoEngine(): SkyjoEngine = engines[GameType.SKYJO] as SkyjoEngine
}
