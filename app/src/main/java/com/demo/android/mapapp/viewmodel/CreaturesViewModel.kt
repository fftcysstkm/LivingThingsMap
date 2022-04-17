package com.demo.android.mapapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.android.mapapp.data.Creature
import com.demo.android.mapapp.data.DataSource

class CreaturesViewModel : ViewModel() {

    // 生き物のリスト
    private val _creatures = MutableLiveData<List<Creature>>()
    val creatures: LiveData<List<Creature>> get() = _creatures

    // テストのデータを取得
    init {
        val datasource = DataSource()
        _creatures.value = datasource.createCreaturesList()
    }

}