package com.zerogame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerogame.ZeroApp
import com.zerogame.data.model.Game
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.Player
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class StatsPeriod(val label: String) {
    ALL("All"),
    LAST_5("Last 5"),
    LAST_10("Last 10"),
    LAST_50("Last 50")
}

class PlayerProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ZeroApp).repository

    private val _playerId = MutableStateFlow<Long?>(null)

    val player: StateFlow<Player?> = _playerId.flatMapLatest { id ->
        if (id != null) repository.getPlayerByIdFlow(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allGamePlayers: StateFlow<List<GamePlayer>> = _playerId.flatMapLatest { id ->
        if (id != null) repository.getGamePlayersByPlayerId(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGames: StateFlow<List<Game>> = repository.getAllGames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.ALL)
    val selectedPeriod: StateFlow<StatsPeriod> = _selectedPeriod.asStateFlow()

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

    private val _totalRoundsPlayed = MutableStateFlow(0)
    val totalRoundsPlayed: StateFlow<Int> = _totalRoundsPlayed.asStateFlow()

    private val _gamePlayers = MutableStateFlow<List<GamePlayer>>(emptyList())
    val gamePlayers: StateFlow<List<GamePlayer>> = _gamePlayers.asStateFlow()

    fun loadPlayer(playerId: Long) {
        _playerId.value = playerId
    }

    fun setPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
    }

    fun computeStats(gamePlayers: List<GamePlayer>, games: List<Game>) {
        val gameDateMap = games.associate { it.id to it.createdAt }

        val sorted = gamePlayers.sortedByDescending { gameDateMap[it.gameId] ?: 0L }

        val filtered = when (_selectedPeriod.value) {
            StatsPeriod.ALL -> sorted
            StatsPeriod.LAST_5 -> sorted.take(5)
            StatsPeriod.LAST_10 -> sorted.take(10)
            StatsPeriod.LAST_50 -> sorted.take(50)
        }

        _gamePlayers.value = filtered
        _totalGames.value = filtered.size
        _totalScore.value = filtered.sumOf { it.totalScore }
        _averageScore.value = if (filtered.isNotEmpty()) {
            filtered.map { it.totalScore }.average()
        } else 0.0
        _totalZeros.value = filtered.sumOf { it.zerosAchieved }
        _totalRoundsPlayed.value = filtered.sumOf { it.roundsPlayed }

        viewModelScope.launch {
            var wins = 0
            for (gp in filtered) {
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
