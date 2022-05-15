package com.demo.android.mapapp.viewmodel.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.demo.android.mapapp.model.location.LocationLiveData
import com.demo.android.mapapp.repository.creature.CreatureRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

/**
 * BottomSheetScaffoldに表示する状態を表すクラス
 */
data class AddRecordState(
    val creatureId: Long,
    val creatureName: String,
    val categoryId: Long,
    val num: Int = 0,
    val detailMemo: String = "",
    val recordedAt: Calendar = Calendar.getInstance(),
    val latLng: LatLng = LatLng(0.0, 0.0),
    val done: Boolean = false
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
    var text = MutableStateFlow("")

    private val _state = MutableStateFlow(
        AddRecordState(
            creatureId = creatureId,
            creatureName = creatureName,
            categoryId = categoryId
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
}