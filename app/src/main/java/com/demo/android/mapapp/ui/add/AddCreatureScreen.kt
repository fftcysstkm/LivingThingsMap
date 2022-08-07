package com.demo.android.mapapp.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.viewmodel.add.AddCreatureScreenState
import com.demo.android.mapapp.viewmodel.add.AddCreatureViewModel

/**
 * 生き物を追加する画面
 * 参考：https://blog.mokelab.com/71/compose_todo9.html
 * @param viewModel
 * @param onClickTopBarBack 前の画面に戻る処理
 * @param categoryId 生き物カテゴリID
 * @param modifier
 */
@Composable
fun AddCreatureScreen(
    viewModel: AddCreatureViewModel,
    onClickTopBarBack: () -> Unit,
    categoryId:Long?,
    modifier: Modifier = Modifier
) {

    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()
    // 生き物リスト画面から渡されたカテゴリIDで、画面にカテゴリ名を表示する
    if(categoryId != null) viewModel.setCategoryName(categoryId)

    // 監視するUIの状態(変更があった場合、UIを再Compose)
    val state by viewModel.state.collectAsState()

    // viewmodelのエラーメッセージを監視
    // メッセージがある場合はスナックバーで表示
    if (state.errorMessage.isNotEmpty()) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = state.errorMessage
            )
            // 画面回転など再Compose時に再度表示されるのを抑制する
            viewModel.resetErrorMessage()
        }
    }
    // 再コンポーズ時にもう一度保存されるのを防止
    if (state.done) {
        viewModel.resetDoneValue()
        onClickTopBarBack()
    }

    // 画面本体
    Scaffold(
        scaffoldState = scaffoldState,
        // トップバー
        topBar = {
            CreateTopBar(onClickTopBarBack)
        }, content = { padding ->
            // 入力欄
            AddCreatureBody(
                state,
                onInputNameChange = { name -> viewModel.updateCreatureName(name) },
                onInputMemoChange = { memo -> viewModel.updateMemo(memo) },
                modifier = modifier.padding(padding)
            ) {
                viewModel.save(state.creatureName, categoryId!!.toInt(), state.memo)
            }
        })
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
    state: AddCreatureScreenState,
    onInputNameChange: (String) -> Unit,
    onInputMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    save: () -> Unit,
) {
    Column(modifier = modifier.padding(16.dp)) {

        // 生き物カテゴリ名
        Text(state.categoryName, fontSize = 32.sp)

        // 生き物の名前
        RowItem(
            iconId = R.drawable.ic_abc_24,
            contentDescription = "",
            inputTxt = state.creatureName,
            singleLine = true,
            label = "生き物の名前",
            onValueChange = onInputNameChange,
            modifier = modifier
        )
        // 備考
        RowItem(
            iconId = R.drawable.ic_edit_note_24,
            contentDescription = "",
            inputTxt = state.memo,
            singleLine = false,
            label = "備考",
            onValueChange = onInputMemoChange,
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
    onValueChange: (String) -> Unit,
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
