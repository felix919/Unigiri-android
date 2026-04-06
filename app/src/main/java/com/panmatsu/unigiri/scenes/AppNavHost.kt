package com.panmatsu.unigiri.scenes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panmatsu.unigiri.scenes.search.CardDetailScreen
import com.panmatsu.unigiri.scenes.search.SearchViewModel

@Composable
fun AppNavHost(viewModel: SearchViewModel) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onCardClick = { cardId ->
                    rootNavController.navigate("cardDetail/$cardId")
                }
            )
        }

        composable("cardDetail/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: return@composable
            CardDetailScreen(
                cardId = cardId,
                viewModel = viewModel,
                onBack = { rootNavController.popBackStack() }
            )
        }
    }
}
