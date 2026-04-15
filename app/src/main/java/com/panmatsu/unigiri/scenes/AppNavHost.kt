package com.panmatsu.unigiri.scenes

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panmatsu.unigiri.R
import com.panmatsu.unigiri.scenes.about.WebViewScreen
import com.panmatsu.unigiri.scenes.search.CardDetailScreen
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavHost(
    viewModel: SearchViewModel,
    hasConsent: Boolean,
    onConsent: () -> Unit
) {
    val rootNavController = rememberNavController()
    val baseUrl = stringResource(R.string.url_github_unigiri)
    val startDestination = if (hasConsent) "main" else "consent"

    fun navigateToWebView(title: String, url: String) {
        val encodedUrl = URLEncoder.encode(url, "UTF-8")
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        rootNavController.navigate("webView/$encodedTitle/$encodedUrl")
    }

    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {
        composable("consent") {
            FirstLaunchScreen(
                onAgree = {
                    onConsent()
                    rootNavController.navigate("main") {
                        popUpTo("consent") { inclusive = true }
                    }
                },
                onTermsClick = {
                    navigateToWebView("利用規約", "$baseUrl/terms-of-service.html")
                },
                onPrivacyPolicyClick = {
                    navigateToWebView("プライバシーポリシー", "$baseUrl/privacy-policy.html")
                }
            )
        }

        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onCardClick = { cardId ->
                    rootNavController.navigate("cardDetail/$cardId")
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
