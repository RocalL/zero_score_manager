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

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ZeroApp).repository

    private val _currentGameId = MutableStateFlow<Long?>(null)
    val currentGameId: StateFlow<Long?> = _currentGameId.asStateFlow()

    private val _selectedPlayerIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedPlayerIds: StateFlow<List<Long>> = _selectedPlayerIds.asStateFlow()

    val allPlayers = repository.getAllPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentGamePlayers: StateFlow<List<GamePlayer>> = _currentGameId.flatMapLatest { gameId ->
        if (gameId != null) {
            repository.getGamePlayersByGameId(gameId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentRoundScores: StateFlow<List<RoundScore>> = _currentGameId.flatMapLatest { gameId ->
        if (gameId != null) {
            repository.getRoundScoresByGameId(gameId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    val totalRounds: StateFlow<Int> = _selectedPlayerIds.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _gameFinished = MutableStateFlow(false)
    val gameFinished: StateFlow<Boolean> = _gameFinished.asStateFlow()

    private val _scores = MutableStateFlow<Map<Long, String>>(emptyMap())
    val scores: StateFlow<Map<Long, String>> = _scores.asStateFlow()

    private val _zeros = MutableStateFlow<Set<Long>>(emptySet())
    val zeros: StateFlow<Set<Long>> = _zeros.asStateFlow()

    fun togglePlayerSelection(playerId: Long) {
        _selectedPlayerIds.update { current ->
            if (playerId in current) current - playerId else current + playerId
        }
    }

    fun startGame() {
        viewModelScope.launch {
            val game = Game()
            val gameId = repository.insertGame(game)
            repository.addPlayersToGame(gameId, _selectedPlayerIds.value)
            _currentGameId.value = gameId
            _currentRound.value = 1
            _scores.value = _selectedPlayerIds.value.associateWith { "0" }
            _zeros.value = emptySet()
            _gameFinished.value = false
        }
    }

    fun updateScore(playerId: Long, score: String) {
        _scores.update { current ->
            current + (playerId to score)
        }
    }

    fun toggleZero(playerId: Long) {
        _zeros.update { current ->
            if (playerId in current) current - playerId else current + playerId
        }
    }

    fun submitRound() {
        viewModelScope.launch {
            val gameId = _currentGameId.value ?: return@launch
            val round = _currentRound.value
            val totalRounds = _selectedPlayerIds.value.size

            val scoreList = _scores.value.map { (playerId, scoreStr) ->
                val score = scoreStr.toIntOrNull() ?: 0
                playerId to score
            }

            repository.addRoundScores(gameId, round, scoreList, _zeros.value.toList())

            if (round >= totalRounds) {
                repository.finishGame(gameId, totalRounds)
                _gameFinished.value = true
            } else {
                _currentRound.value = round + 1
                _scores.value = _selectedPlayerIds.value.associateWith { "0" }
                _zeros.value = emptySet()
            }
        }
    }

    fun endGame() {
        viewModelScope.launch {
            val gameId = _currentGameId.value ?: return@launch
            val roundsPlayed = _currentRound.value - 1
            if (roundsPlayed > 0) {
                repository.finishGame(gameId, roundsPlayed)
            }
            _currentGameId.value = null
            _gameFinished.value = true
        }
    }

    fun getGamePlayersForGameId(gameId: Long): Flow<List<GamePlayer>> {
        return repository.getGamePlayersByGameId(gameId)
    }

    fun getRoundScoresForGameId(gameId: Long): Flow<List<RoundScore>> {
        return repository.getRoundScoresByGameId(gameId)
    }
}
