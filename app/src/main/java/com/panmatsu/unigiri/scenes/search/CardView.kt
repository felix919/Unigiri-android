package com.panmatsu.unigiri.scenes.search

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.panmatsu.unigiri.model.ZutocaModel

@Composable
fun CardView(item: ZutocaModel, onClick: () -> Unit = {}) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(item.img)
        .addHeader("Referer", "https://zutomayocard.net/")
        .crossfade(true)
        .build()

    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {

        AsyncImage(
            model = request,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.715f),
            contentScale = ContentScale.Crop
        )
    }
}
