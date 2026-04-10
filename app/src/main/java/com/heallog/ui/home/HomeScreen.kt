package com.heallog.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.model.InjuryStatus
import com.heallog.model.VoiceCommand
import com.heallog.ui.theme.HealLogTheme
import com.heallog.util.SpeechState
import com.heallog.util.VoiceCommandParser
import com.heallog.util.rememberSpeechRecognizerManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    onNavigateToBodyMap: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val voiceFabEnabled by viewModel.voiceFabEnabled.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        voiceFabEnabled = voiceFabEnabled,
        onNavigateToBodyMap = onNavigateToBodyMap,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToSettings = onNavigateToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    voiceFabEnabled: Boolean,
    onNavigateToBodyMap: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val injuryCount = if (uiState is HomeUiState.Success) uiState.items.size else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HealLog", fontWeight = FontWeight.Bold) },
                actions = {
                    BadgedBox(
                        badge = {
                            if (injuryCount > 0) {
                                Badge { Text(injuryCount.toString()) }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "부상 수",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    androidx.compose.material3.IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "설정",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (voiceFabEnabled) {
                    VoiceCommandFab(
                        onNavigateToBodyMap = onNavigateToBodyMap
                    )
                }
                FloatingActionButton(onClick = onNavigateToBodyMap) {
                    Icon(Icons.Default.Add, contentDescription = "부상 기록")
                }
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is HomeUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            is HomeUiState.Empty -> EmptyContent(
                onNavigateToBodyMap = onNavigateToBodyMap,
                modifier = Modifier.padding(innerPadding)
            )
            is HomeUiState.Success -> InjuryListContent(
                items = uiState.items,
                onNavigateToDetail = onNavigateToDetail,
                modifier = Modifier.padding(innerPadding)
            )
            is HomeUiState.Error -> ErrorContent(
                message = uiState.message,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    onNavigateToBodyMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
            )
            Text("🩺", fontSize = 36.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "부상 기록이 없어요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "바디맵에서 부상을 기록해보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onNavigateToBodyMap) {
            Text("바디맵으로 이동")
        }
    }
}

@Composable
private fun ErrorContent(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun InjuryListContent(
    items: List<InjuryWithLatestLog>,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.injury.id }) { item ->
            InjuryCard(
                item = item,
                onClick = { onNavigateToDetail(item.injury.id) }
            )
        }
    }
}

