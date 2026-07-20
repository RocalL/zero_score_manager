package com.zerogame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zerogame.data.model.GameType
import com.zerogame.ui.screens.*
import com.zerogame.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    playersViewModel: PlayersViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel(),
    skyjoGameViewModel: SkyjoGameViewModel = viewModel(),
    gameHistoryViewModel: GameHistoryViewModel = viewModel(),
    playerProfileViewModel: PlayerProfileViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartGame = { gameType ->
                    when (gameType) {
                        GameType.ZERO -> navController.navigate("zero_new_game")
                        GameType.SKYJO -> navController.navigate("skyjo_new_game")
                    }
                },
                onManagePlayers = { navController.navigate("players") },
                onViewHistory = { navController.navigate("history") },
                onViewProfile = { navController.navigate("profile/$it") }
            )
        }

        composable("players") {
            PlayersScreen(
                viewModel = playersViewModel,
                onBack = { navController.popBackStack() },
                onViewProfile = { navController.navigate("profile/$it") }
            )
        }

        // Zero game flow
        composable("zero_new_game") {
            NewGameScreen(
                viewModel = gameViewModel,
                onStartGame = {
                    navController.navigate("zero_score_entry") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("zero_score_entry") {
            ScoreEntryScreen(
                viewModel = gameViewModel,
                onGameFinished = {
                    navController.navigate("leaderboard") {
                        popUpTo("zero_new_game") { inclusive = true }
                    }
                },
                onPickCards = { playerId ->
                    val playerName = gameViewModel.allPlayers.value.find { it.id == playerId }?.name ?: "Player"
                    navController.navigate("card_picker/$playerId/${java.net.URLEncoder.encode(playerName, "UTF-8")}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "card_picker/{playerId}/{playerName}",
            arguments = listOf(
                navArgument("playerId") { type = NavType.LongType },
                navArgument("playerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getLong("playerId") ?: return@composable
            val playerName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("playerName") ?: "Player",
                "UTF-8"
            )
            CardPickerScreen(
                viewModel = gameViewModel,
                playerId = playerId,
                playerName = playerName,
                onConfirm = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // Skyjo game flow
        composable("skyjo_new_game") {
            SkyjoNewGameScreen(
                viewModel = skyjoGameViewModel,
                onStartGame = {
                    navController.navigate("skyjo_score_entry") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("skyjo_score_entry") {
            SkyjoScoreEntryScreen(
                viewModel = skyjoGameViewModel,
                onGameFinished = {
                    navController.navigate("skyjo_leaderboard") {
                        popUpTo("skyjo_new_game") { inclusive = true }
                    }
                },
                onPickCards = { playerId ->
                    val playerName = skyjoGameViewModel.allPlayers.value.find { it.id == playerId }?.name ?: "Player"
                    navController.navigate("skyjo_card_picker/$playerId/${java.net.URLEncoder.encode(playerName, "UTF-8")}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "skyjo_card_picker/{playerId}/{playerName}",
            arguments = listOf(
                navArgument("playerId") { type = NavType.LongType },
                navArgument("playerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getLong("playerId") ?: return@composable
            val playerName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("playerName") ?: "Player",
                "UTF-8"
            )
            SkyjoCardPickerScreen(
                viewModel = skyjoGameViewModel,
                playerId = playerId,
                playerName = playerName,
                onConfirm = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("skyjo_leaderboard") {
            SkyjoLeaderboardScreen(
                viewModel = skyjoGameViewModel,
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onStartNewGame = {
                    navController.navigate("skyjo_new_game") {
                        popUpTo("home")
                    }
                }
            )
        }

        // Shared
        composable("leaderboard") {
            LeaderboardScreen(
                viewModel = gameViewModel,
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onStartNewGame = {
                    navController.navigate("zero_new_game") {
                        popUpTo("home")
                    }
                }
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
