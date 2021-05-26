package com.example.myapplication.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.example.myapplication.ui.theme.*

private val DarkColorPalette = darkColors(
        primary = purple200,
        primaryVariant = purple700,
        secondary = teal200
)

private val LightColorPalette = lightColors(
    primary = primaryColor,
    primaryVariant = darkPrimaryColor,
    secondary = accentColor,
    background = background,
    surface = background,
    error = error,
    onPrimary = background,
    onSecondary = secondaryText,
    onBackground = secondaryText,
    onSurface = secondaryText,
    onError = background
)

@Composable
fun MyApplicationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(colors = colors, typography = typography, shapes = shapes, content = content)
}