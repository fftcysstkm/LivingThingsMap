package com.demo.android.mapapp.viewmodel.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.repository.creature.CreatureRepository
import com.demo.android.mapapp.viewmodel.add.AddCreatureScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 生き物のカテゴリー
 * （タブに表示）
 */
data class CreatureListState(
    val categories: List<String> = listOf(
        "鳥",
        "虫",
        "魚",
        "爬虫類",
        "両生類",
        "植物",
        "菌類",
        "軟体動物",
        "哺乳類",
        "その他"
    ),
    val currentIndex: Int = 0
)

/**
 * 生き物の情報に関するViewModel
 */
@HiltViewModel
class CreaturesListViewModel @Inject constructor(
    private val repository: CreatureRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreatureListState())
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> CreatureListState) {
        _state.value = newState()
    }
    private val _creatures = MutableStateFlow<List<Creature>>(emptyList())
    val creatures = _creatures.asStateFlow()


    init {
        viewModelScope.launch {
            repository.getCreaturesByCatId(0).collect() {
                _creatures.value = it
            }
        }
    }




    /**
     * 現在のタブを変更する
     */
    fun updateCreatureList(index: Int) {
        viewModelScope.launch {
            repository.getCreaturesByCatId(index.toLong()).collect(){
                _creatures.value = it
            }
        }
    }

}