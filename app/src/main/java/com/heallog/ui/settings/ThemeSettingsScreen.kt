package com.heallog.ui.settings

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.model.AppThemeSettings
import com.heallog.model.ColorScheme
import com.heallog.model.FontScale
import com.heallog.model.ThemeMode
import com.heallog.ui.theme.HealLogTheme
import com.heallog.ui.theme.calmPurpleDark
import com.heallog.ui.theme.calmPurpleLight
import com.heallog.ui.theme.healingGreenDark
import com.heallog.ui.theme.healingGreenLight
import com.heallog.ui.theme.medicalBlueDark
import com.heallog.ui.theme.medicalBlueLight
import com.heallog.ui.theme.warmingOrangeDark
import com.heallog.ui.theme.warmingOrangeLight

@Composable
fun ThemeSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ThemeSettingsViewModel = hiltViewModel()
) {
    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    ThemeSettingsContent(
        themeSettings = themeSettings,
        onNavigateBack = onNavigateBack,
        onThemeModeChanged = { viewModel.updateThemeMode(it) },
        onColorSchemeChanged = { viewModel.updateColorScheme(it) },
        onDynamicColorChanged = { viewModel.updateDynamicColor(it) },
        onFontScaleChanged = { viewModel.updateFontScale(it) },
        onHighContrastChanged = { viewModel.updateHighContrast(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSettingsContent(
    themeSettings: AppThemeSettings,
    onNavigateBack: () -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onColorSchemeChanged: (ColorScheme) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onFontScaleChanged: (FontScale) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("테마 설정", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ThemeModeSection(
                    selectedMode = themeSettings.themeMode,
                    onModeSelected = onThemeModeChanged
                )
            }

            item {
                ColorSchemeSection(
                    selectedScheme = themeSettings.colorScheme,
                    onSchemeSelected = onColorSchemeChanged,
                    useDynamicColor = themeSettings.useDynamicColor,
                    onDynamicColorChanged = onDynamicColorChanged
                )
            }

            item {
                FontScaleSection(
                    selectedScale = themeSettings.fontScale,
                    onScaleSelected = onFontScaleChanged
                )
            }

            item {
                HighContrastSection(
                    highContrast = themeSettings.highContrast,
                    onHighContrastChanged = onHighContrastChanged
                )
            }
        }
    }
}

@Composable
private fun ThemeModeSection(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Column {
        Text(
            text = "테마 모드",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.entries.forEach { mode ->
                ModeButton(
                    label = ThemeLabels.getThemeModeLabel(mode),
                    isSelected = selectedMode == mode,
                    onClick = { onModeSelected(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        border = if (!isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else {
            null
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ColorSchemeSection(
    selectedScheme: ColorScheme,
    onSchemeSelected: (ColorScheme) -> Unit,
    useDynamicColor: Boolean,
    onDynamicColorChanged: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "색상 스킴",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorScheme.entries.forEach { scheme ->
                if (scheme != ColorScheme.DYNAMIC) {
                    ColorSchemeOption(
                        scheme = scheme,
                        isSelected = selectedScheme == scheme,
                        onClick = { onSchemeSelected(scheme) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "동적 색상",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = useDynamicColor,
                    onCheckedChange = onDynamicColorChanged
                )
            }
        }
    }
}

@Composable
private fun ColorSchemeOption(
    scheme: ColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = when (scheme) {
        ColorScheme.HEALING_GREEN -> listOf(healingGreenLight.primary, healingGreenDark.primary)
        ColorScheme.MEDICAL_BLUE -> listOf(medicalBlueLight.primary, medicalBlueDark.primary)
        ColorScheme.WARMING_ORANGE -> listOf(warmingOrangeLight.primary, warmingOrangeDark.primary)
        ColorScheme.CALM_PURPLE -> listOf(calmPurpleLight.primary, calmPurpleDark.primary)
        ColorScheme.DYNAMIC -> listOf(Color.Gray, Color.DarkGray)
    }

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors[0]),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors[1])
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = ThemeLabels.getColorSchemeLabel(scheme),
            style = MaterialTheme.typography.labelSmall,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun FontScaleSection(
    selectedScale: FontScale,
    onScaleSelected: (FontScale) -> Unit
) {
    Column {
        Text(
            text = "글자 크기",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        val scales = FontScale.entries.toList()
        val currentIndex = scales.indexOf(selectedScale)

        Slider(
            value = currentIndex.toFloat(),
            onValueChange = { newIndex ->
                onScaleSelected(scales[newIndex.toInt()])
            },
            valueRange = 0f..(scales.size - 1).toFloat(),
            steps = scales.size - 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            scales.forEach { scale ->
                Text(
                    text = ThemeLabels.getFontScaleLabel(scale),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = (MaterialTheme.typography.labelSmall.fontSize * scale.scale)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "글자 크기 미리보기입니다",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = (MaterialTheme.typography.bodyMedium.fontSize * selectedScale.scale)
            )
        }
    }
}

@Composable
private fun HighContrastSection(
    highContrast: Boolean,
    onHighContrastChanged: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "고대비 모드",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "고대비 모드 활성화",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "시각 장애인을 위한 더 높은 명암 지원",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = highContrast,
                onCheckedChange = onHighContrastChanged
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeSettingsScreenPreview() {
    HealLogTheme {
        ThemeSettingsContent(
            themeSettings = AppThemeSettings(),
            onNavigateBack = {},
            onThemeModeChanged = {},
            onColorSchemeChanged = {},
            onDynamicColorChanged = {},
            onFontScaleChanged = {},
            onHighContrastChanged = {}
        )
    }
}
