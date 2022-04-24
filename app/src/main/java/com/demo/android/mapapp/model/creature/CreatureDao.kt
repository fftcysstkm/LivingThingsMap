package com.demo.android.mapapp.model.creature

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomWarnings
import kotlinx.coroutines.flow.Flow

/**
 * 生き物Dao
 */
@Dao
interface CreatureDao {

    /**
     * 生き物リストに表示する生き物を取得
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query(
        "SELECT creatureId, typeId, creatureName " +
                "FROM T_CREATURE " +
                "ORDER BY typeId, creatureId"
    )
    fun getCreatures(): Flow<List<Creature>>

    /**
     * 生き物リストに表示する生き物を登録
     */
    @Insert
    suspend fun addCreatures(creature: Creature)
}