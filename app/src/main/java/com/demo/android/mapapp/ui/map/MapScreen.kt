package com.demo.android.mapapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.model.creature.CreatureDetail
import com.demo.android.mapapp.model.date.RecordDateTime
import com.demo.android.mapapp.model.location.LocationDetail
import com.demo.android.mapapp.ui.add.CreateTopBar
import com.demo.android.mapapp.viewmodel.map.DetailRecordState
import com.demo.android.mapapp.viewmodel.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil

const val RATIO_BOTTOM_SHEET_HEIGHT = 0.5
const val DEFAULT_CAMERA_ZOOM = 17f

/**
 * 地図画c面
 * accompanistの使い方参考：
 * https://google.github.io/accompanist/permissions/
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onClickTopBarBack: () -> Unit,
    creatureId: Long?,
    creatureName: String?,
    categoryId: Long?,
    modifier: Modifier = Modifier
) {

    // 現在位置
    val currentLocation =
        viewModel.getLocationLiveData().observeAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(0.0, 0.0), 17f
        )
    }

    val creatureList = viewModel.creatureList.collectAsState()

    val state by viewModel.state.collectAsState()

    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()

    // 位置情報が許可されたかを監視(ACCESS_FINE_LOCATIONが許可されればACCESS_COARSE_LOCATIONも許可される)
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // ボトムシートの状態、開閉操作に必要なcoroutineScope
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // マップロード完了したかどうか
    var isMapLoaded by remember { mutableStateOf(false) }

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

            // マップが読み込まれていなければサークルプログレスバー表示
            Box(modifier.fillMaxSize(1f)) {
                MapView(
                    viewModel,
                    state,
                    creatureList.value,
                    bottomSheetScaffoldState,
                    coroutineScope,
                    currentLocation.value,
                    onMapLoaded = {
                        isMapLoaded = true
                    },
                    onDateChange = { year: Int, month: Int, dayOfMonth: Int ->
                        viewModel.updateRecordedAtDate(year, month, dayOfMonth)
                    },
                    onTimeChange = { hour: Int, minute: Int ->
                        viewModel.updateRecordedAtTime(hour, minute)
                    },
                    onDecrement = { viewModel.decreaseCreatureNum() },
                    onIncrement = { viewModel.increaseCreatureNum() },
                    onValueChange = { memo -> viewModel.updateMemo(memo) },
                    onSaveRecord = {
                        // 保存ボタンで位置情報記録、ボトムシートを閉じる
                        viewModel.addCreatureDetail()
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.apply {
                                if (isCollapsed) expand() else collapse()
                            }
                        }
                    },
                    onUpdateRecord = {
                        // 更新ボタンで詳細情報更新、ボトムシートを閉じる
                        viewModel.updateCreatureDetail()
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.apply {
                                if (isCollapsed) expand() else collapse()
                            }
                        }
                    },
                    onDeleteRecord = {
                        // 更新ボタンで詳細情報更新、ボトムシートを閉じる
                        viewModel.deleteCreatureDetail()
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.apply {
                                if (isCollapsed) expand() else collapse()
                            }
                        }
                    }
                )
                // ローディング中のプログレスサークル
                if (!isMapLoaded) {
                    AnimatedVisibility(
                        modifier = modifier.fillMaxSize(),
                        visible = !isMapLoaded,
                        enter = EnterTransition.None,
                        exit = fadeOut(),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .wrapContentSize()
                        )
                    }
                }
            }

        } else {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val textToShow = if (permissionState.shouldShowRationale) {
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
 * 生き物詳細情報がViewModelのメソッド（既存記録表示用）に必要なため、引数として渡している
 */
