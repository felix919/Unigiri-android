package com.panmatsu.unigiri.scenes.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 検索条件ボトムシート
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val condition by viewModel.condition.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                // タイトル
                Text("検索条件", style = MaterialTheme.typography.titleLarge)
            }

            item {
                // フリーワード検索
                OutlinedTextField(
                    value = condition.keyword,
                    onValueChange = { viewModel.updateKeyword(it) },
                    label = { Text("フリーワード検索") },
                    singleLine = true,
                    trailingIcon = {
                        if (condition.keyword.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateKeyword("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "クリア"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // 属性
                FilterTypeSection(viewModel, condition)
            }

            item {
                // カードの種類
                FilterCardTypeSection(viewModel, condition)
            }

            item {
                // レアリティ
                FilterRareSection(viewModel, condition)
            }

            item {
                // パック名
                FilterPackSection(viewModel, condition)
            }

            item {
                // 楽曲名
                FilterSongSection(viewModel, condition)
            }

            item {
                // Send To Power
                FilterPowerSection(viewModel, condition)
            }

            item {
                // POWER COST
                FilterCostRangeSection(viewModel, condition)
            }

            item {
                // 攻撃力
                FilterAttackRangeSection(viewModel, condition)
            }

            item {
                // 絵師名
                FilterIllustratorSection(viewModel, condition)
            }
        }
    }
}

// レアリティ
@Composable
private fun FilterRareSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    Text("レアリティ")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelFilterChip(
            initialSelected = condition.rare.contains("UR"),
            title = "UR",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateRare("UR")
                } else {
                    viewModel.removeRare("UR")
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.rare.contains("SR"),
            title = "SR",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateRare("SR")
                } else {
                    viewModel.removeRare("SR")
                }

            }
        )
        LabelFilterChip(
            initialSelected = condition.rare.contains("R"),
            title = "R",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateRare("R")
                } else {
                    viewModel.removeRare("R")
                }

            }
        )
        LabelFilterChip(
            initialSelected = condition.rare.contains("N"),
            title = "N",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateRare("N")
                } else {
                    viewModel.removeRare("N")
                }

            }
        )
        LabelFilterChip(
            initialSelected = condition.rare.contains("SE"),
            title = "SE",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateRare("SE")
                } else {
                    viewModel.removeRare("SE")
                }

            }
        )
    }
}


// パック名
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterPackSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    val uiState by viewModel.uiState.collectAsState()
    val packList = listOf("すべて") + uiState.packList
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.padding(end = 16.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = condition.pack ?: "すべて",
            onValueChange = {},
            readOnly = true,
            label = { Text("パック名") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            packList.forEach { pack ->
                DropdownMenuItem(
                    text = { Text(pack) },
                    onClick = {
                        viewModel.updatePack(if (pack == "すべて") null else pack)
                        expanded = false
                    }
                )
            }
        }
    }
}


// 楽曲名
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSongSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    val uiState by viewModel.uiState.collectAsState()
    val songList = listOf("すべて") + uiState.songList.sorted()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.padding(end = 16.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = condition.song ?: "すべて",
            onValueChange = {},
            readOnly = true,
            label = { Text("楽曲名") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            songList.forEach { song ->
                DropdownMenuItem(
                    text = { Text(song) },
                    onClick = {
                        viewModel.updateSong(if (song == "すべて") null else song)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 種類
@Composable
private fun FilterCardTypeSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    Text("種類")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelFilterChip(
            initialSelected = condition.cardType.contains("Character"),
            title = "Character",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateCardType("Character")
                } else {
                    viewModel.removeCardType("Character")
                }
            }
        )

        LabelFilterChip(
            initialSelected = condition.cardType.contains("Enchant"),
            title = "Enchant",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateCardType("Enchant")
                } else {
                    viewModel.removeCardType("Enchant")
                }
            }
        )

        LabelFilterChip(
            initialSelected = condition.cardType.contains("Area Enchant"),
            title = "Area Enchant",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateCardType("Area Enchant")
                } else {
                    viewModel.removeCardType("Area Enchant")
                }
            }
        )
    }
}


// 属性
@Composable
private fun FilterTypeSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    Text("属性")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelFilterChip(
            initialSelected = condition.type.contains("炎"),
            title = "炎",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateType("炎")
                } else {
                    viewModel.removeType("炎")
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.type.contains("電気"),
            title = "電気",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateType("電気")
                } else {
                    viewModel.removeType("電気")
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.type.contains("闇"),
            title = "闇",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateType("闇")
                } else {
                    viewModel.removeType("闇")
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.type.contains("風"),
            title = "風",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateType("風")
                } else {
                    viewModel.removeType("風")
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.type.contains("カオス"),
            title = "カオス",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updateType("カオス")
                } else {
                    viewModel.removeType("カオス")
                }
            }
        )
    }
}

