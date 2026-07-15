package com.panmatsu.unigiri.scenes

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.panmatsu.unigiri.scenes.about.WebViewScreen
import com.panmatsu.unigiri.scenes.deck.DeckEditScreen
import com.panmatsu.unigiri.scenes.search.CardDetailScreen
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavHost(
    viewModel: SearchViewModel
) {
    val rootNavController = rememberNavController()

    fun navigateToWebView(title: String, url: String) {
        val encodedUrl = URLEncoder.encode(url, "UTF-8")
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        rootNavController.navigate("webView/$encodedTitle/$encodedUrl")
    }

    NavHost(
        navController = rootNavController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onCardClick = { cardId ->
                    rootNavController.navigate("cardDetail/$cardId")
                },
                onDeckEdit = { deckId ->
                    rootNavController.navigate(
                        if (deckId != null) "deckEdit?deckId=$deckId" else "deckEdit"
                    )
                },
                onNavigateToWebView = { title, url ->
                    navigateToWebView(title, url)
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

        composable(
            route = "deckEdit?deckId={deckId}",
            arguments = listOf(
                navArgument("deckId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            DeckEditScreen(
                deckId = backStackEntry.arguments?.getString("deckId"),
                onBack = { rootNavController.popBackStack() }
            )
        }

        composable("webView/{title}/{url}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""
            val url = backStackEntry.arguments?.getString("url")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""
            WebViewScreen(
                title = title,
                url = url,
                onBack = { rootNavController.popBackStack() }
            )
        }
    }
}
