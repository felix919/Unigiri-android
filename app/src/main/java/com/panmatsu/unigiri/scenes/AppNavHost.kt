package com.panmatsu.unigiri.scenes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panmatsu.unigiri.scenes.about.WebViewScreen
import com.panmatsu.unigiri.scenes.search.CardDetailScreen
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import java.net.URLDecoder
import java.net.URLEncoder

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
                },
                onNavigateToWebView = { title, url ->
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    val encodedTitle = URLEncoder.encode(title, "UTF-8")
                    rootNavController.navigate("webView/$encodedTitle/$encodedUrl")
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
