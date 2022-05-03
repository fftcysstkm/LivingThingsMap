package com.demo.android.mapapp.ui.component

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.demo.android.mapapp.R

/**
 * アプリのトップバー
 */
@Composable
fun TopBar() {
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.app_name))
    })
}