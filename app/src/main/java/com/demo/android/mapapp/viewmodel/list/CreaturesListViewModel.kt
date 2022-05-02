package com.demo.android.mapapp.viewmodel.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 生き物の情報に関するViewModel
 */
@HiltViewModel
class CreaturesViewModel @Inject constructor(
    private val repository: CreatureRepository
) : ViewModel() {


    // 生き物のリスト
    val creatures: LiveData<List<Creature>> =
        repository.getCreatures().asLiveData()

}