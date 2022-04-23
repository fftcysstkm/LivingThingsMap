package com.demo.android.mapapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.demo.android.mapapp.model.dao.CreatureDao
import com.demo.android.mapapp.model.data.Creature

/**
 * 生き物の情報を保持するViewModel
 */
class CreaturesViewModel(private val creatureDao: CreatureDao) : ViewModel() {

    // 生き物のリスト
//    private val _creatures = MutableLiveData<List<Creature>>()
//    val creatures: LiveData<List<Creature>> get() = _creatures
    val creatures: LiveData<List<Creature>> =
        creatureDao.getCreaturesList().asLiveData()

    /**
     * データソースクラスからテストのお魚リスト取得
     */
//    init {
//        val datasource = DataSource()
//        _creatures.value = datasource.createCreaturesList()
//    }

    /**
     * テストメソッド：生き物のリストのランダムな位置に新たなテストデータを挿入
     */
//    @SuppressLint("NewApi")
//    fun addTestCreature() {
//        val random = Random()
//        val now = LocalDateTime.now()
//        val increasedCreatures = _creatures.value!!.toMutableList()
//        val nextId = (increasedCreatures.size + 1).toLong()
//        val position = random.nextInt(increasedCreatures.size)
////        increasedCreatures.add(
////            position,
////            Creature(creatureId = nextId, type = "魚", "added new お魚$nextId in ViewModel", now)
////        )
//        _creatures.value = increasedCreatures
//    }

    /**
     * テストメソッド：生き物のリストのランダムな位置に新たなテストデータを挿入
     */
//    fun addTestInputCreature(creature: Creature) {
//        val increasedCreatures = _creatures.value!!.toMutableList()
//        val nextId = increasedCreatures.size + 1
//        increasedCreatures.add(creature)
//        _creatures.value = increasedCreatures
//    }

    /**
     * 引数あり(Dao)のViewModelをインスタンス化するのに必要なクラス（ボイラープレート）
     */
    class CreatureViewModelFactory(private val creatureDao: CreatureDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreaturesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreaturesViewModel(creatureDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}