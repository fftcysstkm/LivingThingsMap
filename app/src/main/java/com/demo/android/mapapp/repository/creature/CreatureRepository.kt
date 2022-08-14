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
     * 生き物リストに表示する生き物一覧をカテゴリーIDで取得
     */
    fun getCreaturesByCatId(categoryId: Long): Flow<List<Creature>>

    /**
     * 生き物をIDで取得
     */
    fun getCreatureById(creatureId: Long): Creature

    /**
     * 生き物リストに表示する生き物一覧を追加
     */
    suspend fun addCreature(creature: Creature)

    /**
     * 生き物リストに表示する生き物を更新する
     * @param creatureId 生き物Id
     * @param creatureName 生き物名
     * @param memo 備考
     */
    suspend fun updateCreature(creatureId: Long, creatureName: String, memo: String)

    /**
     * 生き物リストに表示する生き物をIDで削除する
     */
    suspend fun deleteCreatureById(creatureId: Long): Int

    /**
     * 生き物の詳細情報（位置情報や個体数など）を登録
     */
    suspend fun addCreatureDetail(creatureDetail: CreatureDetail)

    /**
     * 生き物詳細情報（位置情報や個体数など）リストを生き物IDで取得
     */
    fun getCreatureDetails(creatureId: Long): Flow<List<CreatureDetail>>

    /**
     * 生き物詳細情報を更新する
     */
    suspend fun updateCreatureDetail(creatureDetail: CreatureDetail): Int

    /**
     * 生き物詳細情報を削除する
     */
    suspend fun deleteCreatureDetail(creatureDetail: CreatureDetail): Int

    /**
     * 生き物詳細情報を生き物IDで削除する
     */
    suspend fun deleteCreatureDetailById(creatureId: Long): Int
}