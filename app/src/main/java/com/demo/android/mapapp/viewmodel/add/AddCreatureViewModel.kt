package com.demo.android.mapapp.viewmodel.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 生き物追加に関するViewModel
 * 文字列リソースを使用するため、AndroidViewModelを継承
 * https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel
 * https://stackoverflow.com/questions/63144315/how-to-inject-app-context-in-viewmodel-with-hilt
 */
@HiltViewModel
class AddCreatureViewModel @Inject constructor(
    private val repository: CreatureRepository
) : ViewModel() {

    // エラーメッセージ
    val errorMessage = MutableStateFlow("")

    // 保存完了したか
    val done = MutableStateFlow(false)

    /**
     * 生き物の情報を保存
     */
    fun save(creatureType: String, creatureName: String) {

        // 生き物タイプと生き物名が空だったらエラーメッセージを表示
        if (creatureType.trim().isEmpty() || creatureName.trim().isEmpty()) {
            errorMessage.value = "Please input text."
            return
        }

        // リポジトリ経由で生き物を保存
        viewModelScope.launch {
            try {
                repository.addCreature(
                    Creature(
                        creatureId = null,
                        typeId = 1,
                        creatureName = creatureName,
                        createdAt = null,
                        updatedAt = null
                    )
                )
                // 保存完了
                done.value = true
            } catch (e: Exception) {
                errorMessage.value = e.message ?: ""
            }
        }
    }

    fun resetDoneValue() {
        if (done.value) {
            done.value = false
        }
    }
}