package com.heallog.ui.settings

import com.heallog.model.ColorScheme
import com.heallog.model.FontScale
import com.heallog.model.ThemeMode

/**
 * Centralized labels for theme settings.
 * Prevents duplication and ensures consistency across the settings screen.
 */
object ThemeLabels {

    fun getThemeModeLabel(mode: ThemeMode): String = when (mode) {
        ThemeMode.LIGHT -> "라이트"
        ThemeMode.DARK -> "다크"
        ThemeMode.SYSTEM -> "시스템"
    }

    fun getColorSchemeLabel(scheme: ColorScheme): String = when (scheme) {
        ColorScheme.HEALING_GREEN -> "힐링\n그린"
        ColorScheme.MEDICAL_BLUE -> "메디컬\n블루"
        ColorScheme.WARMING_ORANGE -> "워밍\n오렌지"
        ColorScheme.CALM_PURPLE -> "칼름\n퍼플"
        ColorScheme.DYNAMIC -> "동적"
    }

    fun getFontScaleLabel(scale: FontScale): String = when (scale) {
        FontScale.SMALL -> "작음"
        FontScale.NORMAL -> "보통"
        FontScale.LARGE -> "큼"
        FontScale.EXTRA_LARGE -> "매우큼"
    }
}