// Send To Power
@Composable
private fun FilterPowerSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    Text("SEND TO POWER")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelFilterChip(
            initialSelected = condition.power.contains(0),
            title = "0",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updatePower(0)
                } else {
                    viewModel.removePower(0)
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.power.contains(1),
            title = "1",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updatePower(1)
                } else {
                    viewModel.removePower(1)
                }
            }
        )
        LabelFilterChip(
            initialSelected = condition.power.contains(2),
            title = "2",
            onClick = { isSelected ->
                if (isSelected) {
                    viewModel.updatePower(2)
                } else {
                    viewModel.removePower(2)
                }
            }
        )
    }
}

// POWER COST
@Composable
private fun FilterCostRangeSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    // POWER COSTの最小値
    val min = 0f
    // POWER COSTの最大値
    val max = 10f
    // 1刻みでスライドバーを制御する
    val stepSize = 1f
    val steps = ((max - min) / stepSize).toInt() - 1

    // 初期値
    var range by remember {
        mutableStateOf(
            condition.costRange.toFloatRange()
        )
    }

    Text("POWER COST")

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 現在値（大きく）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${range.start.roundToInt()}")
            Text("${range.endInclusive.roundToInt()}")
        }

        RangeSlider(
            value = range,
            onValueChange = { range = it },
            onValueChangeFinished = {
                viewModel.updateCostRange(
                    range.start.roundToInt()..range.endInclusive.roundToInt()
                )
            },
            valueRange = min..max,
            steps = steps,
        )

        // 最小・最大（固定値）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${min.toInt()}")
            Text("${max.toInt()}")
        }
    }
}

// 攻撃力
@Composable
private fun FilterAttackRangeSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    // 攻撃力の最小値
    val min = 0f
    // 攻撃力の最大値
    val max = 250f
    // 10刻みでスライドバーを制御する
    val stepSize = 10f
    val steps = ((max - min) / stepSize).toInt() - 1

    // 初期値
    var range by remember {
        mutableStateOf(
            condition.attackRange.toFloatRange()
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("攻撃力")
        Spacer(modifier = Modifier.padding(horizontal = 12.dp))
        Switch(
            checked = condition.attackEnabled,
            onCheckedChange = {
                viewModel.togglePowerEnabled()
            },
            thumbContent = if (condition.attackEnabled) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else null
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = SearchCondition.AttackType.DAY in condition.attackType,
            onClick = {
                if (SearchCondition.AttackType.DAY in condition.attackType) {
                    viewModel.removeAttackType(SearchCondition.AttackType.DAY)
                } else {
                    viewModel.updateAttackType(SearchCondition.AttackType.DAY)
                }
            },
            enabled = condition.attackEnabled,
            label = { Text("昼") },
            leadingIcon = if (SearchCondition.AttackType.DAY in condition.attackType) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )

        FilterChip(
            selected = SearchCondition.AttackType.NIGHT in condition.attackType,
            onClick = {
                if (SearchCondition.AttackType.NIGHT in condition.attackType) {
                    viewModel.removeAttackType(SearchCondition.AttackType.NIGHT)
                } else {
                    viewModel.updateAttackType(SearchCondition.AttackType.NIGHT)
                }
            },
            enabled = condition.attackEnabled,
            label = { Text("夜") },
            leadingIcon = if (SearchCondition.AttackType.NIGHT in condition.attackType) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 現在値（大きく）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${range.start.roundToInt()}")
            Text("${range.endInclusive.roundToInt()}")
        }

        RangeSlider(
            value = range,
            onValueChange = { range = it },
            onValueChangeFinished = {
                viewModel.updateAttackRange(
                    range.start.roundToInt()..range.endInclusive.roundToInt()
                )
            },
            valueRange = min..max,
            steps = steps,
            enabled = condition.attackEnabled
        )

        // 最小・最大（固定値）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${min.toInt()}")
            Text("${max.toInt()}")
        }
    }
}

// 絵師名
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterIllustratorSection(
    viewModel: SearchViewModel,
    condition: SearchCondition
) {
    val uiState by viewModel.uiState.collectAsState()
    val illustratorList = listOf("すべて") + uiState.illustrator.sorted()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.padding(end = 16.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = condition.illustrator ?: "すべて",
            onValueChange = {},
            readOnly = true,
            label = { Text("Illustrator名") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            illustratorList.forEach { illustrator ->
                DropdownMenuItem(
                    text = { Text(illustrator) },
                    onClick = {
                        viewModel.updateIllustrator(if (illustrator == "すべて") null else illustrator)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun LabelFilterChip(
    initialSelected: Boolean,
    title: String,
    onClick: (Boolean) -> Unit
) {
    var selected by remember { mutableStateOf(initialSelected) }
    FilterChip(
        onClick = {
            selected = !selected
            onClick(selected)
        },
        label = {
            Text(title)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}
