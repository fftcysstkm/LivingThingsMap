package com.demo.android.mapapp.ui.map

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.model.date.RecordDate
import com.demo.android.mapapp.model.location.LocationDetail
import com.demo.android.mapapp.ui.add.CreateTopBar
import com.demo.android.mapapp.viewmodel.map.AddRecordState
import com.demo.android.mapapp.viewmodel.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * 地図画面
 * accompanistの使い方参考：
 * https://google.github.io/accompanist/permissions/
 */
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onClickTopBarBack: () -> Unit,
    creatureId: Long?,
    creatureName: String?,
    categoryId: Long?,
    modifier: Modifier = Modifier
) {

    val currentLocation =
        viewModel.getLocationLiveData().observeAsState()

    val state by viewModel.state.collectAsState()

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
            MapView(
                state,
                currentLocation.value,
                modifier,
                onDateChange = { year: Int, month: Int, dayOfMonth: Int ->
                    viewModel.updateRecordedAtDate(year, month, dayOfMonth)
                },
                onTimeChange = { hour: Int, minute: Int ->
                    viewModel.updateRecordedAtTime(hour, minute)
                },
                onDecrement = { viewModel.decreaseCreatureNum() },
                onIncrement = { viewModel.increaseCreatureNum() },
                onValueChange = { memo -> viewModel.updateMemo(memo) },
                onSaveRecord = {}
            )
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
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapView(
    state: AddRecordState,
    locationDetail: LocationDetail?,
    modifier: Modifier = Modifier,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onValueChange: (String) -> Unit,
    onSaveRecord: () -> Unit
) {

    val currentLocation = locationDetail?.let {
        val latitude = it.latitude.toDouble()
        val longitude = it.longitude.toDouble()
        LatLng(latitude, longitude)
    }
    if (currentLocation != null) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation, 17f)
        }
        // ボトムシートの状態
        val bottomState = rememberBottomSheetScaffoldState()
        val coroutineScope = rememberCoroutineScope()

        // 現在地有効化
        val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
        val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
        val tappedLocation = rememberMarkerState(position = LatLng(0.0, 0.0))

        BottomSheetScaffold(
            scaffoldState = bottomState,
            sheetContent = {
                BottomSheetContent(
                    onValueChange = onValueChange,
                    state = state,
                    onDateChange = onDateChange,
                    onTimeChange = onTimeChange,
                    onDecrement = onDecrement,
                    onIncrement = onIncrement,
                    saveRecord = onSaveRecord
                )
            },
            drawerGesturesEnabled = true,
            sheetPeekHeight = 0.dp,
        ) {
            // マップ
            GoogleMap(
                modifier = modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapLongClick = {
                    tappedLocation.position = it
                    coroutineScope.launch {
                        bottomState.bottomSheetState.apply {
                            if (isCollapsed) expand() else collapse()
                        }
                    }
                }
            ) {
                Marker(state = tappedLocation, draggable = true)
            }
        }
    }
}

/**
 * ModalBottomSheetLayoutの中身
 */
@Composable
fun BottomSheetContent(
    state: AddRecordState,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    onDecrement: () -> Unit = {},
    onIncrement: () -> Unit = {},
    saveRecord: () -> Unit = {}
) {
    Column(
        modifier
            .height(400.dp)
            .padding(16.dp)
    ) {
        val spacerSize = 16.dp

        Text(state.creatureName, style = MaterialTheme.typography.h5)
        Spacer(modifier = modifier.size(spacerSize))

        // 記録日
        CalendarText(
            recordDate = state.recordedAt,
            onDateChange = onDateChange,
            onTimeChange = onTimeChange
        )
        Spacer(modifier = modifier.size(spacerSize))

        // 個体数
        CreatureNumber(state.creatureNum, onDecrement, onIncrement)
        Spacer(modifier = modifier.size(spacerSize))

        // メモ
        Memo(inputText = state.detailMemo, onValueChange = onValueChange)
        Spacer(modifier = modifier.size(spacerSize))

        // 保存ボタン
        Button(
            modifier = modifier
                .fillMaxWidth(1f)
                .height(56.dp), onClick = saveRecord
        ) {
            Text("保存")
        }
    }
}

@Composable
fun CalendarText(
    recordDate: RecordDate,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconEndPadding = 24.dp
    val fontSize = 16.sp
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_time_24),
            contentDescription = null,
            modifier = modifier.padding(end = iconEndPadding)
        )
        // 日付。タップでDatepicker立ち上げ
        Text(
            recordDate.recordDateText(),
            modifier
                .clickable { datePicker(context, recordDate, onDateChange) },
            fontSize = fontSize
        )

        // 時刻。タップでTimepicker立ち上げ
        Text(
            recordDate.recordTimeText(),
            modifier
                .padding(start = 8.dp)
                .clickable { timePicker(context, recordDate, onTimeChange) },
            fontSize = fontSize
        )
    }
}

/**
 * ボトムシート内の日付をタップして表示するDatePicker
 */
fun datePicker(
    context: Context,
    recordDate: RecordDate,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
) {
    DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onDateChange(year, month, dayOfMonth)
        },
        recordDate.year,
        recordDate.month,
        recordDate.dayOfMonth
    ).show()
}

/**
 * ボトムシート内の時刻をタップして表示するTimePicker
 * AM/PM表記
 */
fun timePicker(
    context: Context,
    recordDate: RecordDate,
    onTimeChange: (hour: Int, minute: Int) -> Unit
) {
    // AM/PM表記
    TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            onTimeChange(hour, minute)
        }, recordDate.hour, recordDate.minute, false
    ).show()
}

/**
 * 生物の個体数を表示するComposable
 * - + ボタンでインクリメント
 */
@Composable
fun CreatureNumber(
    creatureNumber: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontSize = 16.sp
    val iconEndPadding = 24.dp

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_number_24),
            contentDescription = null,
            modifier = modifier.padding(end = iconEndPadding)
        )
        // マイナスボタン
        OutlinedButton(
            onClick = { onDecrement() },
            modifier.size(40.dp),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_remove_24),
                contentDescription = null,
            )
        }

        // 生き物の数
        Text(creatureNumber.toString(), modifier.padding(horizontal = 24.dp), fontSize = fontSize)

        // プラスボタン
        OutlinedButton(
            onClick = { onIncrement() },
            modifier.size(40.dp),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_24),
                contentDescription = null,
            )
        }
    }
}

/**
 * 生物の記録時のメモComposable
 */
@Composable
fun Memo(
    inputText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconEndPadding = 24.dp

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit_note_24),
            contentDescription = null,
            modifier = modifier.padding(end = iconEndPadding)
        )
        OutlinedTextField(
            singleLine = false,
            value = inputText,
            onValueChange = onValueChange,
            label = { Text("メモ") },
            modifier = modifier
                .fillMaxWidth(1f)
        )
    }
}

/**
 * ボトムシートのプレビュー
 */
@Preview
@Composable
fun PreviewBottomSheet(modifier: Modifier = Modifier) {
    val state = AddRecordState(creatureId = 1, categoryId = 1, creatureName = "Test")
    val onValueChange: (String) -> Unit = { }
    val onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit = { _, _, _ -> }
    val onTimeChange: (hour: Int, minute: Int) -> Unit = { _, _ -> }
    val onDecrement: () -> Unit = {}
    val onIncrement: () -> Unit = {}
    val saveRecord: () -> Unit = {}

    BottomSheetContent(
        state,
        modifier,
        onValueChange,
        onDateChange,
        onTimeChange,
        onDecrement,
        onIncrement,
        saveRecord
    )
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