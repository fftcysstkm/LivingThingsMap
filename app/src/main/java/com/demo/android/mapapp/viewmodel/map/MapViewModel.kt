package com.demo.android.mapapp.viewmodel.map

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.demo.android.mapapp.model.creature.CreatureDetail
import com.demo.android.mapapp.model.date.RecordDateTime
import com.demo.android.mapapp.model.location.LocationLiveData
import com.demo.android.mapapp.repository.creature.CreatureRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * BottomSheetScaffoldに表示する状態を表すクラス
 */
@SuppressLint("NewApi")
data class DetailRecordState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val creatureId: Long,
    val creatureName: String,
    val categoryId: Long,
    val creatureNum: Int = 1,
    val detailMemo: String = "",
    val recordedAt: RecordDateTime = RecordDateTime(LocalDateTime.now()),
    val tappedLocation: LatLng = LatLng(0.0, 0.0),
    val done: Boolean = false,
    val isNormalMap: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String = ""
)

/**
 * マップのViewModel
 * https://www.youtube.com/watch?v=t2Vi7iKC9v4&t=239s
 * https://proandroiddev.com/android-tutorial-on-location-update-with-livedata-774f8fcc9f15
 * https://johnoreilly.dev/posts/jetpack-compose-google-maps-part2/
 */
@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class MapViewModel @Inject constructor(
    private val repository: CreatureRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // ナビゲーションで渡される生き物名・ID、カテゴリーID
    val creatureId: Long = requireNotNull(savedStateHandle.get<Long>("creatureId"))
    val creatureName: String = requireNotNull(savedStateHandle.get<String>("creatureName"))
    val categoryId: Long = requireNotNull(savedStateHandle.get<Long>("categoryId"))

    private val locationLiveData = LocationLiveData(application)

    val creatureList = flow {
        val list = repository.getCreatureDetails(creatureId)
        emitAll(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _state = MutableStateFlow(
        DetailRecordState(
            creatureId = creatureId,
            creatureName = creatureName,
            categoryId = categoryId,
        )
    )
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> DetailRecordState) {
        _state.value = newState()
    }

    fun getLocationLiveData() = locationLiveData
    fun startLocation() {
        locationLiveData.startLocationUpdates()
    }

    /**
     * タップした位置の生物の情報を記録する
     */
    fun addCreatureDetail() {
        val creatureDetail = CreatureDetail(
            creatureDetailId = 0,
            creatureId = _state.value.creatureId,
            creatureNum = _state.value.creatureNum,
            detailMemo = _state.value.detailMemo,
            recordedAt = _state.value.recordedAt,
            longitude = _state.value.tappedLocation.longitude,
            latitude = _state.value.tappedLocation.latitude
        )

        // 生き物詳細を保存
        viewModelScope.launch {
            try {
                repository.addCreatureDetail(creatureDetail)
                updateState { currentState().copy(done = true) }
                Log.d("creatureDetail", "insert success")
            } catch (e: Exception) {
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    /**
     * マーカーの位置を更新する（タップ時）
     * @param position タップした位置情報
     */
    fun updateTappedLocation(position: LatLng) {
        updateState {
            currentState().copy(
                tappedLocation = position
            )
        }
    }

    /**
     * 過去記録したマーカーをタップして、stateを更新する。
     * これにより過去の記録情報をボトムシートに表示する。
     * @param creatureDetail 生き物詳細情報
     */
    fun updateStateForEditCreature(creatureDetail: CreatureDetail) {
        with(creatureDetail) {
            updateState {
                currentState().copy(
                    creatureNum = creatureNum,
                    detailMemo = detailMemo ?: "",
                    recordedAt = recordedAt
                )
            }
        }
    }


    /**
     * 年月日の状態を更新する
     * @param year 年
     * @param month 月
     * @param dayOfMonth 日
     */
    fun updateRecordedAtDate(year: Int, month: Int, dayOfMonth: Int) {
        // stateのRecordDateTimeクラス内の、年月日(LocalDate)だけを更新
        val minute = currentState().recordedAt.dateTime.minute
        val hour = currentState().recordedAt.dateTime.hour
        val changedDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
        updateState {
            currentState()
                .copy(
                    recordedAt = _state.value.recordedAt
                        .copy(dateTime = changedDateTime)
                )
        }
    }

    /**
     * 時刻の状態を更新する
     * @param hour 年
     * @param minute 月
     */
    fun updateRecordedAtTime(hour: Int, minute: Int) {
        // stateのRecordDateTimeクラス内の、時・分(LocalTime)だけを更新
        val year = currentState().recordedAt.dateTime.year
        val month = currentState().recordedAt.dateTime.monthValue
        val dayOfMonth = currentState().recordedAt.dateTime.dayOfMonth
        val changedDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
        updateState {
            currentState()
                .copy(
                    recordedAt = _state.value.recordedAt
                        .copy(dateTime = changedDateTime)
                )
        }
    }

    /**
     * 記録する生き物の数を1減らす
     */
    fun decreaseCreatureNum() {
        val creatureNum = _state.value.creatureNum
        // 記録する生き物の数は最小値1
        val decreasedNum = if (creatureNum > 2) creatureNum - 1 else 1
        updateState { currentState().copy(creatureNum = decreasedNum) }
    }

    /**
     * 記録する生き物の数を1増やす
     */
    fun increaseCreatureNum() {
        val increasedNum = _state.value.creatureNum + 1
        updateState { currentState().copy(creatureNum = increasedNum) }
    }

    /**
     * 「メモ」のテキストフィールドの値を変更する
     */
    fun updateMemo(memo: String) {
        updateState { currentState().copy(detailMemo = memo) }
    }
}
