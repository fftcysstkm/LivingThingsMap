package com.demo.android.mapapp.viewmodel.add

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.android.mapapp.R
import com.demo.android.mapapp.model.creature.Category
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 生き物追加画面の状態を表すクラス
 */
data class AddCreatureScreenState(
    val categories: List<String> = listOf(),
    val categoryName:String = "",
    val creatureName: String = "",
    val memo: String = "",
    val errorMessage: String = "",
    val done: Boolean = false
)

/**
 * 生き物追加に関するViewModel
 */
@HiltViewModel
class AddCreatureViewModel @Inject constructor(
    private val repository: CreatureRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AddCreatureScreenState())
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> AddCreatureScreenState) {
        _state.value = newState()
    }

    // ViewModel生成時、string.xmlから生き物カテゴリリストをStateに設定
    init {
        // 生き物カテゴリをリソースから取得して設定
        updateState { currentState().copy(categories =
        listOf(*context.resources.getStringArray(R.array.category_list)))}
    }

    /**
     * 生き物の情報を保存
     */
    fun save(creatureName: String, categoryId: Int, memo: String) {

        // 生き物名が空だったらエラーメッセージを表示
        if (creatureName.trim().isEmpty()) {
            updateState { currentState().copy(errorMessage = "Please input text.") }
            return
        }

        // リポジトリ経由で生き物を保存
        viewModelScope.launch {
            try {
                repository.addCreature(
                    Creature(
                        creatureId = 0,
                        categoryId = categoryId.toLong(),
                        creatureName = creatureName,
                        scientificName = null,
                        memo = memo
                    )
                )
                // 保存完了
                updateState { currentState().copy(done = true) }

            } catch (e: Exception) {
                // 保存失敗
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    fun setCategoryName(categoryId: Long){
        val categoryName = state.value.categories[categoryId.toInt()]
        updateState { currentState().copy(categoryName=categoryName) }
    }

    fun updateCreatureName(name: String) {
        updateState { currentState().copy(creatureName = name) }
    }

    fun updateMemo(memo: String) {
        updateState { currentState().copy(memo = memo) }
    }

    fun resetErrorMessage() {
        updateState { currentState().copy(errorMessage = "") }
    }

    fun resetDoneValue() {
        if (currentState().done) {
            updateState { currentState().copy(done = false) }
        }
    }
}