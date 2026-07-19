package com.zerogame

import android.app.Application
import com.zerogame.data.AppDatabase
import com.zerogame.data.repository.ZeroRepository

class ZeroApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        ZeroRepository(
            database.playerDao(),
            database.gameDao(),
            database.gamePlayerDao(),
            database.roundScoreDao()
        )
    }
}
