package com.example.wearalarm.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = Colors(
    primary = Color.White,
    primaryVariant = Color.Gray,
    secondary = Color.DarkGray,
    background = Color.Black, // ✅ Pure black background
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun WearAlarmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        content = content
    )
}
