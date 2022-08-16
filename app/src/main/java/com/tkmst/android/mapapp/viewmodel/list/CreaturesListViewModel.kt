package com.tkmst.android.mapapp.viewmodel.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tkmst.android.mapapp.R
import com.tkmst.android.mapapp.model.creature.Creature
import com.tkmst.android.mapapp.repository.creature.CreatureRepository
import com.tkmst.android.mapapp.repository.user.UserPreferencesRepository
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
    private val creatureRepository: CreatureRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
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
        updateState {
            currentState().copy(
                categories =
                listOf(*context.resources.getStringArray(R.array.category_list))
            )
        }
        // 生き物リストを内部DBから取得して設定
        viewModelScope.launch {
            // 最期に選択したタブインデックスを取得
            val tabIndex = userPreferencesRepository.getUserPreferences().lastSelectedTabIndex
            updateState { currentState().copy(currentIndex = tabIndex) }
            // 取得したインデックスの生き物リストを取得
            creatureRepository.getCreaturesByCatId(tabIndex.toLong()).collect() {
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
            // 設定情報に最期に選択したタブインデックスを設定
            userPreferencesRepository.updateLastSelectedTabIndex(index.toLong())
            // タブインデックスで生き物リスト再取得
            creatureRepository.getCreaturesByCatId(index.toLong()).collect() {
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