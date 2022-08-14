package com.demo.android.mapapp.ui.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.demo.android.mapapp.ui.component.CreatureListDetailBody
import com.demo.android.mapapp.ui.component.TopBarWithArrowBack
import com.demo.android.mapapp.viewmodel.list_detail.CreatureListDetailViewModel

/**
 * 生き物リストを編集・削除する画面
 * @param viewModel
 * @param onClickTopBarBack 前の画面に戻る処理
 * @param creatureId 生き物カテゴリID
 * @param modifier
 */
@Composable
fun EditCreatureScreen(
    viewModel: CreatureListDetailViewModel,
    onClickTopBarBack: () -> Unit,
    categoryId: Long?,
    creatureId: Long?,
    creatureName: String?,
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

    // 更新/削除が完了したら前の一覧画面に戻る
    if (state.done) {
        viewModel.resetDoneValue()
        onClickTopBarBack()
    }

    // 更新・削除画面本体
    Scaffold(
        scaffoldState = scaffoldState,
        // トップバー
        topBar = {
            TopBarWithArrowBack(onClickTopBarBack)
        }, content = { padding ->
            // 更新・削除画面本体
            CreatureListDetailBody(
                isModeAdd = false,
                state = state,
                onInputNameChange = { name -> viewModel.updateCreatureName(name) },
                onInputMemoChange = { memo -> viewModel.updateMemo(memo) },
                insert = { /* 編集画面では使用しない */ },
                update = {
                    viewModel.update(
                        state.creatureId,
                        state.creatureName,
                        state.memo
                    )
                },
                delete = { viewModel.delete(state.creatureId) },
                modifier = modifier.padding(padding)
            )
        })
}