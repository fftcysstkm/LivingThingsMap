package com.demo.android.mapapp.repository.creature

import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.model.creature.CreatureDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreatureRepositoryImpl @Inject constructor(private val dao: CreatureDao) :
    CreatureRepository {

    /**
     * 生き物リストに表示する生き物一覧を取得
     */
    override fun getCreatures(): Flow<List<Creature>> {
        return dao.getCreatures()
    }

    /**
     * 生き物をIDで取得
     */
    override suspend fun getCreatureById(creatureId: Long): Creature {
        return dao.getCreatureById(creatureId)
    }

    /**
     * 生き物リストに表示する生き物一覧を追加
     */
    override suspend fun addCreature(creature: Creature) {
        dao.addCreature(creature)
    }
}