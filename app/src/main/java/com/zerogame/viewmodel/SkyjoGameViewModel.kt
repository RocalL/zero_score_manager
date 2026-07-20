package com.zerogame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerogame.ZeroApp
import com.zerogame.data.model.Game
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.GameType
import com.zerogame.data.model.RoundScore
import com.zerogame.game.GameEngineFactory
import com.zerogame.game.KpiComputer
import com.zerogame.game.SkyjoEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SkyjoGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ZeroApp).repository
    private val engine = GameEngineFactory.getSkyjoEngine()

    private val _currentGameId = MutableStateFlow<Long?>(null)
    val currentGameId: StateFlow<Long?> = _currentGameId.asStateFlow()

    private val _selectedPlayerIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedPlayerIds: StateFlow<List<Long>> = _selectedPlayerIds.asStateFlow()

    val allPlayers = repository.getAllPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentGamePlayers: StateFlow<List<GamePlayer>> = _currentGameId.flatMapLatest { gameId ->
        if (gameId != null) repository.getGamePlayersByGameId(gameId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentRoundScores: StateFlow<List<RoundScore>> = _currentGameId.flatMapLatest { gameId ->
        if (gameId != null) repository.getRoundScoresByGameId(gameId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _gameFinished = MutableStateFlow(false)
    val gameFinished: StateFlow<Boolean> = _gameFinished.asStateFlow()

    private val _lastFinishedGameId = MutableStateFlow<Long?>(null)
    val lastFinishedGameId: StateFlow<Long?> = _lastFinishedGameId.asStateFlow()

    private val _scores = MutableStateFlow<Map<Long, String>>(emptyMap())
    val scores: StateFlow<Map<Long, String>> = _scores.asStateFlow()

    private val _triggerPlayerId = MutableStateFlow<Long?>(null)
    val triggerPlayerId: StateFlow<Long?> = _triggerPlayerId.asStateFlow()

    private val _columnsEliminated = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val columnsEliminated: StateFlow<Map<Long, Int>> = _columnsEliminated.asStateFlow()

    private var targetScore = engine.defaultTargetScore

    fun togglePlayerSelection(playerId: Long) {
        _selectedPlayerIds.update { current ->
            if (playerId in current) current - playerId else current + playerId
        }
    }

    fun startGame() {
        viewModelScope.launch {
            targetScore = engine.defaultTargetScore
            val config = engine.createGameConfig(targetScore)
            val game = Game(gameType = GameType.SKYJO, config = config)
            val gameId = repository.insertGame(game)
            repository.addPlayersToGame(gameId, _selectedPlayerIds.value)
            _currentGameId.value = gameId
            _currentRound.value = 1
            _scores.value = _selectedPlayerIds.value.associateWith { "0" }
            _triggerPlayerId.value = null
            _columnsEliminated.value = emptyMap()
            _gameFinished.value = false
        }
    }

    fun updateScore(playerId: Long, score: String) {
        _scores.update { it + (playerId to score) }
    }

    fun setScoreFromCards(playerId: Long, cardValues: List<Int>) {
        val score = cardValues.sum()
        _scores.update { it + (playerId to score.toString()) }
    }

    fun setTriggerPlayer(playerId: Long) {
        _triggerPlayerId.value = playerId
    }

    fun updateColumnsEliminated(playerId: Long, columns: Int) {
        _columnsEliminated.update { it + (playerId to columns) }
    }

    fun submitRound() {
        viewModelScope.launch {
            val gameId = _currentGameId.value ?: return@launch
            val round = _currentRound.value

            val scoreList = _scores.value.map { (playerId, scoreStr) ->
                val score = scoreStr.toIntOrNull() ?: 0
                playerId to score
            }

            val extrasMap = mutableMapOf<Long, Map<String, String>>()
            val triggerId = _triggerPlayerId.value
            if (triggerId != null) {
                extrasMap[triggerId] = (extrasMap[triggerId] ?: emptyMap()) + ("isTrigger" to "true")
            }

            _columnsEliminated.value.forEach { (playerId, cols) ->
                extrasMap[playerId] = (extrasMap[playerId] ?: emptyMap()) + ("columnsEliminated" to cols.toString())
            }

            repository.addRoundScores(gameId, round, scoreList, extrasMap)

            val updatedGamePlayers = repository.getGamePlayersByGameIdSync(gameId)
            val cumulativeScores = updatedGamePlayers.map { it.totalScore }

            if (engine.isGameEndConditionMet(cumulativeScores, engine.createGameConfig(targetScore))) {
                repository.finishGame(gameId, round)
                recomputeKpis(gameId)
                _lastFinishedGameId.value = gameId
                _gameFinished.value = true
            } else {
                _currentRound.value = round + 1
                _scores.value = _selectedPlayerIds.value.associateWith { "0" }
                _triggerPlayerId.value = null
                _columnsEliminated.value = emptyMap()
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
            recomputeKpis(gameId)
            _lastFinishedGameId.value = gameId
            _gameFinished.value = true
        }
    }

    private suspend fun recomputeKpis(gameId: Long) {
        val game = repository.getGameById(gameId) ?: return
        val playerIds = repository.getGamePlayersByGameIdSync(gameId).map { it.playerId }

        val allGamesOfType = repository.getGamesByTypeSync(game.gameType)
        val allGpsOfType = repository.getGamePlayersByGameIdForType(game.gameType)

        for (playerId in playerIds) {
            val kpi = KpiComputer.computeAfterGame(
                playerId = playerId,
                gameType = game.gameType,
                finishedGame = game,
                allGamePlayersInGame = repository.getGamePlayersByGameIdSync(gameId),
                allGamesOfType = allGamesOfType,
                allGamePlayersOfType = allGpsOfType
            )
            repository.upsertKpi(kpi)
        }
    }

    fun resetAfterGameFinished() {
        _currentGameId.value = null
        _lastFinishedGameId.value = null
        _scores.value = emptyMap()
        _currentRound.value = 1
        _gameFinished.value = false
        _triggerPlayerId.value = null
        _columnsEliminated.value = emptyMap()
    }
}
