package com.demo.android.mapapp.repository.creature

import com.demo.android.mapapp.model.creature.Creature
import kotlinx.coroutines.flow.Flow

interface CreatureRepository {
    /**
     * 生き物リストに表示する生き物一覧を取得
     */
    fun getCreatures(): Flow<List<Creature>>

    /**
     * 生き物リストに表示する生き物一覧を追加
     */
    suspend fun addCreature(creature: Creature)
}