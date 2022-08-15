package com.tkmst.android.mapapp.viewmodel.list_detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tkmst.android.mapapp.R
import com.tkmst.android.mapapp.model.creature.Creature
import com.tkmst.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 生き物追加・編集画面の状態を表すクラス
 */
data class CreatureDetailState(
    val categories: List<String> = listOf(),
    val categoryId: Long = 0,
    val categoryName: String = "",
    val creatureId: Long = 1,
    val creatureName: String = "",
    val memo: String = "",
    val errorMessage: String = "",
    val done: Boolean = false
)

/**
 * 生き物リストにて生きものを追加、更新、削除するViewModel
 */
@HiltViewModel
class CreatureListDetailViewModel @Inject constructor(
    private val repository: CreatureRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CreatureDetailState())
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> CreatureDetailState) {
        _state.value = newState()
    }

    // ViewModel生成時、string.xmlから生き物カテゴリリストをStateに設定
    init {
        // 画面遷移時に生き物ID、生き物名、カテゴリIDが渡ってくる
        // 生き物IDと生き物名は編集モード時のみ渡ってくる
        val creatureId: Long = savedStateHandle.get<Long>("creatureId") ?: 0
        val creatureName: String = savedStateHandle.get<String>("creatureName") ?: ""
        val categoryId: Long = requireNotNull(savedStateHandle.get<Long>("categoryId"))
        val memo: String = savedStateHandle.get<String>("memo") ?: ""

        // データクラスに初期値設定（カテゴリーリストはリソースから取得）
        updateState {
            currentState().copy(
                creatureId = creatureId,
                creatureName = creatureName,
                categoryId = categoryId,
                memo = memo,
                categories =
                listOf(*context.resources.getStringArray(R.array.category_list))
            )
        }
    }

    /**
     * 生き物の情報を保存
     * @param  creatureName 生きもの名
     * @param categoryId カテゴリ名
     * @param memo 備考
     */
    fun save(creatureName: String, categoryId: Int, memo: String) {

        // 生き物名が空だったらエラーメッセージを表示
        if (creatureName.trim().isEmpty()) {
            updateState { currentState().copy(errorMessage = context.resources.getString(R.string.error_empty_name_text)) }
            return
        }

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

    /**
     * 生き物の情報を更新
     * @param categoryId カテゴリID
     * @param creatureId 生きものID
     * @param creatureName 生きもの名
     * @param memo 備考（テーブルcreatureの方。creature_detailではない。）
     */
    fun update(creatureId: Long, creatureName: String, memo: String) {

        // 生き物名が空だったらエラーメッセージを表示
        if (creatureName.trim().isEmpty()) {
            updateState { currentState().copy(errorMessage = context.resources.getString(R.string.error_empty_name_text)) }
            return
        }

        // 更新
        viewModelScope.launch {
            try {
                repository.updateCreature(
                    creatureId = creatureId,
                    creatureName = creatureName,
                    memo = memo
                )
                // 更新完了
                updateState { currentState().copy(done = true) }

            } catch (e: Exception) {
                // 更新失敗
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    /**
     * 生き物の情報を削除
     * @param creatureId 生きものID
     */
    fun delete(creatureId: Long) {

        // 削除
        viewModelScope.launch {
            try {
                // Creatureテーブルから生き物削除
                repository.deleteCreatureById(creatureId = creatureId)
                // CreatureDetailテーブルから生き物削除
                repository.deleteCreatureDetailById(creatureId = creatureId)
                // 削除完了
                updateState { currentState().copy(done = true) }

            } catch (e: Exception) {
                // 削除失敗
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    /**
     * 生きもののカテゴリ名を更新する
     * @param categoryId 生きものカテゴリID
     */
    fun setCategoryName(categoryId: Long) {
        val categoryName = state.value.categories[categoryId.toInt()]
        updateState { currentState().copy(categoryName = categoryName) }
    }

    /**
     * 生きもの名を更新する
     * @param name 生きもの名
     */
    fun updateCreatureName(name: String) {
        updateState { currentState().copy(creatureName = name) }
    }

    /**
     * 生きもの備考を更新する
     * @param memo 備考文字列
     */
    fun updateMemo(memo: String) {
        updateState { currentState().copy(memo = memo) }
    }

    /**
     * エラーメッセージを空文字にリセットする。
     */
    fun resetErrorMessage() {
        updateState { currentState().copy(errorMessage = "") }
    }

    /**
     * 処理完了状態をリセット（false）に戻す
     */
    fun resetDoneValue() {
        if (currentState().done) {
            updateState { currentState().copy(done = false) }
        }
    }
}