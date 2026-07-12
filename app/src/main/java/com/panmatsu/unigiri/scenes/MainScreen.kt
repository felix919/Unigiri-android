package com.panmatsu.unigiri.scenes

import androidx.compose.foundation.background
import androidx.navigation.compose.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.panmatsu.unigiri.BuildConfig
import com.panmatsu.unigiri.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panmatsu.unigiri.scenes.about.AboutScreen
import com.panmatsu.unigiri.scenes.battle.BattleScreen
import com.panmatsu.unigiri.scenes.deck.DeckListScreen
import com.panmatsu.unigiri.scenes.deck.DeckListViewModel
import com.panmatsu.unigiri.scenes.deck.DeckListViewModelFactory
import com.panmatsu.unigiri.scenes.search.FilterBottomSheet
import com.panmatsu.unigiri.scenes.search.SearchScreen
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import com.panmatsu.unigiri.ui.theme.MainColor

@Composable
fun MainScreen(
    viewModel: SearchViewModel,
    onCardClick: (String) -> Unit,
    onDeckEdit: (String?) -> Unit,
    onNavigateToWebView: (title: String, url: String) -> Unit
) {
    val tabNavController = rememberNavController()
    val currentRoute = tabNavController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            when (currentRoute) {
                Screen.CardList.route -> {
                    FloatingActionButton(
                        onClick = { showSheet = true }
                    ) {
                        Icon(
                            painterResource(R.drawable.baseline_search),
                            "Floating action button."
                        )
                    }
                }

                Screen.Deck.route -> {
                    FloatingActionButton(
                        onClick = { onDeckEdit(null) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "デッキ作成")
                    }
                }
            }
        },
        bottomBar = {
            Column {
                if (BuildConfig.SHOW_ADS) {
                    AdBanner(
                        adUnitId = stringResource(R.string.admob_banner_unit_id)
                    )
                }
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Battle.route,
                        onClick = {
                            tabNavController.navigate(Screen.Battle.route) {
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(painterResource(R.drawable.baseline_sword), contentDescription = null)
                        },
                        label = { Text("Battle") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.CardList.route,
                        onClick = {
                            tabNavController.navigate(Screen.CardList.route) {
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_card_list),
                                contentDescription = null
                            )
                        },
                        label = { Text("CardList") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Deck.route,
                        onClick = {
                            tabNavController.navigate(Screen.Deck.route) {
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(Icons.Default.Style, contentDescription = null)
                        },
                        label = { Text("Deck") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.About.route,
                        onClick = {
                            tabNavController.navigate(Screen.About.route) {
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        },
                        label = { Text("About") }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = tabNavController,
            startDestination = Screen.Battle.route,
            modifier = Modifier
                .padding(padding)
                .background(color = MainColor)
        ) {
            composable(Screen.Battle.route) {
                BattleScreen()
            }

            composable(Screen.CardList.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onCardClick = onCardClick
                )

                if (showSheet) {
                    FilterBottomSheet(
                        viewModel = viewModel,
                        onDismiss = { showSheet = false },
                    )
                }
            }

            composable(Screen.Deck.route) {
                val context = LocalContext.current
                val deckListViewModel: DeckListViewModel = viewModel(
                    factory = DeckListViewModelFactory(context.applicationContext)
                )
                DeckListScreen(
                    viewModel = deckListViewModel,
                    onDeckClick = { deckId -> onDeckEdit(deckId) }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(onNavigateToWebView = onNavigateToWebView)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object CardList : Screen("cardList")
    object Battle : Screen("battle")
    object Deck : Screen("deck")
    object About : Screen("about")
}
