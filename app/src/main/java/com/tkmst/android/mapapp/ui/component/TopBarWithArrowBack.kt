package com.tkmst.android.mapapp.ui.component

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.tkmst.android.mapapp.R

/**
 * 戻る矢印付きのトップバー
 * @param onClickTopBarBack 前の画面に戻るラムダ式
 */
@Composable
fun TopBarWithArrowBack(onClickTopBarBack: () -> Unit) {
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