package com.zerogame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerogame.ZeroApp
import com.zerogame.R
import com.zerogame.data.model.Game
import com.zerogame.data.model.GamePlayer
import com.zerogame.data.model.GameType
import com.zerogame.data.model.Player
import com.zerogame.data.model.PlayerGameKpi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class GameFilter(val labelResId: Int) {
    ALL(R.string.filter_all),
    ZERO(R.string.game_type_zero),
    SKYJO(R.string.game_type_skyjo)
}

enum class StatsPeriod(val labelResId: Int) {
    ALL(R.string.period_all),
    LAST_5(R.string.period_last_5),
    LAST_10(R.string.period_last_10),
    LAST_50(R.string.period_last_50)
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

    val allKpis: StateFlow<List<PlayerGameKpi>> = _playerId.flatMapLatest { id ->
        if (id != null) repository.getKpisByPlayerId(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedGameFilter = MutableStateFlow(GameFilter.ALL)
    val selectedGameFilter: StateFlow<GameFilter> = _selectedGameFilter.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.ALL)
    val selectedPeriod: StateFlow<StatsPeriod> = _selectedPeriod.asStateFlow()

    fun loadPlayer(playerId: Long) {
        _playerId.value = playerId
    }

    fun setGameFilter(filter: GameFilter) {
        _selectedGameFilter.value = filter
    }

    fun setPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
    }

    fun computeKpis(
        gamePlayers: List<GamePlayer>,
        games: List<Game>,
        gameFilter: GameFilter,
        period: StatsPeriod,
        playerId: Long
    ): PlayerGameKpi? {
        val gameDateMap = games.associate { it.id to it.createdAt }
        val gameTypeMap = games.associate { it.id to it.gameType }

        val typeFiltered = when (gameFilter) {
            GameFilter.ALL -> gamePlayers
            GameFilter.ZERO -> gamePlayers.filter { gp -> gameTypeMap[gp.gameId] == GameType.ZERO }
            GameFilter.SKYJO -> gamePlayers.filter { gp -> gameTypeMap[gp.gameId] == GameType.SKYJO }
        }

        val sorted = typeFiltered.sortedByDescending { gameDateMap[it.gameId] ?: 0L }

        val periodFiltered = when (period) {
            StatsPeriod.ALL -> sorted
            StatsPeriod.LAST_5 -> sorted.take(5)
            StatsPeriod.LAST_10 -> sorted.take(10)
            StatsPeriod.LAST_50 -> sorted.take(50)
        }

        if (periodFiltered.isEmpty()) return null

        val totalGames = periodFiltered.size
        var wins = 0
        var totalPts = 0
        var totalZeros = 0
        var triggerCount = 0
        var streak = 0
        var maxStreak = 0

        for (gp in periodFiltered) {
            totalPts += gp.totalScore
            totalZeros += gp.extras["zerosAchieved"]?.toIntOrNull() ?: 0
            triggerCount += if (gp.extras["isTrigger"] == "true") 1 else 0

            val allInGame = gamePlayers.filter { it.gameId == gp.gameId }
            val minScore = allInGame.minOfOrNull { it.totalScore }
            val isWin = minScore != null && gp.totalScore == minScore && allInGame.size > 1

            if (isWin) {
                wins++
                streak++
                if (streak > maxStreak) maxStreak = streak
            } else {
                streak = 0
            }
        }

        val winRate = if (totalGames > 0) wins.toFloat() / totalGames else 0f
        val avgPts = if (totalGames > 0) totalPts.toFloat() / totalGames else 0f

        return PlayerGameKpi(
            playerId = playerId,
            gameType = when (gameFilter) {
                GameFilter.ALL -> GameType.ZERO
                GameFilter.ZERO -> GameType.ZERO
                GameFilter.SKYJO -> GameType.SKYJO
            },
            totalGames = totalGames,
            totalWins = wins,
            winRate = winRate,
            currentWinStreak = streak,
            maxWinStreak = maxStreak,
            totalPoints = totalPts,
            averagePoints = avgPts,
            totalZeros = totalZeros,
            triggerCount = triggerCount
        )
    }

    fun computePerGameKpis(
        gamePlayers: List<GamePlayer>,
        games: List<Game>,
        period: StatsPeriod,
        playerId: Long
    ): Map<GameType, PlayerGameKpi> {
        val result = mutableMapOf<GameType, PlayerGameKpi>()
        for (type in GameType.values()) {
            val kpi = computeKpis(gamePlayers, games, when (type) {
                GameType.ZERO -> GameFilter.ZERO
                GameType.SKYJO -> GameFilter.SKYJO
            }, period, playerId)
            if (kpi != null && kpi.totalGames > 0) {
                result[type] = kpi
            }
        }
        return result
    }
}
