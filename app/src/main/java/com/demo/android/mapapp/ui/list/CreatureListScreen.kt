package com.demo.android.mapapp.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.ui.component.AddFab
import com.demo.android.mapapp.ui.component.TopBar
import com.demo.android.mapapp.viewmodel.list.CreaturesListViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 生き物を一覧表示する画面
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreatureListScreen(
    viewModel: CreaturesListViewModel,
    onClickList: (Long, String, Long) -> Unit,
    onClickFab: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    // タブ切り替えに必要なコルーチンスコープ
    val coroutineScope = rememberCoroutineScope()

    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()

    // タブの状態を保持
    val pagerState = rememberPagerState()

    // 監視する生き物リスト（Flowが最初のデータを流してくるまでの間初期値として扱う空のリストを設定）
    val creatures = viewModel.creatures.collectAsState(initial = emptyList())

    val uiState = viewModel.state.collectAsState()

    // トップバーとFab設置
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar() },
        floatingActionButton = { AddFab(onClickFab = {onClickFab(uiState.value.currentIndex.toLong())}) }, content = { padding ->

            Column(modifier = modifier.padding(padding)) {
                // 生き物カテゴリーのタブ
                Tabs(
                    viewModel = viewModel,
                    categories = uiState.value.categories,
                    coroutineScope = coroutineScope,
                    pagerState = pagerState,
                )

                // カテゴリーに対応した生き物リスト
                TabContent(
                    categories = uiState.value.categories,
                    creatures = creatures,
                    pagerState = pagerState,
                    onClickList = onClickList,
                )
            }
        })
}

/**
 * タブのComposable
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(
    viewModel: CreaturesListViewModel,
    categories: List<String>,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,

        // 現在いるタブを示す装飾
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.White
            )
        }
    ) {
        categories.forEachIndexed { index, _ ->
            Tab(
                text = { Text(categories[index]) },
                selected = pagerState.currentPage == index,
                onClick = {
                    viewModel.updateCreatureList(index)
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

/**
 * タブのコンテンツComposable
 * タブに対応した生き物のリストを表示する
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContent(
    categories: List<String>,
    creatures: State<List<Creature>>,
    pagerState: PagerState,
    onClickList: (Long, String, Long) -> Unit,
    modifier: Modifier = Modifier
) {

    HorizontalPager(count = categories.size, state = pagerState) {

        LazyColumn(modifier.padding(8.dp)) {
            items(creatures.value) { creature ->
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
            .height(48.dp)
            .fillMaxWidth(1f),
        elevation = 2.dp
    ) {
        Text(
            text = creatureName,
            modifier
                .wrapContentSize(Alignment.CenterStart)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.body2
        )
    }
}