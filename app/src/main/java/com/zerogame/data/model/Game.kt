package com.zerogame.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    foreignKeys = []
)
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val numberOfRounds: Int = 0,
    val isFinished: Boolean = false
)
