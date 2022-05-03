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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.viewmodel.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onClickTopBarBack: () -> Unit,
    creatureId: Int,
    modifier: Modifier = Modifier
) {
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
            MapView()
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

@Composable
fun MapView(modifier: Modifier = Modifier) {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(modifier = modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
        Marker(position = singapore, title = "Singapore", snippet = "Marker in Singapore")
    }
}