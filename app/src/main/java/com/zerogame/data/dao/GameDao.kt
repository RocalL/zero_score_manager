package com.zerogame.data.dao

import androidx.room.*
import com.zerogame.data.model.Game
import com.zerogame.data.model.GameType
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY createdAt DESC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE gameType = :gameType ORDER BY createdAt DESC")
    fun getGamesByType(gameType: GameType): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE gameType = :gameType ORDER BY createdAt DESC")
    suspend fun getGamesByTypeSync(gameType: GameType): List<Game>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): Game?

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameByIdFlow(gameId: Long): Flow<Game?>

    @Insert
    suspend fun insertGame(game: Game): Long

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("UPDATE games SET isFinished = 1, numberOfRounds = :rounds WHERE id = :gameId")
    suspend fun finishGame(gameId: Long, rounds: Int)
}
