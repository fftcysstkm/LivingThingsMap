package com.demo.android.mapapp.viewmodel.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.demo.android.mapapp.model.creature.CreatureDetail
import com.demo.android.mapapp.model.date.RecordDate
import com.demo.android.mapapp.model.location.LocationLiveData
import com.demo.android.mapapp.repository.creature.CreatureRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * BottomSheetScaffoldに表示する状態を表すクラス
 */
data class AddRecordState(
    val creatureId: Long,
    val creatureName: String,
    val categoryId: Long,
    val creatureNum: Int = 1,
    val detailMemo: String = "",
    val recordedAt: RecordDate = RecordDate(Calendar.getInstance()),
    val tappedLocation: LatLng = LatLng(0.0, 0.0),
    val done: Boolean = false,
    val errorMessage: String = ""
)

/**
 * マップのViewModel
 * https://www.youtube.com/watch?v=t2Vi7iKC9v4&t=239s
 * https://proandroiddev.com/android-tutorial-on-location-update-with-livedata-774f8fcc9f15
 * https://johnoreilly.dev/posts/jetpack-compose-google-maps-part2/
 */
@HiltViewModel
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

    private val _state = MutableStateFlow(
        AddRecordState(
            creatureId = creatureId,
            creatureName = creatureName,
            categoryId = categoryId,
        )
    )
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> AddRecordState) {
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
            recordedAt = _state.value.recordedAt.toString(),
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
     * 年月日の状態を更新する
     * @param year 年
     * @param month 月
     * @param dayOfMonth 日
     */
    fun updateRecordedAtDate(year: Int, month: Int, dayOfMonth: Int) {
        // stateのRecordDateクラスのCalendar取得、年月日を更新
        val calendar = currentState().recordedAt.calendar
        calendar.set(year, month, dayOfMonth)
        updateState { currentState().copy(recordedAt = RecordDate(calendar)) }
    }

    /**
     * 時刻の状態を更新する
     * @param hour 年
     * @param minute 月
     */
    fun updateRecordedAtTime(hour: Int, minute: Int) {
        // stateのRecordDateクラスのCalendar取得、時分を更新
        val calendar = currentState().recordedAt.calendar
        calendar.set(Calendar.HOUR, hour)
        calendar.set(Calendar.MINUTE, minute)
        updateState { currentState().copy(recordedAt = RecordDate(calendar)) }
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
