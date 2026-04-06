package com.panmatsu.unigiri.scenes.battle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HPView(
    value: Int,
    isUpsideDown: Boolean,
    onChange: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable {
                    if (isUpsideDown) onChange(value + 10)
                    else onChange(value - 10)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isUpsideDown) "＋" else "ー",
                fontSize = 20.sp,)
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = value.toString(),
                fontSize = 32.sp,
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (isUpsideDown) 180f else 0f
                }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable {
                    if (isUpsideDown) onChange(value - 10)
                    else onChange(value + 10)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isUpsideDown) "ー" else "＋",
                fontSize = 20.sp,
            )
        }
    }
}