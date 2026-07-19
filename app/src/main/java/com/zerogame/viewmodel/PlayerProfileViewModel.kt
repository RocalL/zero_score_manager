package com.zerogame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerogame.ZeroApp
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.Player
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ZeroApp).repository

    private val _playerId = MutableStateFlow<Long?>(null)

    val player: StateFlow<Player?> = _playerId.flatMapLatest { id ->
        if (id != null) repository.getPlayerByIdFlow(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val gamePlayers: StateFlow<List<GamePlayer>> = _playerId.flatMapLatest { id ->
        if (id != null) repository.getGamePlayersByPlayerId(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalGames = MutableStateFlow(0)
    val totalGames: StateFlow<Int> = _totalGames.asStateFlow()

    private val _totalWins = MutableStateFlow(0)
    val totalWins: StateFlow<Int> = _totalWins.asStateFlow()

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    private val _averageScore = MutableStateFlow(0.0)
    val averageScore: StateFlow<Double> = _averageScore.asStateFlow()

    private val _totalZeros = MutableStateFlow(0)
    val totalZeros: StateFlow<Int> = _totalZeros.asStateFlow()

    fun loadPlayer(playerId: Long) {
        _playerId.value = playerId
    }

    fun computeStats(gamePlayers: List<GamePlayer>) {
        _totalGames.value = gamePlayers.size
        _totalScore.value = gamePlayers.sumOf { it.totalScore }
        _averageScore.value = if (gamePlayers.isNotEmpty()) {
            gamePlayers.map { it.totalScore }.average()
        } else 0.0
        _totalZeros.value = gamePlayers.sumOf { it.zerosAchieved }

        viewModelScope.launch {
            var wins = 0
            for (gp in gamePlayers) {
                val allPlayersInGame = repository.getGamePlayersForGameId(gp.gameId)
                val lowestScore = allPlayersInGame.minOfOrNull { it.totalScore }
                if (lowestScore != null && gp.totalScore == lowestScore && allPlayersInGame.size > 1) {
                    wins++
                }
            }
            _totalWins.value = wins
        }
    }
}
