package com.piappstudio.a2ui.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NatureGreenPrimaryDark,
    onPrimary = NatureGreenOnPrimaryDark,
    primaryContainer = NatureGreenContainerDark,
    onPrimaryContainer = NatureGreenOnContainerDark,
    secondary = NatureTealSecondaryDark,
    onSecondary = NatureTealOnSecondaryDark,
    secondaryContainer = NatureTealContainerDark,
    onSecondaryContainer = NatureTealOnContainerDark,
    tertiary = NatureGoldTertiaryDark,
    onTertiary = NatureGoldOnTertiaryDark,
    tertiaryContainer = NatureGoldContainerDark,
    onTertiaryContainer = NatureGoldOnContainerDark,
    background = NatureBackgroundDark,
    surface = NatureSurfaceDark,
    surfaceVariant = NatureSurfaceVariantDark,
    onBackground = NatureOnSurfaceDark,
    onSurface = NatureOnSurfaceDark,
    onSurfaceVariant = NatureOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = NatureGreenPrimary,
    onPrimary = NatureGreenOnPrimary,
    primaryContainer = NatureGreenContainer,
    onPrimaryContainer = NatureGreenOnContainer,
    secondary = NatureTealSecondary,
    onSecondary = NatureTealOnSecondary,
    secondaryContainer = NatureTealContainer,
    onSecondaryContainer = NatureTealOnContainer,
    tertiary = NatureGoldTertiary,
    onTertiary = NatureGoldOnTertiary,
    tertiaryContainer = NatureGoldContainer,
    onTertiaryContainer = NatureGoldOnContainer,
    background = NatureBackgroundLight,
    surface = NatureSurfaceLight,
    surfaceVariant = NatureSurfaceVariantLight,
    onBackground = NatureOnSurfaceLight,
    onSurface = NatureOnSurfaceLight,
    onSurfaceVariant = NatureOnSurfaceVariantLight
)

@Composable
fun A2UITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
