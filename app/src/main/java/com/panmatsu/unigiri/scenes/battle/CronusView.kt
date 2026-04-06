package com.panmatsu.unigiri.scenes.battle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.panmatsu.unigiri.R
import kotlin.math.*

@Composable
fun CronusView(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onCenterTap: () -> Unit
) {

    val divisions = 18

    fun calculateIndex(position: Offset, width: Float, height: Float): Int {
        val centerX = width / 2
        val centerY = height / 2

        val dx = position.x - centerX
        val dy = position.y - centerY

        var angle = Math.toDegrees(atan2(dy, dx).toDouble())
        angle += 180
        if (angle < 0) angle += 360.0
        val step = 360.0 / divisions
        return (angle / step).toInt().coerceIn(0, divisions - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val index = calculateIndex(
                        offset, size.width.toFloat(), size.height.toFloat()
                    )
                    onSelect(index)
                }
            }

            // ドラッグ対応
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val index = calculateIndex(
                        change.position, size.width.toFloat(), size.height.toFloat()
                    )
                    onSelect(index)
                }
            }
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val radius = size.minDimension / 2
            val step = 360f / divisions

            for (i in 0 until divisions) {

                val startAngle = step * i + 180f

                val color =
                    if (i == selectedIndex) Color(0xFFFF9800)
                    else if (i > 8) Color(0xFFFFF59D)
                    else Color(0xFF90CAF9)

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = step,
                    useCenter = true
                )
            }

            for (i in 0 until divisions) {

                val distance = (i - selectedIndex + divisions) % divisions
                if (distance == 0) continue

                val stepAngle = 360.0 / divisions
                val angle = stepAngle * (i + 0.5) + 180
                val rad = Math.toRadians(angle)

                val r = radius * 0.9

                val x = center.x + cos(rad) * r
                val y = center.y + sin(rad) * r

                drawContext.canvas.nativeCanvas.apply {

                    val paint = android.graphics.Paint().apply {
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 40f
                        alpha = ((1 - distance / divisions.toFloat()) * 255).toInt()
                    }

                    drawText("+$distance", x.toFloat(), y.toFloat(), paint)
                }
            }
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .background(Color.White, CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures {
                        onCenterTap()
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            if (selectedIndex > 8) {
                Icon(
                    painterResource(id = R.drawable.baseline_brightness),
                    contentDescription = null,
                    tint = colorResource(id = R.color.orange),
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Icon(
                    painterResource(id = R.drawable.baseline_night),
                    contentDescription = null,
                    tint = colorResource(id = R.color.indigo),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}