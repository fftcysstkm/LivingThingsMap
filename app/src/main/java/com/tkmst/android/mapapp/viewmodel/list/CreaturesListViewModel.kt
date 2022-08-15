package com.tkmst.android.mapapp.viewmodel.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tkmst.android.mapapp.R
import com.tkmst.android.mapapp.model.creature.Creature
import com.tkmst.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 生き物のカテゴリー
 * （タブに表示）
 */
data class CreatureListState(
    val categories: List<String> = listOf(),
    val currentIndex: Int = 0,
    val isEditMode: Boolean = false
)

/**
 * 生き物の情報に関するViewModel
 */
@HiltViewModel
class CreaturesListViewModel @Inject constructor(
    private val repository: CreatureRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CreatureListState())
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> CreatureListState) {
        _state.value = newState()
    }
    private val _creatures = MutableStateFlow<List<Creature>>(emptyList())
    val creatures = _creatures.asStateFlow()

    // 初期設定
    init {
        // 生き物カテゴリをリソースから取得して設定
        updateState { currentState().copy(categories =
            listOf(*context.resources.getStringArray(R.array.category_list)))}
        // 生き物リストを内部DBから取得して設定
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
        updateState { currentState().copy(currentIndex = index) }
        viewModelScope.launch {
            repository.getCreaturesByCatId(index.toLong()).collect(){
                _creatures.value = it
            }
        }
    }

    /**
     * 編集モード切り替え
     */
    fun changeEditMode() {
        updateState { currentState().copy(isEditMode = !_state.value.isEditMode) }
    }

}