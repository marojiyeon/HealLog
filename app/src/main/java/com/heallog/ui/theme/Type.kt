package com.heallog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.heallog.model.FontScale

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

fun appTypography(fontScale: FontScale): Typography {
    return Typography(
        displayLarge = Typography.displayLarge.copy(
            fontSize = Typography.displayLarge.fontSize * fontScale.scale
        ),
        displayMedium = Typography.displayMedium.copy(
            fontSize = Typography.displayMedium.fontSize * fontScale.scale
        ),
        displaySmall = Typography.displaySmall.copy(
            fontSize = Typography.displaySmall.fontSize * fontScale.scale
        ),
        headlineLarge = Typography.headlineLarge.copy(
            fontSize = Typography.headlineLarge.fontSize * fontScale.scale
        ),
        headlineMedium = Typography.headlineMedium.copy(
            fontSize = Typography.headlineMedium.fontSize * fontScale.scale
        ),
        headlineSmall = Typography.headlineSmall.copy(
            fontSize = Typography.headlineSmall.fontSize * fontScale.scale
        ),
        titleLarge = Typography.titleLarge.copy(
            fontSize = Typography.titleLarge.fontSize * fontScale.scale
        ),
        titleMedium = Typography.titleMedium.copy(
            fontSize = Typography.titleMedium.fontSize * fontScale.scale
        ),
        titleSmall = Typography.titleSmall.copy(
            fontSize = Typography.titleSmall.fontSize * fontScale.scale
        ),
        bodyLarge = Typography.bodyLarge.copy(
            fontSize = Typography.bodyLarge.fontSize * fontScale.scale
        ),
        bodyMedium = Typography.bodyMedium.copy(
            fontSize = Typography.bodyMedium.fontSize * fontScale.scale
        ),
        bodySmall = Typography.bodySmall.copy(
            fontSize = Typography.bodySmall.fontSize * fontScale.scale
        ),
        labelLarge = Typography.labelLarge.copy(
            fontSize = Typography.labelLarge.fontSize * fontScale.scale
        ),
        labelMedium = Typography.labelMedium.copy(
            fontSize = Typography.labelMedium.fontSize * fontScale.scale
        ),
        labelSmall = Typography.labelSmall.copy(
            fontSize = Typography.labelSmall.fontSize * fontScale.scale
        )
    )
}
