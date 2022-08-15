package com.tkmst.android.mapapp.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tkmst.android.mapapp.ui.add.AddCreatureScreen
import com.tkmst.android.mapapp.ui.edit.EditCreatureScreen
import com.tkmst.android.mapapp.ui.list.CreatureListScreen
import com.tkmst.android.mapapp.ui.map.MapScreen
import com.tkmst.android.mapapp.ui.theme.LivingThingsMapTheme
import com.tkmst.android.mapapp.viewmodel.list_detail.CreatureListDetailViewModel
import com.tkmst.android.mapapp.viewmodel.list.CreaturesListViewModel
import com.tkmst.android.mapapp.viewmodel.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LivingThingsMapActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapApp() {
    val navController = rememberNavController()
    LivingThingsMapTheme {
        NavHost(navController = navController, startDestination = "list") {
            // 生き物リスト(メイン)
            composable("list") {
                val viewModel = hiltViewModel<CreaturesListViewModel>()
                CreatureListScreen(
                    viewModel = viewModel,
                    onClickList = { creatureId, creatureName, memo, categoryId ->
                        val route =
                            if (memo != null) "$creatureId/$creatureName/$categoryId?memo=$memo" else "$creatureId/$creatureName/$categoryId"
                        navController.navigate(
                            "map/$route"
                        )
                    },
                    onClickFab = { categoryId -> navController.navigate("add/$categoryId") },
                    onClickListWhenEdit = { creatureId, creatureName, memo, categoryId ->
                        val route =
                            if (memo != null) "$creatureId/$creatureName/$categoryId?memo=$memo" else "$creatureId/$creatureName/$categoryId"
                        navController.navigate(
                            "edit/$route"
                        )
                    }
                )
            }

            // 生き物追加画面に遷移（カテゴリーIDを引数として一覧画面から受け取る）
            composable(
                route = "add/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
            )
            { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getLong("categoryId")
                val viewModel = hiltViewModel<CreatureListDetailViewModel>()
                AddCreatureScreen(
                    viewModel = viewModel,
                    onClickTopBarBack = { navController.popBackStack() },
                    categoryId = categoryId
                )
            }

            // 生き物編集(カテゴリーID、生き物ID、生き物名、追加モードか編集モードかを引数としてわたす)
            composable(route = "edit/{creatureId}/{creatureName}/{categoryId}?memo={memo}",
                arguments = listOf(
                    navArgument("creatureId") { type = NavType.LongType },
                    navArgument("creatureName") { type = NavType.StringType },
                    navArgument("categoryId") { type = NavType.LongType },
                    navArgument("memo") {
                        type = NavType.StringType
                        nullable = true
                    }
                )) { backStackEntry ->
                val viewModel = hiltViewModel<CreatureListDetailViewModel>()
                val creatureId =
                    backStackEntry.arguments?.getLong("creatureId") ?: 0
                val creatureName = backStackEntry.arguments?.getString("creatureName") ?: ""
                val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0
                EditCreatureScreen(
                    viewModel = viewModel,
                    categoryId = categoryId,
                    creatureId = creatureId,
                    creatureName = creatureName,
                    onClickTopBarBack = { navController.popBackStack() }
                )
            }

            // マップ（生き物IDを引数としてわたす）
            composable(
                route = "map/{creatureId}/{creatureName}/{categoryId}?memo={memo}",
                arguments = listOf(
                    navArgument("creatureId") { type = NavType.LongType },
                    navArgument("creatureName") { type = NavType.StringType },
                    navArgument("categoryId") { type = NavType.LongType },
                    navArgument("memo") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val viewModel = hiltViewModel<MapViewModel>()
                val creatureId = backStackEntry.arguments?.getLong("creatureId")
                val creatureName = backStackEntry.arguments?.getString("creatureName")
                val categoryId = backStackEntry.arguments?.getLong("categoryId")
                MapScreen(
                    viewModel = viewModel,
                    onClickTopBarBack = { navController.popBackStack() },
                    creatureId = creatureId,
                    creatureName = creatureName,
                    categoryId = categoryId
                )
            }
        }
    }
}