package com.tkmst.android.mapapp.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tkmst.android.mapapp.R
import com.tkmst.android.mapapp.viewmodel.list_detail.CreatureDetailState


/**
 * 生きものリストに新規追加、編集・削除する画面の共通部品
 * @param isModeAdd 追加モードか(true:追加モード。ボタンが保存処理。false:編集・削除モード。ボタンが更新か削除。)
 * @param state viewModelで保持するビューの状態
 * @param onInputMemoChange 備考が変更されたときの処理（viewModelのstateを変更）
 * @param onInputNameChange 生きものの名前が変更されたときの処理（viewModelのstateを変更）
 * @param insert 生きものテーブルに対するinsert操作
 * @param update 生きものテーブルに対するupdate操作
 * @param delete 生きものテーブルに対するdelete操作
 * @param modifier 装飾子
 */
@Composable
fun CreatureListDetailBody(
    isModeAdd: Boolean,
    state: CreatureDetailState,
    onInputNameChange: (String) -> Unit,
    onInputMemoChange: (String) -> Unit,
    insert: () -> Unit,
    update: () -> Unit,
    delete: () -> Unit,
    modifier: Modifier = Modifier,
) {

    // アラートダイアログを表示するかどうか（初期：しない）
    // 削除ボタン押下でtrueとなり、アラートダイアログが表示
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {

        // 生き物カテゴリ名
        Text(state.categoryName, fontSize = 32.sp)

        // 生き物の名前
        RowItem(
            iconId = R.drawable.ic_abc_24,
            contentDescription = "",
            inputTxt = state.creatureName,
            singleLine = true,
            label = stringResource(id = R.string.hint_text_creature_name),
            onValueChange = onInputNameChange,
            modifier = modifier
        )

        // 備考
        RowItem(
            iconId = R.drawable.ic_edit_note_24,
            contentDescription = "",
            inputTxt = state.memo,
            singleLine = false,
            label = stringResource(id = R.string.hint_text_creature_memo),
            onValueChange = onInputMemoChange,
            modifier = modifier
        )

        // 生きもの追加モードのとき、追加ボタン配置(押下時の処理は親で記述)
        if (isModeAdd) {
            Button(
                modifier = modifier
                    .padding(top = 16.dp)
                    .height(56.dp)
                    .fillMaxWidth(1f), onClick = insert
            ) {
                Text(stringResource(id = R.string.add_to_list_text))
            }
        }

        // 追加モード以外の場合、更新・削除ボタン配置（押下時の処理は親で記述）
        if (!isModeAdd) {
            val height = 56.dp
            Row(
                modifier = modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(1f), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 削除ボタン
                Button(
                    modifier = modifier
                        .height(height)
                        .weight(1f),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {showDialog = !showDialog}
                ) {
                    Text(stringResource(id = R.string.delete_list_text))
                }

                Spacer(modifier = modifier.size(32.dp))

                // 更新ボタン
                Button(
                    modifier = modifier
                        .height(height)
                        .weight(1f), onClick = update
                ) {
                    Text(stringResource(id = R.string.update_list_text))
                }
            }
        }

        // 削除ボタン押下でアラートダイアログ表示
        if (showDialog) {
            AlertDialog(
                text = { Text(stringResource(id = R.string.confirm_delete)) },
                confirmButton = {
                    TextButton(onClick = delete) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = !showDialog }) {
                        Text(stringResource(id = android.R.string.cancel))
                    }
                }, onDismissRequest = { })
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
