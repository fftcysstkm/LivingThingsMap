package com.demo.android.mapapp.viewmodel.list

import androidx.lifecycle.ViewModel
import com.demo.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 生き物の情報に関するViewModel
 */
@HiltViewModel
class CreaturesListViewModel @Inject constructor(
    private val repository: CreatureRepository
) : ViewModel() {


    // 生き物のリスト(Flow型)
    val creatures = repository.getCreatures()

}