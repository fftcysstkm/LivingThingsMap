package com.demo.android.mapapp.ui.map

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.model.location.LocationDetail
import com.demo.android.mapapp.viewmodel.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * 地図画面
 * accompanistの使い方参考：
 * https://google.github.io/accompanist/permissions/
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onClickTopBarBack: () -> Unit,
    creatureId: Int,
    modifier: Modifier = Modifier
) {

    val currentLocation =
        viewModel.getLocationLiveData().observeAsState()

    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()

    // 位置情報が許可されたかを監視(ACCESS_FINE_LOCATIONが許可されればACCESS_COARSE_LOCATIONも許可される)
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // 画面本体
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CreateTopBar(onClickTopBarBack)
        }
    ) {
        // 位置情報が許可されたらマップ表示
        // 位置情報が許可されていなければ、理由説明/マップが使用できない旨表示→リクエストで権限リクエスト画面表示
        if (permissionState.status.isGranted) {
            MapView(currentLocation.value)
        } else {
            Column(modifier = modifier.fillMaxSize()) {

                val textToShow = if (permissionState.status.shouldShowRationale) {
                    "Location access is important for this app. Please grant the permission."
                } else {
                    "Map not available"
                }

                Text(textToShow)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}

/**
 * 地図
 */
@Composable
fun MapView(locationDetail: LocationDetail?, modifier: Modifier = Modifier) {

    val currentLocation = locationDetail?.let {
        val latitude = it.latitude.toDouble()
        val longitude = it.longitude.toDouble()
        LatLng(latitude, longitude)
    }
    if (currentLocation != null) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
        }
        // 現在地有効化
        val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
        val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
        val tappedLocation = rememberMarkerState(position = LatLng(0.0, 0.0))
        // マップ
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLongClick = {
                tappedLocation.position = it
            }
        ) {
            Marker(state = tappedLocation, draggable = true)
        }
    }
}

/**
 * トップバー生成
 */
@Composable
fun CreateTopBar(onClickTopBarBack: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick =
                // トップバーの「←」で1つ前の画面に戻る
                onClickTopBarBack
            ) {
                Icon(Icons.Filled.ArrowBack, "Back")
            }
        },
        title = {
            Text(stringResource(id = R.string.app_name))
        },
    )
}