package com.demo.android.mapapp.viewmodel.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.demo.android.mapapp.model.location.LocationLiveData
import com.demo.android.mapapp.repository.creature.CreatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * マップのViewModel
 * https://www.youtube.com/watch?v=t2Vi7iKC9v4&t=239s
 * https://proandroiddev.com/android-tutorial-on-location-update-with-livedata-774f8fcc9f15
 * https://johnoreilly.dev/posts/jetpack-compose-google-maps-part2/
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: CreatureRepository,
    application: Application
) : AndroidViewModel(application) {

    private val locationLiveData = LocationLiveData(application)
    var text = MutableStateFlow("")

    fun getLocationLiveData() = locationLiveData
    fun startLocation() {
        locationLiveData.startLocationUpdates()
    }
}