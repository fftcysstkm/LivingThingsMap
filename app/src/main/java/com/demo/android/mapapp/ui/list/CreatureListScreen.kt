package com.demo.android.mapapp.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.ui.component.AddFab
import com.demo.android.mapapp.ui.component.TopBar
import com.demo.android.mapapp.viewmodel.list.CreaturesListViewModel

/**
 * 生き物を一覧表示する画面
 */
@Composable
fun CreatureListScreen(
    viewModel: CreaturesListViewModel,
    onClickList: (Long, String, Long) -> Unit,
    onClickFab: () -> Unit
) {
    // 監視する生き物リスト（Flowが最初のデータを流してくるまでの間初期値として扱う空のリストを設定）
    val creatures = viewModel.creatures.collectAsState(initial = emptyList())

    // トップバーとFab設置
    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = { AddFab(onClickFab = onClickFab) }
    ) {
        // 一覧表示
        CreaturesList(list = creatures, onClickList)
    }
}

/**
 * 生き物リストを表すパーツ
 */
@Composable
fun CreaturesList(
    list: State<List<Creature>>,
    onClickList: (Long, String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(list.value) { creature ->
            CreatureCard(
                creatureName = creature.creatureName,
                modifier = modifier.clickable {
                    onClickList(
                        creature.creatureId,
                        creature.creatureName,
                        creature.categoryId
                    )
                })
        }
    }
}

/**
 * 生き物リストの一行を表すパーツ
 */
@Composable
fun CreatureCard(
    creatureName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier
            .padding(4.dp)
            .height(36.dp)
            .fillMaxWidth(1f)
    ) {
        Text(
            text = creatureName, modifier.wrapContentSize(Alignment.CenterStart)
        )
    }
}