package com.panmatsu.unigiri.scenes.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.panmatsu.unigiri.model.DeckCardModel

@Composable
fun SelectedCardCell(card: DeckCardModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(card.img)
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

        if (card.count > 1) {
            CountBadge(
                count = card.count,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun CountBadge(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = "×$count",
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .padding(3.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    )
}
