package com.heallog.ui.detail.hospital

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Medication
import java.time.LocalDate

@Composable
fun HospitalTab(
    injuryId: Long,
    onAddVisit: (Long) -> Unit,
    onEditVisit: (Long, Long) -> Unit,
    onAddMedication: (Long) -> Unit,
    onEditMedication: (Long, Long) -> Unit,
    viewModel: HospitalTabViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val visits by viewModel.visits.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()

    var selectedVisitForMenu by rememberSaveable { mutableStateOf<HospitalVisit?>(null) }
    var deletingVisit by rememberSaveable { mutableStateOf<HospitalVisit?>(null) }

    var selectedMedicationForMenu by rememberSaveable { mutableStateOf<Medication?>(null) }
    var deletingMedication by rememberSaveable { mutableStateOf<Medication?>(null) }

    // Visit context menu
    if (selectedVisitForMenu != null) {
        val visit = selectedVisitForMenu!!
        AlertDialog(
            onDismissRequest = { selectedVisitForMenu = null },
            title = { Text("방문 기록") },
            text = { Text("이 기록을 어떻게 할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    onEditVisit(injuryId, visit.id)
                    selectedVisitForMenu = null
                }) { Text("수정") }
            },
            dismissButton = {
                TextButton(onClick = {
                    deletingVisit = visit
                    selectedVisitForMenu = null
                }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Visit delete confirmation
    if (deletingVisit != null) {
        AlertDialog(
            onDismissRequest = { deletingVisit = null },
            title = { Text("방문 기록 삭제") },
            text = { Text("이 기록을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteVisit(deletingVisit!!)
                    deletingVisit = null
                }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingVisit = null }) { Text("취소") }
            }
        )
    }

    // Medication context menu
    if (selectedMedicationForMenu != null) {
        val med = selectedMedicationForMenu!!
        AlertDialog(
            onDismissRequest = { selectedMedicationForMenu = null },
            title = { Text("약 정보") },
            text = { Text("이 약을 어떻게 할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    onEditMedication(injuryId, med.id)
                    selectedMedicationForMenu = null
                }) { Text("수정") }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (med.isActive) {
                        viewModel.toggleMedicationActive(med)
                    } else {
                        deletingMedication = med
                    }
                    selectedMedicationForMenu = null
                }) {
                    Text(if (med.isActive) "복용완료" else "삭제", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Medication delete confirmation
    if (deletingMedication != null) {
        AlertDialog(
            onDismissRequest = { deletingMedication = null },
            title = { Text("약 삭제") },
            text = { Text("이 약을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMedication(deletingMedication!!)
                    deletingMedication = null
                }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingMedication = null }) { Text("취소") }
            }
        )
    }

    if (visits.isEmpty() && medications.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "아직 기록이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "병원 방문",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onAddVisit(injuryId) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "방문 추가")
                    }
                }
            }

            if (visits.isNotEmpty()) {
                items(visits) { visit ->
                    HospitalVisitCard(
                        visit = visit,
                        medicationCount = medications.count { it.hospitalVisitId == visit.id },
                        onLongPress = { selectedVisitForMenu = visit },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                item {
                    Text(
                        "병원 방문 기록이 없습니다",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "복용 약",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onAddMedication(injuryId) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "약 추가")
                    }
                }
            }

            if (medications.isNotEmpty()) {
                items(medications) { med ->
                    MedicationCard(
                        medication = med,
                        onLongPress = { selectedMedicationForMenu = med },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                item {
                    Text(
                        "복용 약이 없습니다",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HospitalVisitCard(
    visit: HospitalVisit,
    medicationCount: Int,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongPress() })
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        visit.visitDate.toKoreanDateString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        visit.hospitalName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (visit.diagnosis != null && visit.diagnosis.isNotBlank()) {
                Text(
                    "진단: ${visit.diagnosis}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                visit.treatmentNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            if (visit.nextAppointment != null) {
                Text(
                    "다음 예약: ${visit.nextAppointment.toKoreanDateString()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (visit.cost != null) {
                Text(
                    "치료비: ${String.format("%,d", visit.cost)}원",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (medicationCount > 0) {
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        "연관 약 ${medicationCount}개",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongPress() })
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        medication.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${medication.dosage} · ${medication.frequency}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (medication.isActive)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        if (medication.isActive) "복용 중" else "복용 완료",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (medication.isActive)
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "${medication.startDate.toKoreanDateString()} ~ ${medication.endDate?.toKoreanDateString() ?: "진행 중"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (medication.sideEffectNote != null && medication.sideEffectNote.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "부작용: ${medication.sideEffectNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun LocalDate.toKoreanDateString(): String = "${year}년 ${monthValue}월 ${dayOfMonth}일"
