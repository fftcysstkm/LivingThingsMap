package com.demo.android.mapapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.ui.theme.LivingThingsMapTheme

@Composable
fun AddCreatureScreen(modifier: Modifier = Modifier) {

    // 入力中の値
    // 生き物のタイプ、名前、備考
    var inputTypeTxt by rememberSaveable { mutableStateOf("") }
    var inputNameTxt by rememberSaveable { mutableStateOf("") }
    var inputMemoTxt by rememberSaveable { mutableStateOf("") }


    Column(modifier = modifier.padding(8.dp)) {
        // 生きものタイプ
        RowItem(
            iconId = R.drawable.ic_category_24,
            contentDescription = "",
            inputTxt = inputTypeTxt,
            label = "生き物のタイプ",
            onValueChange = { inputTypeTxt = it },
            modifier = modifier
        )
        // 生き物の名前
        RowItem(
            iconId = R.drawable.ic_abc_24,
            contentDescription = "",
            inputTxt = inputNameTxt,
            label = "生き物の名前",
            onValueChange = { inputNameTxt = it },
            modifier = modifier
        )
        // 備考
        RowItem(
            iconId = R.drawable.ic_edit_note_24,
            contentDescription = "",
            inputTxt = inputMemoTxt,
            label = "備考",
            onValueChange = { inputMemoTxt = it },
            modifier = modifier
        )
        // 追加ボタン
        Button(modifier = modifier
            .padding(top = 16.dp)
            .height(56.dp)
            .fillMaxWidth(1f), onClick = { /*TODO*/ }) {
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
            modifier = modifier
                .padding(start = 8.dp)
                .fillMaxWidth(1f),
            value = inputTxt,
            onValueChange = onValueChange,
            label = { Text(label) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LivingThingsMapTheme {
        AddCreatureScreen()
    }
}
