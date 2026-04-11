package com.panmatsu.unigiri.scenes

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
import com.panmatsu.unigiri.scenes.battle.BattleScreen
import com.panmatsu.unigiri.scenes.search.FilterBottomSheet
import com.panmatsu.unigiri.scenes.search.SearchScreen
import com.panmatsu.unigiri.scenes.search.SearchViewModel

@Composable
fun MainScreen(
    viewModel: SearchViewModel,
    onCardClick: (String) -> Unit
) {
    val tabNavController = rememberNavController()
    val currentRoute = tabNavController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (currentRoute == Screen.CardList.route) {
                FloatingActionButton(
                    onClick = { showSheet = true }
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_search),
                        "Floating action button."
                    )
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
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = tabNavController,
            startDestination = Screen.Battle.route,
            modifier = Modifier.padding(padding)
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
        }
    }
}

sealed class Screen(val route: String) {
    object CardList : Screen("cardList")
    object Battle : Screen("battle")
}
