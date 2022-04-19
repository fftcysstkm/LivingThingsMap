package com.demo.android.mapapp.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.android.mapapp.data.Creature
import com.demo.android.mapapp.data.DataSource
import java.time.LocalDateTime
import java.util.*

/**
 * 生き物の情報を保持するViewModel
 */
class CreaturesViewModel : ViewModel() {

    // 生き物のリスト
    private val _creatures = MutableLiveData<List<Creature>>()
    val creatures: LiveData<List<Creature>> get() = _creatures

    /**
     * データソースクラスからテストのお魚リスト取得
     */
    init {
        val datasource = DataSource()
        _creatures.value = datasource.createCreaturesList()
    }

    /**
     * テストメソッド：生き物のリストのランダムな位置に新たなテストデータを挿入
     */
    @SuppressLint("NewApi")
    fun addTestCreature() {
        val random = Random()
        val now = LocalDateTime.now()
        val increasedCreatures = _creatures.value!!.toMutableList()
        val nextId = increasedCreatures.size + 1
        val position = random.nextInt(increasedCreatures.size)
        increasedCreatures.add(
            position,
            Creature(id = nextId, type = "魚", "added new お魚$nextId in ViewModel", now)
        )
        _creatures.value = increasedCreatures
    }

    /**
     * テストメソッド：生き物のリストのランダムな位置に新たなテストデータを挿入
     */
    fun addTestInputCreature(creature: Creature) {
        val increasedCreatures = _creatures.value!!.toMutableList()
        val nextId = increasedCreatures.size + 1
        increasedCreatures.add(creature)
        _creatures.value = increasedCreatures
    }
}