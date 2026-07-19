package com.zerogame.data.dao

import androidx.room.*
import com.zerogame.data.model.GamePlayer
import kotlinx.coroutines.flow.Flow

@Dao
interface GamePlayerDao {
    @Query("SELECT * FROM game_players WHERE gameId = :gameId")
    fun getGamePlayersByGameId(gameId: Long): Flow<List<GamePlayer>>

    @Query("SELECT * FROM game_players WHERE gameId = :gameId")
    suspend fun getGamePlayersByGameIdSync(gameId: Long): List<GamePlayer>

    @Query("SELECT * FROM game_players WHERE gameId = :gameId AND playerId = :playerId")
    suspend fun getGamePlayer(gameId: Long, playerId: Long): GamePlayer?

    @Insert
    suspend fun insertGamePlayer(gamePlayer: GamePlayer): Long

    @Insert
    suspend fun insertGamePlayers(gamePlayers: List<GamePlayer>)

    @Update
    suspend fun updateGamePlayer(gamePlayer: GamePlayer)

    @Query("UPDATE game_players SET totalScore = totalScore + :score, roundsPlayed = roundsPlayed + 1, zerosAchieved = zerosAchieved + CASE WHEN :achievedZero THEN 1 ELSE 0 END WHERE gameId = :gameId AND playerId = :playerId")
    suspend fun updateScore(gameId: Long, playerId: Long, score: Int, achievedZero: Boolean)

    @Query("SELECT * FROM game_players WHERE gameId = :gameId ORDER BY totalScore ASC LIMIT 1")
    suspend fun getWinner(gameId: Long): GamePlayer?

    @Query("SELECT * FROM game_players WHERE playerId = :playerId")
    fun getGamePlayersByPlayerId(playerId: Long): Flow<List<GamePlayer>>

    @Query("SELECT * FROM game_players WHERE playerId = :playerId")
    suspend fun getGamePlayersByPlayerIdSync(playerId: Long): List<GamePlayer>
}
