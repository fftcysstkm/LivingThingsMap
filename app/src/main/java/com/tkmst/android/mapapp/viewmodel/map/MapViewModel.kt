package com.tkmst.android.mapapp.viewmodel.map

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tkmst.android.mapapp.model.creature.CreatureDetail
import com.tkmst.android.mapapp.model.date.RecordDateTime
import com.tkmst.android.mapapp.model.location.LocationLiveData
import com.tkmst.android.mapapp.repository.creature.CreatureRepository
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.tkmst.android.mapapp.repository.user.UserPreferencesRepository
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
    val creatureDetailId: Long = 0,
    val creatureId: Long,
    val creatureName: String,
    val categoryId: Long,
    val creatureNum: Int = 1,
    val detailMemo: String = "",
    val recordedAt: RecordDateTime = RecordDateTime(LocalDateTime.now()),
    val location: LatLng = LatLng(0.0, 0.0),
    val done: Boolean = false,
    val mapProperties: MapProperties = MapProperties(
        isMyLocationEnabled = true,
        mapType = MapType.SATELLITE
    ),
    val isUpdateMode: Boolean = false,
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
    private val creatureRepository: CreatureRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // ナビゲーションで渡される生き物名・ID、カテゴリーID
    val creatureId: Long = requireNotNull(savedStateHandle.get<Long>("creatureId"))
    val creatureName: String = requireNotNull(savedStateHandle.get<String>("creatureName"))
    val categoryId: Long = requireNotNull(savedStateHandle.get<Long>("categoryId"))

    private val locationLiveData = LocationLiveData(application)

    val creatureList = flow {
        val list = creatureRepository.getCreatureDetails(creatureId)
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

    // 初期設定（地図表示モード）
    init {
        viewModelScope.launch {
            try {
                // 最期に設定した地図表示モードを内部DBから取得し、state更新
                // 1:衛星写真モード、0:絵モード
                val isMapTypeSatellite =
                    userPreferencesRepository.getUserPreferences().isModeSatellite == 1
                val lastMapType = if (isMapTypeSatellite) MapType.SATELLITE else MapType.NORMAL
                val lastMapProperties =
                    MapProperties(isMyLocationEnabled = true, mapType = lastMapType)
                updateState { currentState().copy(mapProperties = lastMapProperties) }
            } catch (e: Exception) {
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    private fun currentState() = _state.value
    private fun updateState(newState: () -> DetailRecordState) {
        _state.value = newState()
    }

    private fun refreshState() {
        _state.value = DetailRecordState(
            creatureId = creatureId,
            creatureName = creatureName,
            categoryId = categoryId
        )
    }

    fun getLocationLiveData() = locationLiveData
    fun startLocation() {
        locationLiveData.startLocationUpdates()
    }

    /**
     * マップを衛星写真か通常写真かを入れ替える
     * デフォルトは衛星写真
     */
    fun changeMapType() {
        // タップした位置情報を設定情報として保存し、stateを更新
        // 次開いたときはこの設定情報が初期値になる
        try {
            viewModelScope.launch {
                // 現在のマップモードを反転する
                val isMapTypeSatellite = !(_state.value.mapProperties.mapType == MapType.SATELLITE)
                userPreferencesRepository.updateIsModeSatellite(isMapTypeSatellite)
                updateState {
                    currentState().copy(
                        mapProperties = MapProperties(
                            isMyLocationEnabled = true,
                            mapType = getReversedMapType()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            updateState { currentState().copy(errorMessage = e.message ?: "") }
        }
    }

    /**
     * 現在の設定と逆のMapTypeを取得する
     * @return MapType（衛星orノーマル）
     */
    private fun getReversedMapType(): MapType {
        return if (state.value.mapProperties.mapType == MapType.SATELLITE) MapType.NORMAL else MapType.SATELLITE
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
            longitude = _state.value.location.longitude,
            latitude = _state.value.location.latitude
        )

        // 生き物詳細を保存
        viewModelScope.launch {
            try {
                // 保存完了後、マーカーが残るので詳細情報を初期化する
                creatureRepository.addCreatureDetail(creatureDetail)
                refreshState()
            } catch (e: Exception) {
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    /**
     * 生き物詳細情報を更新する（記録済みマーカータップ、更新ボタン押下）
     */
    fun updateCreatureDetail() {
        val creatureDetail = CreatureDetail(
            creatureDetailId = _state.value.creatureDetailId,
            creatureId = _state.value.creatureId,
            creatureNum = _state.value.creatureNum,
            detailMemo = _state.value.detailMemo,
            recordedAt = _state.value.recordedAt,
            longitude = _state.value.location.longitude,
            latitude = _state.value.location.latitude
        )
        // 生き物詳細情報更新処理
        viewModelScope.launch {
            try {
                creatureRepository.updateCreatureDetail(creatureDetail)
                // 更新完了後、マーカーが残るので詳細情報を初期化する
                refreshState()
            } catch (e: Exception) {
                updateState { currentState().copy(errorMessage = e.message ?: "") }
            }
        }
    }

    /**
     * マーカーを更新する
     */
    fun deleteCreatureDetail() {
        val creatureDetail = CreatureDetail(
            creatureDetailId = _state.value.creatureDetailId,
            creatureId = _state.value.creatureId,
            creatureNum = _state.value.creatureNum,
            detailMemo = _state.value.detailMemo,
            recordedAt = _state.value.recordedAt,
            longitude = _state.value.location.longitude,
            latitude = _state.value.location.latitude
        )
        // 生き物詳細情報削除処理
        viewModelScope.launch {
            try {
                // 削除完了後、マーカーが残るので詳細情報を初期化する
                creatureRepository.deleteCreatureDetail(creatureDetail)
                refreshState()
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
        refreshState()
        updateState {
            currentState().copy(
                location = position
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
                // 更新モードをtrueにする→ボトムシートのボタンが削除、更新になる
                currentState().copy(
                    creatureDetailId = creatureDetailId,
                    creatureNum = creatureNum,
                    detailMemo = detailMemo ?: "",
                    recordedAt = recordedAt,
                    location = LatLng(latitude, longitude),
                    isUpdateMode = true,
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
