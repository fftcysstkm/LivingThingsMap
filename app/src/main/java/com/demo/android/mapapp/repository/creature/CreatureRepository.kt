package com.demo.android.mapapp.repository.creature

import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.model.creature.CreatureDetail
import kotlinx.coroutines.flow.Flow

/**
 * 生き物のリポジトリインタフェース
 */
interface CreatureRepository {
    /**
     * 生き物リストに表示する生き物一覧を取得
     */
    fun getCreatures(): Flow<List<Creature>>

    /**
     * 生き物をIDで取得
     */
    suspend fun getCreatureById(creatureId: Long): Creature

    /**
     * 生き物リストに表示する生き物一覧を追加
     */
    suspend fun addCreature(creature: Creature)

    /**
     * 生き物の詳細情報（位置情報や個体数など）を登録
     */
    suspend fun addCreatureDetail(creatureDetail: CreatureDetail)
}