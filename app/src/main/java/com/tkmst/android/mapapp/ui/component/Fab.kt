package com.tkmst.android.mapapp.ui.component

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable

@Composable
fun AddFab(onClickFab: () -> Unit = {}) {
    FloatingActionButton(onClick = onClickFab) {
        Icon(Icons.Filled.Add, "Add")
    }
}