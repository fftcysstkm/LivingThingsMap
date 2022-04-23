package com.demo.android.mapapp.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.demo.android.mapapp.model.data.Creature
import kotlinx.coroutines.flow.Flow

/**
 * 生き物Dao
 */
@Dao
interface CreatureDao {

    /**
     * 生き物リストに表示する生き物を取得
     */
    @Query(
        "SELECT creatureId, typeId, creatureName " +
                "FROM T_CREATURE " +
                "ORDER BY typeId, creatureId"
    )
    fun getCreaturesList(): Flow<List<Creature>>
}