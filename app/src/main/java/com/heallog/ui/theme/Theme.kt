package com.heallog.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.heallog.model.*

@Composable
fun HealLogTheme(
    themeSettings: AppThemeSettings = AppThemeSettings(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        themeSettings.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> when (themeSettings.colorScheme) {
            ColorScheme.HEALING_GREEN -> if (darkTheme) HealingGreenDark else HealingGreenLight
            ColorScheme.MEDICAL_BLUE -> if (darkTheme) MedicalBlueDark else MedicalBlueLight
            ColorScheme.WARMING_ORANGE -> if (darkTheme) WarmingOrangeDark else WarmingOrangeLight
            ColorScheme.CALM_PURPLE -> if (darkTheme) CalmPurpleDark else CalmPurpleLight
            ColorScheme.DYNAMIC -> if (darkTheme) HealingGreenDark else HealingGreenLight // fallback
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(themeSettings.fontScale),
        content = content
    )
}
