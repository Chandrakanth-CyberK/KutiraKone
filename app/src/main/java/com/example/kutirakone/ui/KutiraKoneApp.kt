package com.example.kutirakone.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kutirakone.model.FabricItem
import com.example.kutirakone.ui.screens.DesignIdeasScreen
import com.example.kutirakone.ui.screens.DetailScreen
import com.example.kutirakone.ui.screens.HomeScreen
import com.example.kutirakone.ui.screens.ProfileScreen
import com.example.kutirakone.ui.screens.SwapRequestsScreen
import com.example.kutirakone.ui.screens.UploadScreen
import com.example.kutirakone.ui.theme.KutiraKoneTheme
import com.example.kutirakone.viewmodel.FabricViewModel

@Composable
fun KutiraKoneApp() {
    val navController = rememberNavController()
    val viewModel: FabricViewModel = viewModel()
    var selectedItem by remember { mutableStateOf<FabricItem?>(null) }

    KutiraKoneTheme {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToUpload = { navController.navigate("upload") },
                    onNavigateToDetail = { item ->
                        selectedItem = item
                        navController.navigate("detail")
                    },
                    onNavigateToIdeas = { navController.navigate("ideas") },
                    onNavigateToSwaps = { navController.navigate("swaps") },
                    onNavigateToProfile = { navController.navigate("profile") }
                )
            }
            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("swaps") {
                SwapRequestsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("upload") {
                UploadScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("detail") {
                selectedItem?.let { item ->
                    DetailScreen(
                        item = item,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
            composable("ideas") {
                DesignIdeasScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
