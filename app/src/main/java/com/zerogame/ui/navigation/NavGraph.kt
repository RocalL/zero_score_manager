package com.zerogame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zerogame.ui.screens.*
import com.zerogame.viewmodel.GameHistoryViewModel
import com.zerogame.viewmodel.GameViewModel
import com.zerogame.viewmodel.PlayerProfileViewModel
import com.zerogame.viewmodel.PlayersViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    playersViewModel: PlayersViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel(),
    gameHistoryViewModel: GameHistoryViewModel = viewModel(),
    playerProfileViewModel: PlayerProfileViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartGame = { navController.navigate("new_game") },
                onManagePlayers = { navController.navigate("players") },
                onViewHistory = { navController.navigate("history") },
                onViewProfile = { playerId ->
                    navController.navigate("profile/$playerId")
                }
            )
        }

        composable("players") {
            PlayersScreen(
                viewModel = playersViewModel,
                onBack = { navController.popBackStack() },
                onViewProfile = { playerId ->
                    navController.navigate("profile/$playerId")
                }
            )
        }

        composable("new_game") {
            NewGameScreen(
                viewModel = gameViewModel,
                onStartGame = {
                    navController.navigate("score_entry") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("score_entry") {
            ScoreEntryScreen(
                viewModel = gameViewModel,
                onGameFinished = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("history") {
            GameHistoryScreen(
                viewModel = gameHistoryViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "profile/{playerId}",
            arguments = listOf(navArgument("playerId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getLong("playerId") ?: return@composable
            PlayerProfileScreen(
                playerId = playerId,
                viewModel = playerProfileViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