@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapView(
    viewModel: MapViewModel,
    state: DetailRecordState,
    creatureList: List<CreatureDetail>,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    currentLocation: LocationDetail?,
    onMapLoaded: () -> Unit,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onValueChange: (String) -> Unit,
    onSaveRecord: () -> Unit,
    onUpdateRecord: () -> Unit,
    onDeleteRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 現在地ボタン有効化
    val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
    // 現在地情報表示有効化、衛星写真に設定
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.SATELLITE
            )
        )
    }
    val offSetY =
        ceil(LocalConfiguration.current.screenHeightDp * RATIO_BOTTOM_SHEET_HEIGHT).toInt()

    // カメラの状態（初期値LatLng(0.0, 0.0), 17f）
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(state.location.latitude, state.location.longitude),
            DEFAULT_CAMERA_ZOOM
        )
    }

    // 現在地が取得できたらカメラを現在地にフォーカス
    val currentLocationLatLng = currentLocation?.let {
        val latitude = it.latitude.toDouble()
        val longitude = it.longitude.toDouble()
        LatLng(latitude, longitude)
    }
    if (currentLocationLatLng != null) {
        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(currentLocationLatLng, DEFAULT_CAMERA_ZOOM)
    }

    // ボトムシート本体（マップロングクリック時に表示）
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetContent(
                onValueChange = onValueChange,
                state = state,
                onDateChange = onDateChange,
                onTimeChange = onTimeChange,
                onDecrement = onDecrement,
                onIncrement = onIncrement,
                onSaveRecord = onSaveRecord,
                onUpdateRecord = onUpdateRecord,
                onDeleteRecord = onDeleteRecord
            )
        },
        drawerGesturesEnabled = true,
        sheetPeekHeight = 0.dp,
    ) {

        // マップ
        Box(modifier = Modifier) {
            GoogleMap(
                modifier = modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapLoaded = onMapLoaded,
                onMapLongClick = {
                    // 地図ロングクリックでStateの緯度経度更新、ボトムシート開閉
                    // カメラ位置をボトムシート上の領域の中央にする todo なぜか機能しない
                    viewModel.updateTappedLocation(it)
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.apply {
                            if (isCollapsed) expand()
                        }
                        val projection = cameraPositionState.projection!!
                        val pointOfMarker = projection.toScreenLocation(it)
                        val pointToMove = Point(pointOfMarker.x, pointOfMarker.y + offSetY)
                        val latLngToMove = projection.fromScreenLocation(pointToMove)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLng(latLngToMove),
                            100
                        )
                    }
                }
            ) {

                // 記録したい生き物のマーカーを表示（マップロングクリックで表示）
                Marker(state = MarkerState(state.location), draggable = true)

                // 記録済みのマーカーを表示。マーカークリックでボトムシート表示、記録済み情報を表示
                // onClickでstateの生き物情報を、クリックしたマーカーの情報に変更。削除、更新可能となる
                creatureList.forEach { creature ->
                    val position = LatLng(creature.latitude, creature.longitude)
                    Marker(
                        state = MarkerState(position = position),
                        draggable = false,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        onClick = {
                            viewModel.updateStateForEditCreature(creature)
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.apply {
                                    if (isCollapsed) expand()
                                }
                            }
                            false
                        }
                    )
                }
            }

            // 衛星写真切り替えスイッチ
            Row(
                modifier
                    .padding(top = 64.dp, end = 8.dp)
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Satellite")
                    Switch(
                        state.isMapTypeSatellite,
                        onCheckedChange = {
                            val mapType = if (it) MapType.SATELLITE else MapType.NORMAL
                            mapProperties = mapProperties.copy(mapType = mapType)
                            viewModel.changeMapType()
                        },
                        colors = SwitchDefaults.colors(uncheckedTrackColor = Color.DarkGray)
                    )
                }
            }
        }
    }
//    }
}