@Composable
private fun InjuryCard(
    item: InjuryWithLatestLog,
    onClick: () -> Unit
) {
    val daysSince = ChronoUnit.DAYS.between(item.injury.occurredAt, LocalDate.now())
    val currentPain = item.latestLog?.painLevel ?: item.injury.painLevel

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(bodyPartEmoji(item.injury.bodyPart), fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.injury.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${daysSince}일째",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                PainLevelBar(painLevel = currentPain)

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatusChip(status = item.injury.status)
                    Text(
                        text = "통증 $currentPain/10",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PainLevelBar(painLevel: Int, modifier: Modifier = Modifier) {
    val fraction = painLevel / 10f
    val barColor = when {
        painLevel <= 3 -> Color(0xFF4CAF50)
        painLevel <= 6 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(6.dp)
                .background(barColor)
        )
    }
}

@Composable
private fun StatusChip(status: InjuryStatus) {
    val label = when (status) {
        InjuryStatus.ACTIVE -> "진행 중"
        InjuryStatus.RECOVERING -> "회복 중"
        InjuryStatus.HEALED -> "완치"
    }
    val containerColor = when (status) {
        InjuryStatus.ACTIVE -> MaterialTheme.colorScheme.errorContainer
        InjuryStatus.RECOVERING -> MaterialTheme.colorScheme.tertiaryContainer
        InjuryStatus.HEALED -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when (status) {
        InjuryStatus.ACTIVE -> MaterialTheme.colorScheme.onErrorContainer
        InjuryStatus.RECOVERING -> MaterialTheme.colorScheme.onTertiaryContainer
        InjuryStatus.HEALED -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun VoiceCommandFab(
    onNavigateToBodyMap: () -> Unit
) {
    val context = LocalContext.current
    val manager = rememberSpeechRecognizerManager()
    val state by manager.state.collectAsState()
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) manager.startListening()
    }

    LaunchedEffect(state) {
        if (state is SpeechState.Result) {
            val text = (state as SpeechState.Result).text
            val command = VoiceCommandParser.parse(text)
            when (command) {
                is VoiceCommand.StartRecord -> onNavigateToBodyMap()
                is VoiceCommand.NavigateTo -> {
                    if (command.destination == "bodymap") onNavigateToBodyMap()
                }
                is VoiceCommand.GoHome -> {
                    Toast.makeText(context, "이미 홈 화면입니다", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "인식됨: $text", Toast.LENGTH_SHORT).show()
                }
            }
            manager.reset()
        }
    }

    if (!manager.isAvailable) return

    val isListening = state is SpeechState.Listening
    val isProcessing = state is SpeechState.Processing

    val infiniteTransition = rememberInfiniteTransition(label = "voice_fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_fab_scale"
    )

    SmallFloatingActionButton(
        onClick = {
            when {
                isListening || isProcessing -> manager.stopListening()
                hasPermission -> manager.startListening()
                else -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        containerColor = when {
            isListening -> MaterialTheme.colorScheme.error
            isProcessing -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        },
        modifier = if (isListening) Modifier.scale(pulseScale) else Modifier
    ) {
        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        } else {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (isListening) "음성 명령 인식 중" else "음성 명령",
                tint = if (isListening) MaterialTheme.colorScheme.onError
                else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

private fun bodyPartEmoji(bodyPartId: String): String = when (bodyPartId) {
    "head" -> "🧠"
    "neck" -> "🫙"
    "left_shoulder", "right_shoulder" -> "💪"
    "left_upper_arm", "right_upper_arm" -> "💪"
    "left_elbow", "right_elbow" -> "🦾"
    "left_forearm", "right_forearm" -> "🦾"
    "left_wrist", "right_wrist" -> "⌚"
    "left_hand", "right_hand" -> "🖐"
    "chest" -> "🫀"
    "abdomen" -> "🫁"
    "upper_back", "lower_back" -> "🔙"
    "left_hip", "right_hip" -> "🦴"
    "left_thigh", "right_thigh" -> "🦵"
    "left_knee", "right_knee" -> "🦵"
    "left_shin", "right_shin" -> "🦵"
    "left_ankle", "right_ankle" -> "🦶"
    "left_foot", "right_foot" -> "🦶"
    else -> "🩹"
}

// --- Previews ---

@Preview(showBackground = true, name = "Loading")
@Composable
private fun HomeLoadingPreview() {
    HealLogTheme {
        HomeContent(
            uiState = HomeUiState.Loading,
            voiceFabEnabled = false,
            onNavigateToBodyMap = {},
            onNavigateToDetail = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun HomeEmptyPreview() {
    HealLogTheme {
        HomeContent(
            uiState = HomeUiState.Empty,
            voiceFabEnabled = false,
            onNavigateToBodyMap = {},
            onNavigateToDetail = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(showBackground = true, name = "With injuries")
@Composable
private fun HomeSuccessPreview() {
    val injury = Injury(
        id = 1L,
        bodyPart = "left_knee",
        title = "왼쪽 무릎 인대",
        description = "계단 내려오다 삐끗",
        painLevel = 7,
        occurredAt = LocalDate.now().minusDays(12),
        createdAt = LocalDateTime.now().minusDays(12),
        status = InjuryStatus.RECOVERING
    )
    val log = PainLog(
        id = 1L, injuryId = 1L, painLevel = 5,
        note = "많이 나아짐", loggedAt = LocalDateTime.now()
    )
    HealLogTheme {
        HomeContent(
            uiState = HomeUiState.Success(
                listOf(InjuryWithLatestLog(injury, log))
            ),
            voiceFabEnabled = false,
            onNavigateToBodyMap = {},
            onNavigateToDetail = {},
            onNavigateToSettings = {}
        )
    }
}
