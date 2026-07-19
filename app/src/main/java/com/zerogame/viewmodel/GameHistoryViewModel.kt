package com.zerogame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerogame.ZeroApp
import com.zerogame.data.model.Game
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.Player
import com.zerogame.data.model.RoundScore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ZeroApp).repository

    val allGames = repository.getAllGames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getGamePlayers(gameId: Long): Flow<List<GamePlayer>> {
        return repository.getGamePlayersByGameId(gameId)
    }

    fun getPlayer(playerId: Long): Flow<Player?> {
        return repository.getPlayerByIdFlow(playerId)
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }

    suspend fun getPlayerName(playerId: Long): String? {
        return repository.getPlayerById(playerId)?.name
    }
}
