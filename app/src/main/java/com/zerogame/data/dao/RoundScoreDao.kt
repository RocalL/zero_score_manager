package com.zerogame.data.dao

import androidx.room.*
import com.zerogame.data.model.RoundScore
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundScoreDao {
    @Query("SELECT * FROM round_scores WHERE gameId = :gameId ORDER BY roundNumber ASC")
    fun getRoundScoresByGameId(gameId: Long): Flow<List<RoundScore>>

    @Query("SELECT * FROM round_scores WHERE gameId = :gameId AND playerId = :playerId ORDER BY roundNumber ASC")
    fun getRoundScoresByGameAndPlayer(gameId: Long, playerId: Long): Flow<List<RoundScore>>

    @Query("SELECT MAX(roundNumber) FROM round_scores WHERE gameId = :gameId")
    suspend fun getMaxRound(gameId: Long): Int?

    @Insert
    suspend fun insertRoundScore(roundScore: RoundScore): Long

    @Insert
    suspend fun insertRoundScores(roundScores: List<RoundScore>)

    @Query("DELETE FROM round_scores WHERE gameId = :gameId")
    suspend fun deleteRoundScoresByGameId(gameId: Long)
}
