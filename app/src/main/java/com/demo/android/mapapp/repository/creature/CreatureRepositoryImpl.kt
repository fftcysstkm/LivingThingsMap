package com.demo.android.mapapp.repository.creature

import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.model.creature.CreatureDao
import com.demo.android.mapapp.model.creature.CreatureDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 生き物のリポジトリ（実装）
 */
class CreatureRepositoryImpl @Inject constructor(private val dao: CreatureDao) :
    CreatureRepository {

    /**
     * 生き物リストに表示する生き物一覧を取得
     */
    override fun getCreatures(): Flow<List<Creature>> {
        return dao.getCreatures()
    }
    /**
     * 生き物リストに表示する生き物一覧をカテゴリーIDで取得
     */
    override fun getCreaturesByCatId(categoryId: Long): Flow<List<Creature>> {
        return dao.getCreaturesByCatId(categoryId)
    }

    /**
     * 生き物をIDで取得
     */
    override fun getCreatureById(creatureId: Long): Creature {
        return dao.getCreatureById(creatureId)
    }

    /**
     * 生き物リストに表示する生き物一覧を追加
     */
    override suspend fun addCreature(creature: Creature) {
        dao.addCreature(creature)
    }

    /**
     * 生き物の詳細情報（位置情報や個体数など）を登録
     */
    override suspend fun addCreatureDetail(creatureDetail: CreatureDetail) {
        dao.addCreatureDetail(creatureDetail)
    }

    /**
     * 生き物の詳細情報（位置情報や個体数など）を登録
     */
    override fun getCreatureDetails(creatureId: Long): Flow<List<CreatureDetail>> {
        return dao.getCreatureDetails(creatureId)
    }

    /**
     * 生き物の詳細情報（位置情報や個体数など）を更新
     */
    override suspend fun updateCreatureDetail(creatureDetail: CreatureDetail): Int {
        return dao.updateCreatureDetail(creatureDetail)
    }

    /**
     * 生き物の詳細情報（位置情報や個体数など）を削除
     */
    override suspend fun deleteCreatureDetail(creatureDetail: CreatureDetail): Int {
        return dao.deleteCreatureDetail(creatureDetail)
    }
}