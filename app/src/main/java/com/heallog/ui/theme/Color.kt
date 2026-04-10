package com.heallog.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── HealLog Medical Teal palette (default) ─────────────────────────────────

// Light theme
val MedicalTeal40 = Color(0xFF00796B)    // Teal 700  – primary
val MedicalGreen40 = Color(0xFF2E7D32)   // Green 800 – secondary
val SkyBlue40 = Color(0xFF0288D1)        // Light Blue 700 – tertiary

// Dark theme
val MedicalTeal80 = Color(0xFF80CBC4)    // Teal 200  – primary
val MedicalGreen80 = Color(0xFFA5D6A7)   // Green 200 – secondary
val SkyBlue80 = Color(0xFF81D4FA)        // Light Blue 200 – tertiary

// ── HEALING_GREEN Palette ─────────────────────────────────────────────────

val healingGreenLight = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF558B2F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCEF7A4),
    onSecondaryContainer = Color(0xFF33600D),
    tertiary = Color(0xFF0097A7),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF80DEEA),
    onTertiaryContainer = Color(0xFF00546A),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFEFDFB),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFEFDFB),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454E),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC7D0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFA5D6A7)
)

val healingGreenDark = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFCEF7A4),
    onSecondary = Color(0xFF33600D),
    secondaryContainer = Color(0xFF4A7C1E),
    onSecondaryContainer = Color(0xFFCEF7A4),
    tertiary = Color(0xFF80DEEA),
    onTertiary = Color(0xFF00546A),
    tertiaryContainer = Color(0xFF006B79),
    onTertiaryContainer = Color(0xFF80DEEA),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC7D0),
    outline = Color(0xFF95919B),
    outlineVariant = Color(0xFF49454E),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E6),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF2E7D32)
)

// ── MEDICAL_BLUE Palette ──────────────────────────────────────────────────

val medicalBlueLight = lightColorScheme(
    primary = Color(0xFF0097A7),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF80DEEA),
    onPrimaryContainer = Color(0xFF005662),
    secondary = Color(0xFF00695C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF80F0DD),
    onSecondaryContainer = Color(0xFF00201A),
    tertiary = Color(0xFF0288D1),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF81D4FA),
    onTertiaryContainer = Color(0xFF00416F),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFEFDFB),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFEFDFB),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE0F2F1),
    onSurfaceVariant = Color(0xFF004D54),
    outline = Color(0xFF72796F),
    outlineVariant = Color(0xFFA8D0D4),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFF80DEEA)
)

val medicalBlueDark = darkColorScheme(
    primary = Color(0xFF80DEEA),
    onPrimary = Color(0xFF005662),
    primaryContainer = Color(0xFF00758B),
    onPrimaryContainer = Color(0xFF80DEEA),
    secondary = Color(0xFF80F0DD),
    onSecondary = Color(0xFF00201A),
    secondaryContainer = Color(0xFF004D47),
    onSecondaryContainer = Color(0xFF80F0DD),
    tertiary = Color(0xFF81D4FA),
    onTertiary = Color(0xFF00416F),
    tertiaryContainer = Color(0xFF004B7B),
    onTertiaryContainer = Color(0xFF81D4FA),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF004D54),
    onSurfaceVariant = Color(0xFFA8D0D4),
    outline = Color(0xFF72796F),
    outlineVariant = Color(0xFF3F4945),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E6),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF0097A7)
)

// ── WARMING_ORANGE Palette ───────────────────────────────────────────────

val warmingOrangeLight = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFB74D),
    onPrimaryContainer = Color(0xFF7A2A00),
    secondary = Color(0xFFD84315),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFB3A1),
    onSecondaryContainer = Color(0xFF5E0800),
    tertiary = Color(0xFFFF6F00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFB74D),
    onTertiaryContainer = Color(0xFF332100),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFEFDFB),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFEFDFB),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFFFE0D9),
    onSurfaceVariant = Color(0xFF7A5B56),
    outline = Color(0xFF8B6D67),
    outlineVariant = Color(0xFFFFB3A1),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFFFB74D)
)

val warmingOrangeDark = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFF7A2A00),
    primaryContainer = Color(0xFFD84315),
    onPrimaryContainer = Color(0xFFFFB74D),
    secondary = Color(0xFFFFB3A1),
    onSecondary = Color(0xFF5E0800),
    secondaryContainer = Color(0xFF803E22),
    onSecondaryContainer = Color(0xFFFFB3A1),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF332100),
    tertiaryContainer = Color(0xFFC67B00),
    onTertiaryContainer = Color(0xFFFFB74D),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF7A5B56),
    onSurfaceVariant = Color(0xFFFFB3A1),
    outline = Color(0xFF8B6D67),
    outlineVariant = Color(0xFF5F463F),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E6),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFFE65100)
)

// ── CALM_PURPLE Palette ───────────────────────────────────────────────────

val calmPurpleLight = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE1BEE7),
    onPrimaryContainer = Color(0xFF4A0080),
    secondary = Color(0xFF512DA8),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF1A0033),
    tertiary = Color(0xFF00897B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB2EBE7),
    onTertiaryContainer = Color(0xFF005047),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFEFDFB),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFEFDFB),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFEBD5F0),
    onSurfaceVariant = Color(0xFF5F596B),
    outline = Color(0xFF79717D),
    outlineVariant = Color(0xFFD0C3DB),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFE1BEE7)
)

val calmPurpleDark = darkColorScheme(
    primary = Color(0xFFE1BEE7),
    onPrimary = Color(0xFF4A0080),
    primaryContainer = Color(0xFF7B1FA2),
    onPrimaryContainer = Color(0xFFE1BEE7),
    secondary = Color(0xFFEDE7F6),
    onSecondary = Color(0xFF1A0033),
    secondaryContainer = Color(0xFF3F2A70),
    onSecondaryContainer = Color(0xFFEDE7F6),
    tertiary = Color(0xFFB2EBE7),
    onTertiary = Color(0xFF005047),
    tertiaryContainer = Color(0xFF00695C),
    onTertiaryContainer = Color(0xFFB2EBE7),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF5F596B),
    onSurfaceVariant = Color(0xFFD0C3DB),
    outline = Color(0xFF9A91A3),
    outlineVariant = Color(0xFF49454E),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E6),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF7B1FA2)
)
