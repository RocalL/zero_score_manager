package com.zerogame.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameType: GameType = GameType.ZERO,
    val createdAt: Long = System.currentTimeMillis(),
    val numberOfRounds: Int = 0,
    val isFinished: Boolean = false,
    val config: Map<String, String> = emptyMap()
)
