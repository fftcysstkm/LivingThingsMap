package com.demo.android.mapapp.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.viewmodel.add.AddCreatureViewModel

/**
 * 生き物を追加する画面
 * 参考：https://blog.mokelab.com/71/compose_todo9.html
 */
@Composable
fun AddCreatureScreen(
    viewModel: AddCreatureViewModel,
    onClickTopBarBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    
    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()

    // エラーメッセージと保存完了フラグを監視
    val errorMessage = viewModel.errorMessage.collectAsState()
    val done = viewModel.done.collectAsState()

    // 入力中の値を保持
    // 生き物のタイプ、名前、備考
    val inputTypeTxt = rememberSaveable { mutableStateOf("") }
    val inputNameTxt = rememberSaveable { mutableStateOf("") }
    val inputMemoTxt = rememberSaveable { mutableStateOf("") }

    // viewmodelのエラーメッセージを監視
    // メッセージがある場合はスナックバーで表示
    if (errorMessage.value.isNotEmpty()) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessage.value
            )
            // 画面回転など再Compose時に再度表示されるのを抑制
            viewModel.errorMessage.value = ""
        }
    }
    // 再コンポーズ時にもう一度保存されるのを防止
    if (done.value) {
        viewModel.resetDoneValue()
        onClickTopBarBack()
    }

    // 画面本体
    Scaffold(
        scaffoldState = scaffoldState,
        // トップバー
        topBar = {
            CreateTopBar(onClickTopBarBack)
        }) {
        // 入力欄
        AddCreatureBody(inputTypeTxt, inputNameTxt, inputMemoTxt, modifier) {
            viewModel.save(inputTypeTxt.value, inputNameTxt.value)
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


/**
 * 追加情報画面、保存ボタン本体
 */
@Composable
fun AddCreatureBody(
    inputTypeTxt: MutableState<String>,
    inputNameTxt: MutableState<String>,
    inputMemoTxt: MutableState<String>,
    modifier: Modifier = Modifier,
    save: () -> Unit,
) {
    Column(modifier = modifier.padding(8.dp)) {
        // 生きものタイプ
        RowItem(
            iconId = R.drawable.ic_category_24,
            contentDescription = "",
            inputTxt = inputTypeTxt.value,
            singleLine = true,
            label = "生き物のタイプ",
            onValueChange = { inputTypeTxt.value = it },
            modifier = modifier
        )
        // 生き物の名前
        RowItem(
            iconId = R.drawable.ic_abc_24,
            contentDescription = "",
            inputTxt = inputNameTxt.value,
            singleLine = true,
            label = "生き物の名前",
            onValueChange = { inputNameTxt.value = it },
            modifier = modifier
        )
        // 備考
        RowItem(
            iconId = R.drawable.ic_edit_note_24,
            contentDescription = "",
            inputTxt = inputMemoTxt.value,
            singleLine = false,
            label = "備考",
            onValueChange = { inputMemoTxt.value = it },
            modifier = modifier
        )
        // 追加ボタン(押下時の処理は親で記述)
        Button(
            modifier = modifier
                .padding(top = 16.dp)
                .height(56.dp)
                .fillMaxWidth(1f), onClick = save
        ) {
            Text("追加")
        }
    }
}

/**
 * アイコンとテキスト入力欄部品
 */
@Composable
fun RowItem(
    iconId: Int,
    contentDescription: String,
    inputTxt: String,
    singleLine: Boolean,
    label: String,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(top = 8.dp)) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = contentDescription
        )
        OutlinedTextField(
            singleLine = singleLine,
            value = inputTxt,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = modifier
                .padding(start = 8.dp)
                .fillMaxWidth(1f),
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    LivingThingsMapTheme {
//        AddCreatureScreen()
//    }
//}
