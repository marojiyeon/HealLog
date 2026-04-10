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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
fun AddVisitScreen(
    injuryId: Long,
    visitId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AddVisitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onNavigateBack() }
    }

    AddVisitContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onHospitalNameChange = viewModel::updateHospitalName,
        onDoctorNameChange = viewModel::updateDoctorName,
        onVisitDateChange = viewModel::updateVisitDate,
        onDiagnosisChange = viewModel::updateDiagnosis,
        onTreatmentNoteChange = viewModel::updateTreatmentNote,
        onNextAppointmentChange = viewModel::updateNextAppointment,
        onCostChange = viewModel::updateCost,
        onIsInsuranceCoveredChange = viewModel::updateIsInsuranceCovered,
        onSave = viewModel::saveVisit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVisitContent(
    uiState: AddVisitUiState,
    onNavigateBack: () -> Unit,
    onHospitalNameChange: (String) -> Unit,
    onDoctorNameChange: (String) -> Unit,
    onVisitDateChange: (LocalDate) -> Unit,
    onDiagnosisChange: (String) -> Unit,
    onTreatmentNoteChange: (String) -> Unit,
    onNextAppointmentChange: (LocalDate?) -> Unit,
    onCostChange: (String) -> Unit,
    onIsInsuranceCoveredChange: (Boolean?) -> Unit,
    onSave: () -> Unit
) {
    var showVisitDatePicker by rememberSaveable { mutableStateOf(false) }
    var showNextAppointmentPicker by rememberSaveable { mutableStateOf(false) }

    if (showVisitDatePicker) {
        DatePickerDialog(
            initialDate = uiState.visitDate,
            onDateSelected = { date ->
                onVisitDateChange(date)
                showVisitDatePicker = false
            },
            onDismiss = { showVisitDatePicker = false }
        )
    }

    if (showNextAppointmentPicker) {
        DatePickerDialog(
            initialDate = uiState.nextAppointment ?: LocalDate.now(),
            onDateSelected = { date ->
                onNextAppointmentChange(date)
                showNextAppointmentPicker = false
            },
            onDismiss = { showNextAppointmentPicker = false },
            title = "다음 예약일 선택"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "방문 기록 수정" else "병원 방문 추가") },
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
                    value = uiState.hospitalName,
                    onValueChange = onHospitalNameChange,
                    label = { Text("병원 이름*") },
                    placeholder = { Text("예: 서울대학교 병원") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.hospitalNameError,
                    supportingText = if (uiState.hospitalNameError) {
                        { Text("필수 입력 항목입니다") }
                    } else null
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.doctorName,
                    onValueChange = onDoctorNameChange,
                    label = { Text("담당 의사") },
                    placeholder = { Text("예: 김의사") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text("방문 날짜*", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { showVisitDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(uiState.visitDate.toKoreanDateString())
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.diagnosis,
                    onValueChange = onDiagnosisChange,
                    label = { Text("진단명") },
                    placeholder = { Text("예: 발목 염좌") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.treatmentNote,
                    onValueChange = onTreatmentNoteChange,
                    label = { Text("진료 내용*") },
                    placeholder = { Text("진료 내용을 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = uiState.treatmentNoteError,
                    supportingText = if (uiState.treatmentNoteError) {
                        { Text("필수 입력 항목입니다") }
                    } else null
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("다음 예약일", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = { showNextAppointmentPicker = true }) {
                        Text(uiState.nextAppointment?.toKoreanDateString() ?: "없음")
                    }
                    if (uiState.nextAppointment != null) {
                        TextButton(onClick = { onNextAppointmentChange(null) }) {
                            Text("삭제")
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.cost,
                    onValueChange = onCostChange,
                    label = { Text("치료비") },
                    placeholder = { Text("예: 50000") },
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text("원") }
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text("보험 적용", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf(null, true, false).forEach { value ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = uiState.isInsuranceCovered == value,
                                onClick = { onIsInsuranceCoveredChange(value) }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                when (value) {
                                    null -> "해당없음"
                                    true -> "적용"
                                    false -> "미적용"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    title: String = "날짜 선택"
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            DatePicker(state = datePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                if (selectedMillis != null) {
                    val date = Instant.ofEpochMilli(selectedMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateSelected(date)
                }
            }) { Text("확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Preview
@Composable
fun AddVisitScreenPreview() {
    HealLogTheme {
        AddVisitContent(
            uiState = AddVisitUiState(),
            onNavigateBack = {},
            onHospitalNameChange = {},
            onDoctorNameChange = {},
            onVisitDateChange = {},
            onDiagnosisChange = {},
            onTreatmentNoteChange = {},
            onNextAppointmentChange = {},
            onCostChange = {},
            onIsInsuranceCoveredChange = {},
            onSave = {}
        )
    }
}
