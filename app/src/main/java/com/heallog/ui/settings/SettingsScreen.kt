package com.heallog.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.ui.theme.HealLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToThemeSettings: () -> Unit,
    onNavigateToNotificationSettings: () -> Unit,
    onNavigateToDataExport: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val voiceFabEnabled by viewModel.voiceFabEnabled.collectAsStateWithLifecycle()
    var showAppInfoDialog by remember { mutableStateOf(false) }

    if (showAppInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAppInfoDialog = false },
            title = { Text("앱 정보") },
            text = {
                Column {
                    Text("HealLog")
                    Text("버전 1.0.0", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppInfoDialog = false }) { Text("확인") }
            }
        )
    }

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
                onClick = onNavigateToNotificationSettings
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "데이터 내보내기",
                subtitle = "부상 기록을 CSV/PDF로 내보내기",
                onClick = onNavigateToDataExport
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "개인정보 처리방침",
                subtitle = "앱 개인정보 처리방침 보기",
                onClick = onNavigateToPrivacyPolicy
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsItem(
                title = "앱 정보",
                subtitle = "버전 1.0.0",
                onClick = { showAppInfoDialog = true }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Text(
                text = "음성 인식",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "홈 화면 음성 명령 버튼",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "홈 화면에 음성 명령 FAB을 표시합니다",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = voiceFabEnabled,
                    onCheckedChange = viewModel::setVoiceFabEnabled
                )
            }

            HorizontalDivider()
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

@Preview(showBackground = true)
@Composable
private fun SettingsItemPreview() {
    HealLogTheme {
        Column {
            SettingsItem(title = "테마 설정", subtitle = "다크 모드, 색상, 글꼴 크기", onClick = {})
        }
    }
}