/**
 * ModalBottomSheetLayoutの中身
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomSheetContent(
    state: DetailRecordState,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onSaveRecord: () -> Unit,
    onUpdateRecord: () -> Unit,
    onDeleteRecord: () -> Unit
) {

    // ボトムシートの高さ（画面の半分）
    val heightOfSheet =
        ceil(LocalConfiguration.current.screenHeightDp * RATIO_BOTTOM_SHEET_HEIGHT).toInt()

    Column(
        modifier
            .height(heightOfSheet.dp)
            .padding(16.dp)
    ) {
        val spacerSize = 16.dp

        Text(state.creatureName, style = MaterialTheme.typography.h5)
        Spacer(modifier = modifier.size(spacerSize))

        // 記録日
        CalendarText(
            recordDateTime = state.recordedAt,
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

        // ボタン（新規マーカー登録時：保存ボタン、既存マーカークリック時：編集と削除ボタン）
        SaveOrEditButton(
            isEditMode = state.isUpdateMode,
            saveRecord = onSaveRecord,
            editRecord = onUpdateRecord,
            deleteRecord = onDeleteRecord
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarText(
    recordDateTime: RecordDateTime,
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
            recordDateTime.dateString(),
            modifier
                .clickable { datePicker(context, recordDateTime, onDateChange) },
            fontSize = fontSize
        )

        // 時刻。タップでTimepicker立ち上げ
        Text(
            recordDateTime.timeString(),
            modifier
                .padding(start = 8.dp)
                .clickable { timePicker(context, recordDateTime, onTimeChange) },
            fontSize = fontSize
        )
    }
}

/**
 * ボトムシート内の日付をタップして表示するDatePicker
 */
@RequiresApi(Build.VERSION_CODES.O)
fun datePicker(
    context: Context,
    recordDateTime: RecordDateTime,
    onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
) {
    // 月は-1することで現実の日付になる
    DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onDateChange(year, month, dayOfMonth)
        },
        recordDateTime.dateTime.year,
        recordDateTime.dateTime.monthValue - 1,
        recordDateTime.dateTime.dayOfMonth
    ).show()
}

/**
 * ボトムシート内の時刻をタップして表示するTimePicker
 * AM/PM表記
 */
@RequiresApi(Build.VERSION_CODES.O)
fun timePicker(
    context: Context,
    recordDateTime: RecordDateTime,
    onTimeChange: (hour: Int, minute: Int) -> Unit
) {
    // AM/PM表記
    TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            onTimeChange(hour, minute)
        },
        recordDateTime.dateTime.hour,
        recordDateTime.dateTime.minute,
        false
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
        Text(
            creatureNumber.toString(),
            modifier.padding(horizontal = 24.dp),
            fontSize = fontSize
        )

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
 * 生物の記録時のメモComposable
 */
@Composable
fun SaveOrEditButton(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    saveRecord: () -> Unit,
    editRecord: () -> Unit,
    deleteRecord: () -> Unit
) {
    // 既存マーカークリック時：編集と削除ボタン
    if (isEditMode) {
        val height = 56.dp
        Row(
            modifier = modifier
                .fillMaxWidth(1f), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = modifier
                    .height(height)
                    .weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                ),
                onClick = deleteRecord
            ) {
                Text("削除")
            }
            Spacer(modifier = modifier.size(32.dp))
            Button(
                modifier = modifier
                    .height(height)
                    .weight(1f), onClick = editRecord
            ) {
                Text("更新")
            }
        }
        return
    }
    // 新規マーカー登録時：保存ボタン
    Button(
        modifier = modifier
            .fillMaxWidth(1f)
            .height(56.dp), onClick = saveRecord
    ) {
        Text("保存")
    }
}

/**
 * ボトムシートのプレビュー
 */
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewBottomSheet(modifier: Modifier = Modifier) {
    val state = DetailRecordState(
        creatureId = 1,
        categoryId = 1,
        creatureName = "Test"
    )
    val onValueChange: (String) -> Unit = { }
    val onDateChange: (year: Int, month: Int, dayOfMonth: Int) -> Unit = { _, _, _ -> }
    val onTimeChange: (hour: Int, minute: Int) -> Unit = { _, _ -> }
    val onDecrement: () -> Unit = {}
    val onIncrement: () -> Unit = {}
    val onSaveRecord: () -> Unit = {}
    val onUpdateRecord: () -> Unit = {}
    val onDeleteRecord: () -> Unit = {}

    BottomSheetContent(
        state,
        modifier,
        onValueChange,
        onDateChange,
        onTimeChange,
        onDecrement,
        onIncrement,
        onSaveRecord,
        onUpdateRecord,
        onDeleteRecord
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