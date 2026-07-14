package com.panmatsu.unigiri.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 公式サイト準拠のパープル基調で全体を統一する
// surface系を未指定にするとM3デフォルトのほぼ黒になり、TopBar等が背景から浮くため明示する
private val DarkColorScheme = darkColorScheme(
    primary = LightPurple,
    onPrimary = Color.White,
    primaryContainer = LightPurple,
    onPrimaryContainer = Color.White,
    secondary = BrandGreen,
    onSecondary = Color.White,
    tertiary = Pink80,
    background = MainColor,
    onBackground = Color.White,

    // TopAppBar など
    surface = SurfacePurple,
    onSurface = Color.White,
    surfaceVariant = SurfacePurpleContainer,
    onSurfaceVariant = PurpleGrey80,

    // NavigationBar / DropdownMenu = surfaceContainer,
    // ModalBottomSheet = surfaceContainerLow, AlertDialog = surfaceContainerHigh
    surfaceContainerLowest = SurfacePurpleDark,
    surfaceContainerLow = SurfacePurple,
    surfaceContainer = SurfacePurpleContainer,
    surfaceContainerHigh = SurfacePurpleHigh,
    surfaceContainerHighest = SurfacePurpleHighest,

    outline = PurpleGrey80,
    outlineVariant = SurfacePurpleHighest,
)

@Composable
fun UnigiriTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
