package com.heallog.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToThemeSettings: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SettingsItem(
                title = "테마 설정",
                subtitle = "다크 모드, 색상, 글꼴 크기",
                onClick = onNavigateToThemeSettings
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "알림 설정",
                subtitle = "복약 알림, 통증 기록 리마인더",
                onClick = { /* placeholder */ }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "데이터 내보내기",
                subtitle = "부상 기록을 CSV/PDF로 내보내기",
                onClick = { /* placeholder */ }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "개인정보 처리방침",
                subtitle = "앱 개인정보 처리방침 보기",
                onClick = { /* placeholder */ }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "앱 정보",
                subtitle = "버전 1.0.0",
                onClick = {}
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "›",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
