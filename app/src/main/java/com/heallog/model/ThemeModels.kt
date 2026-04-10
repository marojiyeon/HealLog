package com.heallog.model

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class ColorScheme { HEALING_GREEN, MEDICAL_BLUE, WARMING_ORANGE, CALM_PURPLE, DYNAMIC }

enum class FontScale(val scale: Float) {
    SMALL(0.85f),
    NORMAL(1.0f),
    LARGE(1.15f),
    EXTRA_LARGE(1.3f)
}

data class AppThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorScheme: ColorScheme = ColorScheme.HEALING_GREEN,
    val useDynamicColor: Boolean = false,
    val fontScale: FontScale = FontScale.NORMAL,
    val highContrast: Boolean = false
)
