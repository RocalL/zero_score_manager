package com.zerogame.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Lime = Color(0xFFDBF827)
val Purple = Color(0xFF9F74D0)
val Pink = Color(0xFFDA8FCF)
val DarkPurple = Color(0xFF684A6D)
val MediumPurple = Color(0xFF795D93)
val Lavender = Color(0xFFE1D1E4)
val DarkBg = Color(0xFF0D0B14)
val DarkSurface = Color(0xFF1A1725)
val DarkCard = Color(0xFF241F33)

private val DarkColorScheme = darkColorScheme(
    primary = Lime,
    secondary = Purple,
    tertiary = Pink,
    background = DarkBg,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Lavender,
    outline = MediumPurple
)

private val LightColorScheme = lightColorScheme(
    primary = MediumPurple,
    secondary = Purple,
    tertiary = Pink,
    background = Color(0xFFF8F5FA),
    surface = Color.White,
    surfaceVariant = Lavender,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF060607),
    onSurface = Color(0xFF060607),
    onSurfaceVariant = DarkPurple,
    outline = MediumPurple
)

@Composable
fun ZeroGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
