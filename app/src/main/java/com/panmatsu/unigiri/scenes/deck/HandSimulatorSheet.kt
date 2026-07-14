package com.panmatsu.unigiri.scenes.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.panmatsu.unigiri.domain.HandCard
import com.panmatsu.unigiri.domain.HandSimulator
import com.panmatsu.unigiri.model.DeckModel

/**
 * 初期手札チェック: 5枚ドロー → 0〜5枚選択して1回だけ引き直し
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandSimulatorSheet(
    deck: DeckModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var dealt by remember { mutableStateOf(HandSimulator.deal(deck)) }
    var selectedIds by remember { mutableStateOf(emptySet<Int>()) }
    // nil=選択フェーズ / 非nil=結果フェーズ (1回のみの引き直しをフェーズで担保)
    var result by remember { mutableStateOf<List<HandCard>?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "初期手札チェック",
                style = MaterialTheme.typography.titleLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val cards = result ?: dealt.hand
                cards.forEach { handCard ->
                    HandCardCell(
                        handCard = handCard,
                        isSelected = result == null && handCard.instanceId in selectedIds,
                        showNewBadge = result != null && handCard.isRedrawn,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = result == null) {
                                selectedIds = if (handCard.instanceId in selectedIds) {
                                    selectedIds - handCard.instanceId
                                } else {
                                    selectedIds + handCard.instanceId
                                }
                            }
                    )
                }
            }

            Text(
                text = if (result == null) {
                    "引き直すカードを選択 (0〜5枚・1回のみ)"
                } else {
                    "これが最終手札です"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (result == null) {
                Button(
                    onClick = {
                        result = if (selectedIds.isEmpty()) {
                            dealt.hand
                        } else {
                            HandSimulator.mulligan(dealt.hand, selectedIds, dealt.pile)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selectedIds.isEmpty()) "このままキープ"
                        else "${selectedIds.size}枚引き直す"
                    )
                }
            } else {
                Button(
                    onClick = {
                        dealt = HandSimulator.deal(deck)
                        selectedIds = emptySet()
                        result = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("もう一度")
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("閉じる")
                }
            }
        }
    }
}

@Composable
private fun HandCardCell(
    handCard: HandCard,
    isSelected: Boolean,
    showNewBadge: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(handCard.card.img)
        .addHeader("Referer", "https://zutomayocard.net/")
        .crossfade(true)
        .build()

    Box(modifier = modifier) {
        AsyncImage(
            model = request,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.715f),
            contentScale = ContentScale.Crop
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "選択中",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (showNewBadge) {
            Text(
                text = "NEW",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(3.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }
    }
}
