package com.panmatsu.unigiri.scenes.deck

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.panmatsu.unigiri.domain.DeckValidator
import com.panmatsu.unigiri.model.DeckModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DeckListScreen(
    viewModel: DeckListViewModel,
    modifier: Modifier = Modifier,
    onDeckClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when {
            state.error != null -> {
                Text(
                    text = state.error ?: "Error",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.decks.isEmpty() -> {
                Text(
                    text = "デッキがありません\n右下の＋からデッキを作成できます",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.decks, key = { it.id }) { deck ->
                        DeckRow(
                            deck = deck,
                            onClick = { onDeckClick(deck.id) },
                            onDelete = { viewModel.delete(deck.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckRow(
    deck: DeckModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            deck.cards.take(3).forEach { card ->
                SelectedCardCell(
                    card = card,
                    modifier = Modifier.width(40.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${deck.totalCount}/${DeckValidator.DECK_SIZE}枚",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (deck.totalCount == DeckValidator.DECK_SIZE) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        Color(0xFFFF9800)
                    }
                )
                Text(
                    text = Instant.ofEpochMilli(deck.updatedAt)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "削除")
        }
    }
}
