package com.zerogame.data.repository

import com.zerogame.data.dao.GameDao
import com.zerogame.data.dao.GamePlayerDao
import com.zerogame.data.dao.PlayerDao
import com.zerogame.data.dao.PlayerGameKpiDao
import com.zerogame.data.dao.RoundScoreDao
import com.zerogame.data.model.*
import kotlinx.coroutines.flow.Flow

class ZeroRepository(
    private val playerDao: PlayerDao,
    private val gameDao: GameDao,
    private val gamePlayerDao: GamePlayerDao,
    private val roundScoreDao: RoundScoreDao,
    private val playerGameKpiDao: PlayerGameKpiDao
) {
    // Player operations
    fun getAllPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    suspend fun getPlayerById(id: Long): Player? = playerDao.getPlayerById(id)

    fun getPlayerByIdFlow(id: Long): Flow<Player?> = playerDao.getPlayerByIdFlow(id)

    suspend fun insertPlayer(player: Player): Long = playerDao.insertPlayer(player)

    suspend fun updatePlayer(player: Player) = playerDao.updatePlayer(player)

    suspend fun deletePlayer(player: Player) = playerDao.deletePlayer(player)

    // Game operations
    fun getAllGames(): Flow<List<Game>> = gameDao.getAllGames()

    fun getGamesByType(gameType: GameType): Flow<List<Game>> = gameDao.getGamesByType(gameType)

    suspend fun getGameById(id: Long): Game? = gameDao.getGameById(id)

    fun getGameByIdFlow(id: Long): Flow<Game?> = gameDao.getGameByIdFlow(gameId = id)

    suspend fun insertGame(game: Game): Long = gameDao.insertGame(game)

    suspend fun updateGame(game: Game) = gameDao.updateGame(game)

    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)

    suspend fun finishGame(gameId: Long, rounds: Int) = gameDao.finishGame(gameId, rounds)

    // GamePlayer operations
    fun getGamePlayersByGameId(gameId: Long): Flow<List<GamePlayer>> =
        gamePlayerDao.getGamePlayersByGameId(gameId)

    suspend fun getGamePlayersByGameIdSync(gameId: Long): List<GamePlayer> =
        gamePlayerDao.getGamePlayersByGameIdSync(gameId)

    suspend fun getGamePlayer(gameId: Long, playerId: Long): GamePlayer? =
        gamePlayerDao.getGamePlayer(gameId, playerId)

    suspend fun addPlayersToGame(gameId: Long, playerIds: List<Long>) {
        val gamePlayers = playerIds.map { playerId ->
            GamePlayer(gameId = gameId, playerId = playerId)
        }
        gamePlayerDao.insertGamePlayers(gamePlayers)
    }

    suspend fun updateGamePlayerScore(gameId: Long, playerId: Long, score: Int) {
        gamePlayerDao.updateScore(gameId, playerId, score)
    }

    suspend fun updateGamePlayerExtras(gameId: Long, playerId: Long, extras: Map<String, String>) {
        val gp = gamePlayerDao.getGamePlayer(gameId, playerId) ?: return
        gamePlayerDao.updateGamePlayer(gp.copy(extras = extras))
    }

    suspend fun getGameWinner(gameId: Long): GamePlayer? = gamePlayerDao.getWinner(gameId)

    // RoundScore operations
    fun getRoundScoresByGameId(gameId: Long): Flow<List<RoundScore>> =
        roundScoreDao.getRoundScoresByGameId(gameId)

    fun getRoundScoresByGameAndPlayer(gameId: Long, playerId: Long): Flow<List<RoundScore>> =
        roundScoreDao.getRoundScoresByGameAndPlayer(gameId, playerId)

    suspend fun getMaxRound(gameId: Long): Int = roundScoreDao.getMaxRound(gameId) ?: 0

    suspend fun addRoundScores(gameId: Long, roundNumber: Int, scores: List<Pair<Long, Int>>, extras: Map<Long, Map<String, String>> = emptyMap()) {
        val roundScores = scores.map { (playerId, score) ->
            RoundScore(
                gameId = gameId,
                playerId = playerId,
                roundNumber = roundNumber,
                score = score,
                extras = extras[playerId] ?: emptyMap()
            )
        }
        roundScoreDao.insertRoundScores(roundScores)

        scores.forEach { (playerId, score) ->
            gamePlayerDao.updateScore(gameId, playerId, score)
        }
    }

    // Stats
    fun getGamePlayersByPlayerId(playerId: Long): Flow<List<GamePlayer>> =
        gamePlayerDao.getGamePlayersByPlayerId(playerId)

    suspend fun getGamePlayersByPlayerIdSync(playerId: Long): List<GamePlayer> =
        gamePlayerDao.getGamePlayersByPlayerIdSync(playerId)

    suspend fun getGamePlayersForGameId(gameId: Long): List<GamePlayer> =
        gamePlayerDao.getGamePlayersByGameIdSync(gameId)

    // KPI operations
    fun getKpisByPlayerId(playerId: Long): Flow<List<PlayerGameKpi>> =
        playerGameKpiDao.getKpisByPlayerId(playerId)

    suspend fun getKpi(playerId: Long, gameType: GameType): PlayerGameKpi? =
        playerGameKpiDao.getKpi(playerId, gameType)

    suspend fun upsertKpi(kpi: PlayerGameKpi) =
        playerGameKpiDao.upsert(kpi)

    suspend fun upsertAllKpis(kpis: List<PlayerGameKpi>) =
        playerGameKpiDao.upsertAll(kpis)

    // Helper for KPI computation
    suspend fun getGamesByTypeSync(gameType: GameType): List<Game> =
        gameDao.getGamesByTypeSync(gameType)

    suspend fun getGamePlayersByGameIdForType(gameType: GameType): List<GamePlayer> {
        val games = gameDao.getGamesByTypeSync(gameType)
        val allGps = mutableListOf<GamePlayer>()
        for (game in games) {
            allGps.addAll(gamePlayerDao.getGamePlayersByGameIdSync(game.id))
        }
        return allGps
    }
}
