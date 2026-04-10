package com.heallog.ui.record

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.heallog.ui.components.VoiceInputField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.heallog.ui.theme.HealLogTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

// Constants
private const val MAX_PHOTOS = 5

// ---------------------------------------------------------------------------
// Screen — wires ViewModel, side effects, activity-level launchers
// ---------------------------------------------------------------------------

@Composable
fun RecordInjuryScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecordInjuryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate back after a successful save
    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onNavigateBack() }
    }

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { viewModel.addPhoto(it) } }

    RecordInjuryContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription,
        onPainLevelChange = viewModel::updatePainLevel,
        onDateChange = viewModel::updateDate,
        onAddPhoto = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRemovePhoto = viewModel::removePhoto,
        onSave = viewModel::saveInjury
    )
}

// ---------------------------------------------------------------------------
// Content — pure composable, previewable
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordInjuryContent(
    uiState: RecordInjuryUiState,
    onNavigateBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPainLevelChange: (Int) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    onSave: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "부상 정보 수정"
                        else "${uiState.bodyPartNameKo} 부상 기록"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                FormSection(title = "부상 제목") {
                    VoiceInputField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        placeholder = "부상 제목을 입력하세요",
                        isError = uiState.titleError,
                        supportingText = if (uiState.titleError) {
                            { Text("제목을 입력해주세요") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                FormSection(title = "부상 설명") {
                    VoiceInputField(
                        value = uiState.description,
                        onValueChange = onDescriptionChange,
                        placeholder = "어떻게 다쳤는지 자세히 적어주세요",
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                FormSection(title = "통증 수준") {
                    PainLevelSection(
                        painLevel = uiState.painLevel,
                        isPainLevelTouched = uiState.isPainLevelTouched,
                        hasError = uiState.painLevelError,
                        onPainLevelChange = onPainLevelChange
                    )
                }
            }

            item {
                FormSection(title = "부상 날짜") {
                    DateSection(
                        date = uiState.occurredDate,
                        onClick = { showDatePicker = true }
                    )
                }
            }

            item {
                FormSection(title = "사진 첨부 (최대 ${MAX_PHOTOS}장)") {
                    PhotoSection(
                        photoUris = uiState.photoUris,
                        onAddPhoto = onAddPhoto,
                        onRemovePhoto = onRemovePhoto
                    )
                }
            }

            item {
                Button(
                    onClick = onSave,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when {
                            uiState.isSaving && uiState.isEditMode -> "수정 중..."
                            uiState.isSaving -> "저장 중..."
                            uiState.isEditMode -> "수정 완료"
                            else -> "저장"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.occurredDate.toEpochDay() * 86_400_000L
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateChange(LocalDate.ofEpochDay(millis / 86_400_000L))
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
}

// ---------------------------------------------------------------------------
// Form section wrapper
// ---------------------------------------------------------------------------

@Composable
private fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}

// ---------------------------------------------------------------------------
// Pain level slider
// ---------------------------------------------------------------------------

private data class PainDescriptor(val emoji: String, val label: String)

private fun painDescriptor(level: Int): PainDescriptor = when (level) {
    0        -> PainDescriptor("😊", "통증 없음")
    in 1..3  -> PainDescriptor("😐", "가벼운 통증")
    in 4..6  -> PainDescriptor("😣", "중간 통증")
    in 7..9  -> PainDescriptor("😫", "심한 통증")
    else     -> PainDescriptor("🚨", "극심한 통증")
}

@Composable
private fun PainLevelSection(
    painLevel: Int,
    isPainLevelTouched: Boolean,
    hasError: Boolean,
    onPainLevelChange: (Int) -> Unit
) {
    val descriptor = painDescriptor(painLevel)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (hasError)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Emoji + label + number
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPainLevelTouched) descriptor.emoji else "🤔",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isPainLevelTouched) descriptor.label else "슬라이더를 움직여 통증 수준을 선택하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasError)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    if (hasError) {
                        Text(
                            text = "통증 수준을 선택해주세요",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                if (isPainLevelTouched) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$painLevel",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Slider(
                value = painLevel.toFloat(),
                onValueChange = { onPainLevelChange(it.roundToInt()) },
                valueRange = 0f..10f,
                steps = 9,    // 9 internal steps → 11 snap points: 0,1,...,10
                modifier = Modifier.fillMaxWidth()
            )

            // Min / max labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("10", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Date section
// ---------------------------------------------------------------------------

private val KoreanDateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)

@Composable
private fun DateSection(
    date: LocalDate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date.format(KoreanDateFormatter),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "변경",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Photo section
// ---------------------------------------------------------------------------

@Composable
private fun PhotoSection(
    photoUris: List<Uri>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Uri) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Existing photos
        photoUris.forEach { uri ->
            PhotoThumbnail(uri = uri, onRemove = { onRemovePhoto(uri) })
        }

        // Add button (shown until limit reached)
        if (photoUris.size < MAX_PHOTOS) {
            AddPhotoButton(onClick = onAddPhoto)
        }
    }

    if (photoUris.isNotEmpty()) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${photoUris.size}/$MAX_PHOTOS 장 첨부됨",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PhotoThumbnail(uri: Uri, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "첨부 사진",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Scrim so the close button is always visible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.08f))
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Black.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "사진 삭제",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun AddPhotoButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Add,
                contentDescription = "사진 추가",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "추가",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun RecordInjuryScreenPreview_Empty() {
    HealLogTheme {
        RecordInjuryContent(
            uiState = RecordInjuryUiState(
                bodyPartId = "left_knee",
                bodyPartNameKo = "왼쪽 무릎"
            ),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onPainLevelChange = {},
            onDateChange = {},
            onAddPhoto = {},
            onRemovePhoto = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun RecordInjuryScreenPreview_WithErrors() {
    HealLogTheme {
        RecordInjuryContent(
            uiState = RecordInjuryUiState(
                bodyPartId = "right_shoulder",
                bodyPartNameKo = "오른쪽 어깨",
                titleError = true,
                painLevelError = true
            ),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onPainLevelChange = {},
            onDateChange = {},
            onAddPhoto = {},
            onRemovePhoto = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun RecordInjuryScreenPreview_Filled() {
    HealLogTheme {
        RecordInjuryContent(
            uiState = RecordInjuryUiState(
                bodyPartId = "left_knee",
                bodyPartNameKo = "왼쪽 무릎",
                title = "왼쪽 무릎 인대 부상",
                description = "축구 경기 중 태클을 당해 무릎 인대가 늘어났습니다.",
                painLevel = 7,
                isPainLevelTouched = true,
                occurredDate = LocalDate.of(2026, 4, 10)
            ),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onPainLevelChange = {},
            onDateChange = {},
            onAddPhoto = {},
            onRemovePhoto = {},
            onSave = {}
        )
    }
}
