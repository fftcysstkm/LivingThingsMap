package com.tkmst.android.mapapp.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.tkmst.android.mapapp.ui.component.CreatureListDetailBody
import com.tkmst.android.mapapp.ui.component.TopBarWithArrowBack
import com.tkmst.android.mapapp.viewmodel.list_detail.CreatureListDetailViewModel

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
    viewModel: CreatureListDetailViewModel,
    onClickTopBarBack: () -> Unit,
    categoryId: Long?,
    modifier: Modifier = Modifier
) {

    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()
    // 生き物リスト画面から渡されたカテゴリIDで、画面にカテゴリ名を表示する
    if (categoryId != null) viewModel.setCategoryName(categoryId)

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
    // 追加完了したら前の一覧画面に戻る
    if (state.done) {
        viewModel.resetDoneValue()
        onClickTopBarBack()
    }

    // 追加画面本体
    Scaffold(
        scaffoldState = scaffoldState,
        // トップバー
        topBar = {
            TopBarWithArrowBack(onClickTopBarBack)
        }, content = { padding ->

            CreatureListDetailBody(
                isModeAdd = true,
                state = state,
                onInputNameChange = { name -> viewModel.updateCreatureName(name) },
                onInputMemoChange = { memo -> viewModel.updateMemo(memo) },
                insert = { viewModel.save(state.creatureName, categoryId!!.toInt(), state.memo) },
                update = { /* 追加画面では使用しない */ },
                delete = { /* 追加画面では使用しない */ },
                modifier = modifier.padding(padding)
            )
        })
}

