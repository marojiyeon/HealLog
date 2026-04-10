package com.heallog.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.model.UserProfile
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val SPORTS_OPTIONS = listOf("축구", "농구", "러닝", "헬스", "수영", "자전거", "등산", "테니스", "기타")
private val BLOOD_TYPE_OPTIONS = listOf("A", "B", "AB", "O", "모름")
private val GENDER_OPTIONS = listOf("남성" to "MALE", "여성" to "FEMALE", "기타" to "OTHER")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    var editingProfile by remember { mutableStateOf(UserProfile()) }
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    // Initialize local state once from DataStore
    LaunchedEffect(profile) {
        if (!isInitialized) {
            editingProfile = profile
            isInitialized = true
        }
    }

    // Debounced auto-save
    LaunchedEffect(editingProfile) {
        if (isInitialized) {
            delay(500)
            viewModel.updateAll(editingProfile)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var bloodTypeExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val initialMillis = runCatching {
            LocalDate.parse(editingProfile.birthDate)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        editingProfile = editingProfile.copy(
                            birthDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        )
                    }
                    showDatePicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필 편집") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Nickname
            OutlinedTextField(
                value = editingProfile.nickname,
                onValueChange = { editingProfile = editingProfile.copy(nickname = it) },
                label = { Text("닉네임") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Birth date
            OutlinedTextField(
                value = editingProfile.birthDate,
                onValueChange = {},
                label = { Text("생년월일") },
                readOnly = true,
                placeholder = { Text("YYYY-MM-DD") },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) { Text("선택") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Gender
            Column {
                Text(
                    text = "성별",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    GENDER_OPTIONS.forEachIndexed { index, (label, value) ->
                        SegmentedButton(
                            selected = editingProfile.gender == value,
                            onClick = { editingProfile = editingProfile.copy(gender = value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = GENDER_OPTIONS.size
                            )
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            // Height & Weight
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = if (editingProfile.heightCm == 0f) "" else editingProfile.heightCm.toInt().toString(),
                    onValueChange = { raw ->
                        val v = raw.toIntOrNull()
                        if (v != null || raw.isEmpty()) {
                            editingProfile = editingProfile.copy(heightCm = v?.toFloat() ?: 0f)
                        }
                    },
                    label = { Text("키") },
                    suffix = { Text("cm") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = if (editingProfile.weightKg == 0f) "" else {
                        val w = editingProfile.weightKg
                        if (w == w.toInt().toFloat()) w.toInt().toString() else w.toString()
                    },
                    onValueChange = { raw ->
                        val v = raw.toFloatOrNull()
                        if (v != null || raw.isEmpty()) {
                            editingProfile = editingProfile.copy(weightKg = v ?: 0f)
                        }
                    },
                    label = { Text("몸무게") },
                    suffix = { Text("kg") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            // Blood type
            ExposedDropdownMenuBox(
                expanded = bloodTypeExpanded,
                onExpandedChange = { bloodTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = editingProfile.bloodType.ifEmpty { "모름" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("혈액형") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = bloodTypeExpanded,
                    onDismissRequest = { bloodTypeExpanded = false }
                ) {
                    BLOOD_TYPE_OPTIONS.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(if (option == "모름") "모름" else "${option}형") },
                            onClick = {
                                editingProfile = editingProfile.copy(
                                    bloodType = if (option == "모름") "" else option
                                )
                                bloodTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // Sports
            Column {
                Text(
                    text = "스포츠 / 운동",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SPORTS_OPTIONS.forEach { sport ->
                        val selected = sport in editingProfile.sports
                        FilterChip(
                            selected = selected,
                            onClick = {
                                editingProfile = editingProfile.copy(
                                    sports = if (selected) {
                                        editingProfile.sports - sport
                                    } else {
                                        editingProfile.sports + sport
                                    }
                                )
                            },
                            label = { Text(sport) }
                        )
                    }
                }
            }

            // Exercise frequency stepper
            Column {
                Text(
                    text = "주간 운동 횟수",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            if (editingProfile.exerciseFrequency > 0) {
                                editingProfile = editingProfile.copy(
                                    exerciseFrequency = editingProfile.exerciseFrequency - 1
                                )
                            }
                        },
                        enabled = editingProfile.exerciseFrequency > 0
                    ) {
                        Text("−", style = MaterialTheme.typography.titleLarge)
                    }
                    Text(
                        text = "${editingProfile.exerciseFrequency}일",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.widthIn(min = 48.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    FilledTonalIconButton(
                        onClick = {
                            if (editingProfile.exerciseFrequency < 7) {
                                editingProfile = editingProfile.copy(
                                    exerciseFrequency = editingProfile.exerciseFrequency + 1
                                )
                            }
                        },
                        enabled = editingProfile.exerciseFrequency < 7
                    ) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            // Medical conditions
            OutlinedTextField(
                value = editingProfile.medicalConditions,
                onValueChange = { editingProfile = editingProfile.copy(medicalConditions = it) },
                label = { Text("지병 / 기저질환") },
                placeholder = { Text("예: 고혈압, 당뇨") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            // Allergies
            OutlinedTextField(
                value = editingProfile.allergies,
                onValueChange = { editingProfile = editingProfile.copy(allergies = it) },
                label = { Text("알레르기") },
                placeholder = { Text("예: 페니실린, 땅콩") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}
