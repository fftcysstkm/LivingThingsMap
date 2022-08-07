package com.demo.android.mapapp.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.demo.android.mapapp.R

/**
 * アプリのトップバー
 */
@Composable
fun TopBar() {

    // ドロップダウンメニューを表示するかどうか（初期設定は表示しない。）
    var isDisplayMenu by remember{ mutableStateOf(false)}

    TopAppBar(title = {Text(text = stringResource(id = R.string.app_name))},
    actions = {

        Box{

            // オプションメニュー（編集か削除のドロップダウンメニュー表示）
            IconButton(onClick = { isDisplayMenu = !isDisplayMenu }) {
                Icon(Icons.Default.MoreVert, "")
            }

            // ドロップダウンメニュー
            DropdownMenu(expanded = isDisplayMenu, onDismissRequest = { isDisplayMenu = false }) {
                // 編集（生きもの一覧リストにラジオボタンを表示、画面下部に「編集ボタン」配置）
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Text(stringResource(id = R.string.drop_down_menu_edit))
                }

                // 削除（いきもの一覧リストにチェックボックスを表示、画面下部に「削除ボタン」配置）
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Text(stringResource(id = R.string.drop_down_menu_delete))
                }
            }
        }

    })


}