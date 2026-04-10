package com.heallog.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.data.local.entity.NotificationSetting
import com.heallog.data.local.entity.NotificationType
import com.heallog.ui.theme.HealLogTheme
import kotlinx.serialization.json.Json

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // POST_NOTIFICATIONS permission launcher (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled silently; user can enable in settings */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    NotificationSettingsContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPainLogEnabledChange = viewModel::updatePainLogEnabled,
        onPainLogTimesChange = viewModel::updatePainLogTimes,
        onPainLogDaysChange = viewModel::updatePainLogDays,
        onHospitalEnabledChange = viewModel::updateHospitalEnabled,
        onMedicationEnabledChange = viewModel::updateMedicationEnabled,
        onMedicationTimesChange = viewModel::updateMedicationTimes,
        onDoNotDisturbChange = viewModel::updateDoNotDisturb
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationSettingsContent(
    uiState: NotificationSettingsUiState,
    onNavigateBack: () -> Unit,
    onPainLogEnabledChange: (Boolean) -> Unit,
    onPainLogTimesChange: (List<String>) -> Unit,
    onPainLogDaysChange: (List<Int>) -> Unit,
    onHospitalEnabledChange: (Boolean) -> Unit,
    onMedicationEnabledChange: (Boolean) -> Unit,
    onMedicationTimesChange: (List<String>) -> Unit,
    onDoNotDisturbChange: (String, String?, String?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알림 설정", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: 통증 기록 알림
            PainLogSection(
                setting = uiState.painLogSetting,
                onEnabledChange = onPainLogEnabledChange,
                onTimesChange = onPainLogTimesChange,
                onDaysChange = onPainLogDaysChange
            )

            HorizontalDivider()

            // Section 2: 병원 예약 알림
            HospitalSection(
                setting = uiState.hospitalSetting,
                onEnabledChange = onHospitalEnabledChange
            )

            HorizontalDivider()

            // Section 3: 약 복용 알림
            MedicationSection(
                setting = uiState.medicationSetting,
                onEnabledChange = onMedicationEnabledChange,
                onTimesChange = onMedicationTimesChange
            )

            HorizontalDivider()

            // Section 4: 방해 금지
            DoNotDisturbSection(
                painSetting = uiState.painLogSetting,
                onDoNotDisturbChange = onDoNotDisturbChange
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PainLogSection(
    setting: NotificationSetting,
    onEnabledChange: (Boolean) -> Unit,
    onTimesChange: (List<String>) -> Unit,
    onDaysChange: (List<Int>) -> Unit
) {
    val times = parseTimes(setting.times)
    val days = parseDays(setting.repeatDays)
    var showTimePicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "통증 기록 알림",
            isEnabled = setting.isEnabled,
            onEnabledChange = onEnabledChange
        )

        if (setting.isEnabled) {
            // Time chips
            Text(
                "알림 시간",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                times.forEach { time ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(time) },
                        trailingIcon = {
                            IconButton(onClick = {
                                onTimesChange(times - time)
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "삭제",
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }
                    )
                }
                FilterChip(
                    selected = false,
                    onClick = { showTimePicker = true },
                    label = { Text("+ 시간 추가") }
                )
            }

            // Day selector
            Text(
                "반복 요일",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DaySelector(selectedDays = days, onDaysChange = onDaysChange)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val newTime = "%02d:%02d".format(hour, minute)
                if (newTime !in times) onTimesChange(times + newTime)
                showTimePicker = false
            }
        )
    }
}

@Composable
private fun HospitalSection(
    setting: NotificationSetting,
    onEnabledChange: (Boolean) -> Unit
) {
    val advanceTimes = parseTimes(setting.times)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "병원 예약 알림",
            isEnabled = setting.isEnabled,
            onEnabledChange = onEnabledChange
        )

        if (setting.isEnabled) {
            Text(
                "사전 알림",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "부상 상세 화면에서 병원 예약 날짜를 등록하면 자동으로 알림이 발송됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MedicationSection(
    setting: NotificationSetting,
    onEnabledChange: (Boolean) -> Unit,
    onTimesChange: (List<String>) -> Unit
) {
    val times = parseTimes(setting.times)
    var showTimePicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "약 복용 알림",
            isEnabled = setting.isEnabled,
            onEnabledChange = onEnabledChange
        )

        if (setting.isEnabled) {
            Text(
                "복용 시간",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                times.forEach { time ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(time) },
                        trailingIcon = {
                            IconButton(onClick = { onTimesChange(times - time) }) {
                                Icon(Icons.Default.Close, contentDescription = "삭제")
                            }
                        }
                    )
                }
                FilterChip(
                    selected = false,
                    onClick = { showTimePicker = true },
                    label = { Text("+ 시간 추가") }
                )
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val newTime = "%02d:%02d".format(hour, minute)
                if (newTime !in times) onTimesChange(times + newTime)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoNotDisturbSection(
    painSetting: NotificationSetting,
    onDoNotDisturbChange: (String, String?, String?) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    val startTime = painSetting.doNotDisturbStart
    val endTime = painSetting.doNotDisturbEnd

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "방해 금지",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "설정한 시간 동안 모든 알림이 전송되지 않습니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = startTime != null,
                onClick = { showStartPicker = true },
                label = { Text(startTime ?: "시작 시간") },
                modifier = Modifier.weight(1f)
            )
            Text("~", style = MaterialTheme.typography.bodyMedium)
            FilterChip(
                selected = endTime != null,
                onClick = { showEndPicker = true },
                label = { Text(endTime ?: "종료 시간") },
                modifier = Modifier.weight(1f)
            )
        }

        if (startTime != null || endTime != null) {
            TextButton(
                onClick = {
                    listOf(NotificationType.PAIN_LOG, NotificationType.HOSPITAL, NotificationType.MEDICATION)
                        .forEach { type -> onDoNotDisturbChange(type, null, null) }
                }
            ) {
                Text("방해 금지 해제")
            }
        }
    }

    if (showStartPicker) {
        TimePickerDialog(
            onDismiss = { showStartPicker = false },
            onConfirm = { hour, minute ->
                val time = "%02d:%02d".format(hour, minute)
                listOf(NotificationType.PAIN_LOG, NotificationType.HOSPITAL, NotificationType.MEDICATION)
                    .forEach { type -> onDoNotDisturbChange(type, time, endTime) }
                showStartPicker = false
            }
        )
    }
    if (showEndPicker) {
        TimePickerDialog(
            onDismiss = { showEndPicker = false },
            onConfirm = { hour, minute ->
                val time = "%02d:%02d".format(hour, minute)
                listOf(NotificationType.PAIN_LOG, NotificationType.HOSPITAL, NotificationType.MEDICATION)
                    .forEach { type -> onDoNotDisturbChange(type, startTime, time) }
                showEndPicker = false
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Switch(checked = isEnabled, onCheckedChange = onEnabledChange)
    }
}

@Composable
private fun DaySelector(
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit
) {
    val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        dayLabels.forEachIndexed { index, label ->
            val dayValue = index + 1 // 1=Mon … 7=Sun
            val isSelected = selectedDays.isEmpty() || dayValue in selectedDays
            FilterChip(
                selected = isSelected,
                onClick = {
                    val allDays = (1..7).toList()
                    val newDays = if (selectedDays.isEmpty()) {
                        // Currently "every day" — deselect this one day
                        allDays - dayValue
                    } else if (dayValue in selectedDays) {
                        val updated = selectedDays - dayValue
                        if (updated.size == 7) emptyList() else updated
                    } else {
                        val updated = selectedDays + dayValue
                        if (updated.size == 7) emptyList() else updated
                    }
                    onDaysChange(newDays)
                },
                label = { Text(label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("시간 선택") },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

private fun parseTimes(json: String): List<String> = try {
    Json.decodeFromString(json)
} catch (e: Exception) { emptyList() }

private fun parseDays(json: String): List<Int> = try {
    Json.decodeFromString(json)
} catch (e: Exception) { emptyList() }

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsPreview() {
    HealLogTheme {
        NotificationSettingsContent(
            uiState = NotificationSettingsUiState(isLoading = false),
            onNavigateBack = {},
            onPainLogEnabledChange = {},
            onPainLogTimesChange = {},
            onPainLogDaysChange = {},
            onHospitalEnabledChange = {},
            onMedicationEnabledChange = {},
            onMedicationTimesChange = {},
            onDoNotDisturbChange = { _, _, _ -> }
        )
    }
}
