package com.heallog.ui.detail.hospital

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.ui.theme.HealLogTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AddMedicationScreen(
    injuryId: Long,
    medicationId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AddMedicationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onNavigateBack() }
    }

    AddMedicationContent(
        uiState = uiState,
        frequencyOptions = viewModel.frequencyOptions,
        onNavigateBack = onNavigateBack,
        onNameChange = viewModel::updateName,
        onDosageChange = viewModel::updateDosage,
        onFrequencyChange = viewModel::updateFrequency,
        onStartDateChange = viewModel::updateStartDate,
        onEndDateChange = viewModel::updateEndDate,
        onSideEffectNoteChange = viewModel::updateSideEffectNote,
        onIsActiveChange = { viewModel.toggleIsActive() },
        onSave = viewModel::saveMedication
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicationContent(
    uiState: AddMedicationUiState,
    frequencyOptions: List<String>,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDosageChange: (String) -> Unit,
    onFrequencyChange: (String) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onSideEffectNoteChange: (String) -> Unit,
    onIsActiveChange: () -> Unit,
    onSave: () -> Unit
) {
    var showStartDatePicker by rememberSaveable { mutableStateOf(false) }
    var showEndDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showStartDatePicker) {
        DatePickerDialog(
            initialDate = uiState.startDate,
            onDateSelected = { date ->
                onStartDateChange(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false },
            title = "복용 시작일 선택"
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            initialDate = uiState.endDate ?: LocalDate.now(),
            onDateSelected = { date ->
                onEndDateChange(date)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false },
            title = "복용 종료일 선택"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "약 정보 수정" else "약 추가") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = { Text("약 이름*") },
                    placeholder = { Text("예: 아세트아미노펜") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.nameError,
                    supportingText = if (uiState.nameError) {
                        { Text("필수 입력 항목입니다") }
                    } else null
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.dosage,
                    onValueChange = onDosageChange,
                    label = { Text("용량*") },
                    placeholder = { Text("예: 500mg") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.dosageError,
                    supportingText = if (uiState.dosageError) {
                        { Text("필수 입력 항목입니다") }
                    } else null
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text("복용 횟수", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    frequencyOptions.forEach { option ->
                        FilterChip(
                            selected = uiState.frequency == option,
                            onClick = { onFrequencyChange(option) },
                            label = { Text(option) },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text("복용 시작일", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(uiState.startDate.toKoreanDateString())
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("복용 종료일", style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(onClick = { showEndDatePicker = true }) {
                        Text(uiState.endDate?.toKoreanDateString() ?: "없음")
                    }
                    if (uiState.endDate != null) {
                        TextButton(onClick = { onEndDateChange(null) }) {
                            Text("삭제")
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.sideEffectNote,
                    onValueChange = onSideEffectNoteChange,
                    label = { Text("부작용 메모") },
                    placeholder = { Text("부작용이나 주의사항을 입력하세요 (선택)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "현재 복용 중",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.isActive,
                        onCheckedChange = { onIsActiveChange() }
                    )
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving
                ) {
                    Text(if (uiState.isEditMode) "수정 완료" else "저장")
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun AddMedicationScreenPreview() {
    HealLogTheme {
        AddMedicationContent(
            uiState = AddMedicationUiState(),
            frequencyOptions = listOf("1일 1회", "1일 2회", "1일 3회", "필요시", "기타"),
            onNavigateBack = {},
            onNameChange = {},
            onDosageChange = {},
            onFrequencyChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSideEffectNoteChange = {},
            onIsActiveChange = {},
            onSave = {}
        )
    }
}
