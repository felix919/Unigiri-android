package com.panmatsu.unigiri.scenes.deck

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panmatsu.unigiri.R
import com.panmatsu.unigiri.domain.DeckValidator
import com.panmatsu.unigiri.scenes.search.CardView
import com.panmatsu.unigiri.scenes.search.FilterBottomSheet
import com.panmatsu.unigiri.scenes.search.SearchUiState
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import com.panmatsu.unigiri.scenes.search.SearchViewModelFactory
import com.panmatsu.unigiri.ui.theme.BrandGreen

private val WarningOrange = Color(0xFFFF9800)
private val CompleteGreen = BrandGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckEditScreen(
    deckId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DeckEditViewModel = viewModel(
        factory = DeckEditViewModelFactory(context.applicationContext, deckId)
    )
    // 検索結果・フィルタ条件はSearch画面と同じViewModelを専用インスタンスで再利用する
    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory())
    val state by viewModel.uiState.collectAsState()
    val searchState by searchViewModel.uiState.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    if (state.saveError != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearSaveError,
            title = { Text("保存に失敗しました") },
            text = { Text(state.saveError ?: "") },
            confirmButton = {
                TextButton(onClick = viewModel::clearSaveError) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("キャンセル") }
                },
                actions = {
                    TextButton(
                        onClick = viewModel::save,
                        enabled = state.selectedEntries.isNotEmpty()
                    ) {
                        Text("保存")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showFilterSheet = true }
            ) {
                Icon(
                    painterResource(R.drawable.baseline_search),
                    "Floating action button."
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 上側: 選択したカードリスト
            SelectedSection(
                viewModel = viewModel,
                state = state,
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxWidth()
            )

            HorizontalDivider()

            // 下側: 検索結果 (SearchScreenと同一ビジュアル)
            ResultSection(
                viewModel = viewModel,
                state = state,
                searchState = searchState,
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxWidth()
            )
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                viewModel = searchViewModel,
                onDismiss = { showFilterSheet = false },
            )
        }
    }
}

@Composable
private fun SelectedSection(
    viewModel: DeckEditViewModel,
    state: DeckEditUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.deckName,
                onValueChange = viewModel::updateDeckName,
                placeholder = { Text("デッキ名") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${state.validation.totalCount}/${DeckValidator.DECK_SIZE}",
                style = MaterialTheme.typography.titleMedium,
                color = if (state.validation.isComplete) CompleteGreen else WarningOrange
            )
        }

        if (state.validation.showsCharacterWarning) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = WarningOrange
                )
                Text(
                    text = "キャラクターカードは50%以上を推奨",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningOrange
                )
            }
        }

        if (state.selectedEntries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "下のカードをタップして追加",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 基本6列、6列で4行目に折り返す(19種類以上)場合は7列に切り替える
            val columnCount = if (state.selectedEntries.size >= 19) 7 else 6
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.selectedEntries, key = { it.cardId }) { entry ->
                    SelectedCardCell(
                        card = entry,
                        modifier = Modifier
                            .padding(2.dp)
                            .clickable { viewModel.removeCard(entry.cardId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSection(
    viewModel: DeckEditViewModel,
    state: DeckEditUiState,
    searchState: SearchUiState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when {
            searchState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            searchState.error != null -> {
                Text(
                    text = searchState.error ?: "Error",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchState.result, key = { it.id }) { item ->
                        val count = state.validation.copyCounts[item.id] ?: 0

                        Box(
                            modifier = Modifier.alpha(
                                if (state.validation.canAdd(item.id)) 1f else 0.35f
                            )
                        ) {
                            CardView(item, onClick = { viewModel.addCard(item) })

                            if (count > 0) {
                                CountBadge(
                                    count = count,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
