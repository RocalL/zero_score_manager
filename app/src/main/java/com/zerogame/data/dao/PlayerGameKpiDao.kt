package com.zerogame.data.dao

import androidx.room.*
import com.zerogame.data.model.GameType
import com.zerogame.data.model.PlayerGameKpi
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerGameKpiDao {
    @Query("SELECT * FROM player_game_kpis WHERE playerId = :playerId")
    fun getKpisByPlayerId(playerId: Long): Flow<List<PlayerGameKpi>>

    @Query("SELECT * FROM player_game_kpis WHERE playerId = :playerId AND gameType = :gameType")
    suspend fun getKpi(playerId: Long, gameType: GameType): PlayerGameKpi?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(kpi: PlayerGameKpi)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(kpis: List<PlayerGameKpi>)

    @Query("DELETE FROM player_game_kpis WHERE playerId = :playerId")
    suspend fun deleteByPlayerId(playerId: Long)
}
