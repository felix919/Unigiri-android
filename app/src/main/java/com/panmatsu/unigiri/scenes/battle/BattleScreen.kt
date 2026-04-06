package com.panmatsu.unigiri.scenes.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BattleScreen(
    modifier: Modifier = Modifier
) {
    var topValue by remember { mutableStateOf(100) }
    var bottomValue by remember { mutableStateOf(100) }
    var selectedIndex by remember { mutableStateOf(4) }

    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x332196F3),
                        Color(0x332196F3),
                        Color(0x33FFF59D),
                        Color(0x33FFF59D)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            HPView(
                value = topValue,
                isUpsideDown = true,
                onChange = { topValue = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                CronusView(
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it },
                    onCenterTap = { showResetDialog = true }
                )
            }

            HPView(
                value = bottomValue,
                isUpsideDown = false,
                onChange = { bottomValue = it }
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        topValue = 100
                        bottomValue = 100
                        selectedIndex = 4
                        showResetDialog = false
                    }) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("No")
                    }
                },
                title = { Text("リセットしますか？") },
                text = { Text("HPとクロノスを対戦前に戻します") }
            )
        }
    }
}