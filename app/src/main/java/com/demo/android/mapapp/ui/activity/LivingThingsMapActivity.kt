package com.demo.android.mapapp.ui.activity

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
import com.demo.android.mapapp.ui.add.AddCreatureScreen
import com.demo.android.mapapp.ui.edit.EditCreatureScreen
import com.demo.android.mapapp.ui.list.CreatureListScreen
import com.demo.android.mapapp.ui.map.MapScreen
import com.demo.android.mapapp.ui.theme.LivingThingsMapTheme
import com.demo.android.mapapp.viewmodel.add.AddCreatureViewModel
import com.demo.android.mapapp.viewmodel.edit.EditCreatureViewModel
import com.demo.android.mapapp.viewmodel.list.CreaturesListViewModel
import com.demo.android.mapapp.viewmodel.map.MapViewModel
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
                    onClickList = { creatureId, creatureName, categoryId ->
                        navController.navigate("map/$creatureId/$creatureName/$categoryId")
                    },
                    onClickFab = { navController.navigate("add") })
            }
            // 生き物追加
            composable("add") {
                val viewModel = hiltViewModel<AddCreatureViewModel>()
                AddCreatureScreen(
                    viewModel = viewModel,
                    onClickTopBarBack = { navController.popBackStack() }
                )
            }
            // 生き物編集(生き物IDを引数としてわたす)
            composable("edit/{creatureId}") { backStackEntry ->
                val viewModel = hiltViewModel<EditCreatureViewModel>()
                val creatureId =
                    backStackEntry.arguments?.getString("creatureId")?.toInt() ?: 0
                EditCreatureScreen(viewModel = viewModel, creatureId = creatureId)
            }
            // マップ（生き物IDを引数としてわたす）
            composable(
                route = "map/{creatureId}/{creatureName}/{categoryId}",
                arguments = listOf(
                    navArgument("creatureId") { type = NavType.LongType },
                    navArgument("creatureName") { type = NavType.StringType },
                    navArgument("categoryId") { type = NavType.LongType },
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