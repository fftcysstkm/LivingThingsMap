package com.demo.android.mapapp.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.android.mapapp.R
import com.demo.android.mapapp.model.creature.Creature
import com.demo.android.mapapp.ui.component.AddFab
import com.demo.android.mapapp.viewmodel.list.CreaturesListViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 生き物を一覧表示する画面
 * @param viewModel
 * @param onClickList 一覧タップしたときの動作（マップ画面遷移）
 * @param onClickFab FABタップしたときの動作（生き物追加画面遷移）
 * @param onClickListWhenEdit 編集モードで一覧タップしたときの動作（生き物更新・削除画面に遷移）
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreatureListScreen(
    viewModel: CreaturesListViewModel,
    onClickList: (Long, String, String?, Long) -> Unit,
    onClickFab: (Long?) -> Unit,
    onClickListWhenEdit: (Long, String, String?, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // タブ切り替えに必要なコルーチンスコープ
    val coroutineScope = rememberCoroutineScope()
    // BackHandlerが有効かどうか（システムの戻るボタンを押したときに利用）
    var backHandlingEnabled by remember { mutableStateOf(true) }

    // scaffoldの状態
    val scaffoldState = rememberScaffoldState()

    // タブの状態を保持
    val pagerState = rememberPagerState()

    // 監視する生き物リスト（Flowが最初のデータを流してくるまでの間初期値として扱う空のリストを設定）
    val creatures = viewModel.creatures.collectAsState(initial = emptyList())

    val uiState = viewModel.state.collectAsState()

    // トップバーとFab設置
    // トップバーの編集ボタンで、リスト編集モード。リストタップすると編集画面に遷移
    // 通常モードでリストタップで地図記録画面に遷移
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBarForCreatureListScreen { viewModel.changeEditMode() } },
        floatingActionButton = { AddFab(onClickFab = { onClickFab(uiState.value.currentIndex.toLong()) }) },
        content = { padding ->

            Column(modifier = modifier.padding(padding)) {
                // 生き物カテゴリーのタブ
                Tabs(
                    viewModel = viewModel,
                    categories = uiState.value.categories,
                    coroutineScope = coroutineScope,
                    pagerState = pagerState,
                )

                Box(
                    modifier = modifier.fillMaxSize(
                    )
                ) {

                    // 生き物編集モードのときはリストタップで編集画面へ遷移するラムダ
                    // 生き物追加モードのときはリストタップで追加画面へ遷移するラムダ
                    val onClickListItem: (Long, String, String?, Long) -> Unit =
                        if (uiState.value.isEditMode) {
                            onClickListWhenEdit
                        } else {
                            onClickList
                        }

                    // カテゴリータブに対応した生き物リスト
                    TabContent(
                        categories = uiState.value.categories,
                        creatures = creatures,
                        pagerState = pagerState,
                        onClickList = onClickListItem,
                        modifier = modifier.align(Alignment.Center)
                    )

                    // 編集モードかどうかを示すカードビュー（編集モードの場合のみ表示）
                    if (uiState.value.isEditMode) {
                        Card(
                            modifier = modifier
                                .height(48.dp)
                                .width(160.dp)
                                .padding(8.dp)
                                .align(Alignment.BottomCenter)
                                .offset(x = 0.dp, y = (-22).dp),
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = Color.DarkGray,
                            elevation = 1.dp
                        ) {
                            Text(
                                modifier = modifier.wrapContentSize(Alignment.Center),
                                text = stringResource(id = R.string.edit_mode_message),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        })

    // 編集モード中、システムの戻るボタンを押下したら、追加モードに戻す
    if (uiState.value.isEditMode) {
        BackHandler(enabled = backHandlingEnabled) {
            viewModel.changeEditMode()
        }
    }
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
    onClickList: (Long, String, String, Long) -> Unit,
    modifier: Modifier = Modifier
) {

    HorizontalPager(count = categories.size, state = pagerState) {
        // 生きものリスト
        LazyColumn(modifier.padding(8.dp)) {
            items(creatures.value) { creature ->

                // カードタップでマップ画面に遷移
                // 編集モードの場合は編集画面に遷移
                CreatureCard(
                    creatureName = creature.creatureName,
                    modifier = modifier.clickable {
                        onClickList(
                            creature.creatureId,
                            creature.creatureName,
                            creature.memo ?: "",
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

/**
 * 生きものリスト画面用トップバー
 */
@Composable
fun TopBarForCreatureListScreen(onClickEditIcon: () -> Unit) {

    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {

            Box {
                // オプションメニュー（編集アイコン）
                IconButton(onClick = onClickEditIcon) {
                    Icon(Icons.Default.Edit, "")
                }
            }
        })
}