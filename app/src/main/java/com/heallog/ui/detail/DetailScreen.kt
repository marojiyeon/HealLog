package com.heallog.ui.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import com.heallog.ui.components.VoiceInputField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.model.BodyParts
import com.heallog.model.ChartPeriod
import com.heallog.model.InjuryStatus
import com.heallog.model.PainChartPoint
import com.heallog.model.RecoveryStats
import com.heallog.model.RehabExercise
import com.heallog.ui.detail.chart.PainTrendChart
import com.heallog.ui.detail.chart.RecoveryProgressRing
import com.heallog.ui.detail.hospital.HospitalTab
import com.heallog.ui.detail.rehab.RehabGuideTab
import com.heallog.ui.theme.HealLogTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun InjuryDetailScreen(
    injuryId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (bodyPartId: String) -> Unit,
    onNavigateToAddVisit: (Long) -> Unit = {},
    onNavigateToEditVisit: (Long, Long) -> Unit = { _, _ -> },
    onNavigateToAddMedication: (Long) -> Unit = {},
    onNavigateToEditMedication: (Long, Long) -> Unit = { _, _ -> },
    viewModel: InjuryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val painChartData by viewModel.painChartData.collectAsStateWithLifecycle()
    val selectedChartPeriod by viewModel.selectedChartPeriod.collectAsStateWithLifecycle()
    val recoveryStats by viewModel.recoveryStats.collectAsStateWithLifecycle()
    val rehabExercises by viewModel.rehabExercises.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onNavigateBack() }
    }
    LaunchedEffect(Unit) {
        viewModel.logSaved.collect {
            snackbarHostState.showSnackbar("통증 기록이 추가되었습니다")
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToEdit.collect { bodyPartId -> onNavigateToEdit(bodyPartId) }
    }

    InjuryDetailContent(
        uiState = uiState,
        painChartData = painChartData,
        selectedChartPeriod = selectedChartPeriod,
        recoveryStats = recoveryStats,
        rehabExercises = rehabExercises,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onToggleLogForm = viewModel::toggleLogForm,
        onUpdateLogPainLevel = viewModel::updateLogPainLevel,
        onUpdateLogNote = viewModel::updateLogNote,
        onAddLogPhoto = viewModel::addLogPhoto,
        onRemoveLogPhoto = viewModel::removeLogPhoto,
        onAddPainLog = viewModel::addPainLog,
        onUpdateStatus = viewModel::updateInjuryStatus,
        onDeleteInjury = viewModel::deleteInjury,
        onEditInjury = viewModel::navigateToEditInjury,
        onUpdatePainLog = viewModel::updatePainLog,
        onDeletePainLog = viewModel::deletePainLog,
        onChartPeriodChanged = viewModel::onChartPeriodChanged,
        onNavigateToAddVisit = onNavigateToAddVisit,
        onNavigateToEditVisit = onNavigateToEditVisit,
        onNavigateToAddMedication = onNavigateToAddMedication,
        onNavigateToEditMedication = onNavigateToEditMedication
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InjuryDetailContent(
    uiState: InjuryDetailUiState,
    painChartData: List<PainChartPoint>,
    selectedChartPeriod: ChartPeriod,
    recoveryStats: RecoveryStats?,
    rehabExercises: List<RehabExercise>,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onToggleLogForm: () -> Unit,
    onUpdateLogPainLevel: (Int) -> Unit,
    onUpdateLogNote: (String) -> Unit,
    onAddLogPhoto: (Uri) -> Unit,
    onRemoveLogPhoto: (Uri) -> Unit,
    onAddPainLog: () -> Unit,
    onUpdateStatus: (InjuryStatus) -> Unit,
    onDeleteInjury: () -> Unit,
    onEditInjury: () -> Unit,
    onUpdatePainLog: (PainLog, Int, String, List<Uri>) -> Unit,
    onDeletePainLog: (PainLog) -> Unit,
    onChartPeriodChanged: (ChartPeriod) -> Unit,
    onNavigateToAddVisit: (Long) -> Unit = {},
    onNavigateToEditVisit: (Long, Long) -> Unit = { _, _ -> },
    onNavigateToAddMedication: (Long) -> Unit = {},
    onNavigateToEditMedication: (Long, Long) -> Unit = { _, _ -> }
) {
    var showOverflowMenu by rememberSaveable { mutableStateOf(false) }
    var showDeleteInjuryDialog by rememberSaveable { mutableStateOf(false) }

    // Pain log action state
    var selectedLogForMenu by remember { mutableStateOf<PainLog?>(null) }
    var editingLog by remember { mutableStateOf<PainLog?>(null) }
    var deletingLog by remember { mutableStateOf<PainLog?>(null) }

    // Delete injury confirmation dialog
    if (showDeleteInjuryDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteInjuryDialog = false },
            title = { Text("부상 기록 삭제") },
            text = { Text("이 부상 기록과 모든 통증 일지가 삭제됩니다. 계속하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = { onDeleteInjury(); showDeleteInjuryDialog = false }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteInjuryDialog = false }) { Text("취소") }
            }
        )
    }

    // Pain log context menu (long-press)
    if (selectedLogForMenu != null) {
        val log = selectedLogForMenu!!
        AlertDialog(
            onDismissRequest = { selectedLogForMenu = null },
            title = { Text("기록 관리") },
            text = { Text("이 통증 기록을 어떻게 할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    editingLog = log
                    selectedLogForMenu = null
                }) { Text("수정") }
            },
            dismissButton = {
                TextButton(onClick = {
                    deletingLog = log
                    selectedLogForMenu = null
                }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Pain log delete confirmation
    if (deletingLog != null) {
        AlertDialog(
            onDismissRequest = { deletingLog = null },
            title = { Text("통증 기록 삭제") },
            text = { Text("이 통증 기록을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeletePainLog(deletingLog!!)
                    deletingLog = null
                }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingLog = null }) { Text("취소") }
            }
        )
    }

    // Pain log edit dialog
    if (editingLog != null) {
        PainLogEditDialog(
            log = editingLog!!,
            onDismiss = { editingLog = null },
            onConfirm = { painLevel, note, photoUris ->
                onUpdatePainLog(editingLog!!, painLevel, note, photoUris)
                editingLog = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.injury?.title ?: "부상 상세") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    // Edit injury button
                    if (uiState.injury != null) {
                        IconButton(onClick = onEditInjury) {
                            Icon(Icons.Default.Edit, contentDescription = "부상 정보 수정")
                        }
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("부상 삭제", color = MaterialTheme.colorScheme.error)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showDeleteInjuryDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.error, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.injury != null -> {
                DetailBody(
                    injury = uiState.injury,
                    painLogs = uiState.painLogs,
                    formState = uiState.newLogForm,
                    painChartData = painChartData,
                    selectedChartPeriod = selectedChartPeriod,
                    recoveryStats = recoveryStats,
                    rehabExercises = rehabExercises,
                    onToggleLogForm = onToggleLogForm,
                    onUpdateLogPainLevel = onUpdateLogPainLevel,
                    onUpdateLogNote = onUpdateLogNote,
                    onAddLogPhoto = onAddLogPhoto,
                    onRemoveLogPhoto = onRemoveLogPhoto,
                    onAddPainLog = onAddPainLog,
                    onUpdateStatus = onUpdateStatus,
                    onLongPressLog = { log -> selectedLogForMenu = log },
                    onChartPeriodChanged = onChartPeriodChanged,
                    onNavigateToAddVisit = onNavigateToAddVisit,
                    onNavigateToEditVisit = onNavigateToEditVisit,
                    onNavigateToAddMedication = onNavigateToAddMedication,
                    onNavigateToEditMedication = onNavigateToEditMedication,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailBody(
    injury: Injury,
    painLogs: List<PainLog>,
    formState: NewLogFormState,
    painChartData: List<PainChartPoint>,
    selectedChartPeriod: ChartPeriod,
    recoveryStats: RecoveryStats?,
    rehabExercises: List<RehabExercise>,
    onToggleLogForm: () -> Unit,
    onUpdateLogPainLevel: (Int) -> Unit,
    onUpdateLogNote: (String) -> Unit,
    onAddLogPhoto: (Uri) -> Unit,
    onRemoveLogPhoto: (Uri) -> Unit,
    onAddPainLog: () -> Unit,
    onUpdateStatus: (InjuryStatus) -> Unit,
    onLongPressLog: (PainLog) -> Unit,
    onChartPeriodChanged: (ChartPeriod) -> Unit,
    onNavigateToAddVisit: (Long) -> Unit = {},
    onNavigateToEditVisit: (Long, Long) -> Unit = { _, _ -> },
    onNavigateToAddMedication: (Long) -> Unit = {},
    onNavigateToEditMedication: (Long, Long) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("통증 일지", "통증 차트", "병원 치료", "재활 가이드")

    Column(modifier = modifier.fillMaxSize()) {
        recoveryStats?.let {
            RecoveryProgressRing(stats = it)
        }

        androidx.compose.material3.TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                androidx.compose.material3.Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                PainLogTab(
                    injury = injury,
                    painLogs = painLogs,
                    formState = formState,
                    onToggleLogForm = onToggleLogForm,
                    onUpdateLogPainLevel = onUpdateLogPainLevel,
                    onUpdateLogNote = onUpdateLogNote,
                    onAddLogPhoto = onAddLogPhoto,
                    onRemoveLogPhoto = onRemoveLogPhoto,
                    onAddPainLog = onAddPainLog,
                    onUpdateStatus = onUpdateStatus,
                    onLongPressLog = onLongPressLog,
                    modifier = Modifier.fillMaxSize()
                )
            }
            1 -> {
                PainTrendChart(
                    chartPoints = painChartData,
                    selectedPeriod = selectedChartPeriod,
                    onPeriodChange = onChartPeriodChanged,
                    modifier = Modifier.fillMaxSize()
                )
            }
            2 -> {
                HospitalTab(
                    injuryId = injury.id,
                    onAddVisit = onNavigateToAddVisit,
                    onEditVisit = onNavigateToEditVisit,
                    onAddMedication = onNavigateToAddMedication,
                    onEditMedication = onNavigateToEditMedication,
                    modifier = Modifier.fillMaxSize()
                )
            }
            3 -> {
                RehabGuideTab(
                    exercises = rehabExercises,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PainLogTab(
    injury: Injury,
    painLogs: List<PainLog>,
    formState: NewLogFormState,
    onToggleLogForm: () -> Unit,
    onUpdateLogPainLevel: (Int) -> Unit,
    onUpdateLogNote: (String) -> Unit,
    onAddLogPhoto: (Uri) -> Unit,
    onRemoveLogPhoto: (Uri) -> Unit,
    onAddPainLog: () -> Unit,
    onUpdateStatus: (InjuryStatus) -> Unit,
    onLongPressLog: (PainLog) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            InjurySummaryCard(
                injury = injury,
                onUpdateStatus = onUpdateStatus,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            AddLogSection(
                formState = formState,
                onToggle = onToggleLogForm,
                onPainLevelChange = onUpdateLogPainLevel,
                onNoteChange = onUpdateLogNote,
                onAddPhoto = onAddLogPhoto,
                onRemovePhoto = onRemoveLogPhoto,
                onSubmit = onAddPainLog,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (painLogs.isNotEmpty()) {
            item {
                Text(
                    text = "통증 기록 (${painLogs.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp)
                )
            }

            itemsIndexed(painLogs) { index, log ->
                PainLogEntry(
                    log = log,
                    isLast = index == painLogs.lastIndex,
                    onLongPress = { onLongPressLog(log) }
                )
            }
        }
    }
}

// ── Summary Card ─────────────────────────────────────────────────────────────

@Composable
private fun InjurySummaryCard(
    injury: Injury,
    onUpdateStatus: (InjuryStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val bodyPartName = BodyParts.findById(injury.bodyPart)?.nameKo ?: injury.bodyPart
    val daysSince = java.time.temporal.ChronoUnit.DAYS.between(injury.occurredAt, LocalDate.now())
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = bodyPartName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = injury.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (injury.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = injury.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (injury.updatedAt != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "수정됨 · ${injury.updatedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = injury.occurredAt.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "D+$daysSince",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "상태",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(40.dp)
                )
                StatusDropdown(
                    current = injury.status,
                    onSelect = onUpdateStatus
                )
            }
        }
    }
}

@Composable
private fun StatusDropdown(
    current: InjuryStatus,
    onSelect: (InjuryStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val (containerColor, contentColor) = statusColors(current)

    Box {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            color = containerColor
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusLabel(current),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            InjuryStatus.entries.forEach { status ->
                val (dotColor, _) = statusColors(status)
                DropdownMenuItem(
                    text = { Text(statusLabel(status)) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    },
                    onClick = { onSelect(status); expanded = false }
                )
            }
        }
    }
}

// ── Add Log Section ───────────────────────────────────────────────────────────

@Composable
private fun AddLogSection(
    formState: NewLogFormState,
    onToggle: () -> Unit,
    onPainLevelChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onAddPhoto: (Uri) -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { onAddPhoto(it) } }

    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "오늘의 통증 기록",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (formState.isExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (formState.isExpanded) "접기" else "펼치기"
                )
            }

            AnimatedVisibility(visible = formState.isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(painEmoji(formState.painLevel), fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "통증 강도: ${formState.painLevel}/10",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = formState.painLevel.toFloat(),
                                onValueChange = { onPainLevelChange(it.roundToInt()) },
                                valueRange = 0f..10f,
                                steps = 9
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    VoiceInputField(
                        value = formState.note,
                        onValueChange = onNoteChange,
                        label = "메모",
                        placeholder = "오늘 상태를 기록하세요",
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(formState.photoUris) { uri ->
                            FormPhotoThumbnail(uri = uri, onRemove = { onRemovePhoto(uri) })
                        }
                        if (formState.photoUris.size < 3) {
                            item {
                                AddPhotoButton(
                                    onClick = {
                                        photoPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !formState.isSaving
                    ) {
                        if (formState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("기록 추가")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormPhotoThumbnail(uri: Uri, onRemove: () -> Unit) {
    var hasError by remember { mutableStateOf(false) }
    Box(modifier = Modifier.size(72.dp)) {
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("!", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
            }
        } else {
            AsyncImage(
                model = uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onError = { hasError = true },
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
            )
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "삭제",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun AddPhotoButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Add,
                contentDescription = "사진 추가",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Pain Log Timeline ─────────────────────────────────────────────────────────

private val logDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN)
private val timeFormatter = DateTimeFormatter.ofPattern("M월 d일 (E) a h:mm", Locale.KOREAN)

@Composable
private fun PainLogEntry(
    log: PainLog,
    isLast: Boolean,
    onLongPress: () -> Unit
) {
    val photos = log.photoUris?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(log.id) {
                detectTapGestures(onLongPress = { onLongPress() })
            }
            .padding(start = 16.dp, end = 16.dp)
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(10.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(painLevelColor(log.painLevel))
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = log.loggedAt.format(timeFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                PainBadge(painLevel = log.painLevel)
            }

            // Updated indicator
            if (log.updatedAt != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "기록: ${log.loggedAt.format(logDateFormatter)} · 수정: ${log.updatedAt.format(logDateFormatter)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            if (log.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = log.note,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (photos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(photos) { uriString ->
                        TimelinePhoto(uriString = uriString)
                    }
                }
            }
        }
    }
}

@Composable
private fun PainBadge(painLevel: Int) {
    val textColor = if (painLevel in 4..6) Color(0xFF3E2723) else Color.White
    Surface(
        shape = CircleShape,
        color = painLevelColor(painLevel)
    ) {
        Text(
            text = painLevel.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimelinePhoto(uriString: String) {
    var hasError by remember { mutableStateOf(false) }
    if (hasError) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("!", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
        }
    } else {
        AsyncImage(
            model = Uri.parse(uriString),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onError = { hasError = true },
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp))
        )
    }
}

// ── Pain Log Edit Dialog ──────────────────────────────────────────────────────

@Composable
private fun PainLogEditDialog(
    log: PainLog,
    onDismiss: () -> Unit,
    onConfirm: (painLevel: Int, note: String, photoUris: List<Uri>) -> Unit
) {
    val initialPhotos = remember(log.id) {
        log.photoUris?.split(",")?.filter { it.isNotBlank() }?.map { Uri.parse(it) } ?: emptyList()
    }

    var painLevel by remember { mutableIntStateOf(log.painLevel) }
    var note by remember { mutableStateOf(log.note) }
    var photoUris by remember { mutableStateOf(initialPhotos) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { if (photoUris.size < 3) photoUris = photoUris + it } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("통증 기록 수정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Pain level
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(painEmoji(painLevel), fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "통증 강도: $painLevel/10",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = painLevel.toFloat(),
                            onValueChange = { painLevel = it.roundToInt() },
                            valueRange = 0f..10f,
                            steps = 9
                        )
                    }
                }

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("메모") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                // Photos
                if (photoUris.isNotEmpty() || photoUris.size < 3) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(photoUris) { uri ->
                            FormPhotoThumbnail(uri = uri, onRemove = { photoUris = photoUris - uri })
                        }
                        if (photoUris.size < 3) {
                            item {
                                AddPhotoButton(
                                    onClick = {
                                        photoPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(painLevel, note, photoUris) }) {
                Text("수정 완료")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun statusLabel(status: InjuryStatus): String = when (status) {
    InjuryStatus.ACTIVE -> "진행 중"
    InjuryStatus.RECOVERING -> "회복 중"
    InjuryStatus.HEALED -> "완치"
}

@Composable
private fun statusColors(status: InjuryStatus): Pair<Color, Color> = when (status) {
    InjuryStatus.ACTIVE -> Pair(
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.onErrorContainer
    )
    InjuryStatus.RECOVERING -> Pair(
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer
    )
    InjuryStatus.HEALED -> Pair(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.onPrimaryContainer
    )
}

private fun painLevelColor(level: Int): Color = when {
    level <= 3 -> Color(0xFF4CAF50)
    level <= 6 -> Color(0xFFFFC107)
    else -> Color(0xFFF44336)
}

private fun painEmoji(level: Int): String = when (level) {
    0 -> "😊"
    in 1..3 -> "😐"
    in 4..6 -> "😣"
    in 7..9 -> "😫"
    else -> "🚨"
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Loading")
@Composable
private fun DetailLoadingPreview() {
    HealLogTheme {
        InjuryDetailContent(
            uiState = InjuryDetailUiState(isLoading = true),
            painChartData = emptyList(),
            selectedChartPeriod = com.heallog.model.ChartPeriod.WEEK,
            recoveryStats = null,
            rehabExercises = emptyList(),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {}, onToggleLogForm = {}, onUpdateLogPainLevel = {},
            onUpdateLogNote = {}, onAddLogPhoto = {}, onRemoveLogPhoto = {},
            onAddPainLog = {}, onUpdateStatus = {}, onDeleteInjury = {},
            onEditInjury = {}, onUpdatePainLog = { _, _, _, _ -> }, onDeletePainLog = {},
            onChartPeriodChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail with logs")
@Composable
private fun DetailSuccessPreview() {
    val injury = Injury(
        id = 1L, bodyPart = "left_knee", title = "왼쪽 무릎 인대",
        description = "계단 내려오다 삐끗했음",
        painLevel = 7,
        occurredAt = LocalDate.now().minusDays(14),
        createdAt = LocalDateTime.now().minusDays(14),
        status = InjuryStatus.RECOVERING
    )
    val logs = listOf(
        PainLog(
            id = 1L, injuryId = 1L, painLevel = 5, note = "많이 나아지는 중",
            loggedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now().plusHours(1)
        ),
        PainLog(id = 2L, injuryId = 1L, painLevel = 7, note = "아직 많이 아픔", loggedAt = LocalDateTime.now().minusDays(3))
    )
    HealLogTheme {
        InjuryDetailContent(
            uiState = InjuryDetailUiState(
                injury = injury, painLogs = logs,
                newLogForm = NewLogFormState(isExpanded = true, painLevel = 4),
                isLoading = false
            ),
            painChartData = emptyList(),
            selectedChartPeriod = com.heallog.model.ChartPeriod.WEEK,
            recoveryStats = null,
            rehabExercises = emptyList(),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {}, onToggleLogForm = {}, onUpdateLogPainLevel = {},
            onUpdateLogNote = {}, onAddLogPhoto = {}, onRemoveLogPhoto = {},
            onAddPainLog = {}, onUpdateStatus = {}, onDeleteInjury = {},
            onEditInjury = {}, onUpdatePainLog = { _, _, _, _ -> }, onDeletePainLog = {},
            onChartPeriodChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail collapsed form")
@Composable
private fun DetailCollapsedPreview() {
    val injury = Injury(
        id = 1L, bodyPart = "right_shoulder", title = "오른쪽 어깨 충돌",
        description = "", painLevel = 6,
        occurredAt = LocalDate.now().minusDays(3),
        createdAt = LocalDateTime.now().minusDays(3),
        status = InjuryStatus.ACTIVE
    )
    HealLogTheme {
        InjuryDetailContent(
            uiState = InjuryDetailUiState(injury = injury, isLoading = false),
            painChartData = emptyList(),
            selectedChartPeriod = com.heallog.model.ChartPeriod.WEEK,
            recoveryStats = null,
            rehabExercises = emptyList(),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {}, onToggleLogForm = {}, onUpdateLogPainLevel = {},
            onUpdateLogNote = {}, onAddLogPhoto = {}, onRemoveLogPhoto = {},
            onAddPainLog = {}, onUpdateStatus = {}, onDeleteInjury = {},
            onEditInjury = {}, onUpdatePainLog = { _, _, _, _ -> }, onDeletePainLog = {},
            onChartPeriodChanged = {}
        )
    }
}
