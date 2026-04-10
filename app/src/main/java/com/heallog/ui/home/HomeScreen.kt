package com.heallog.ui.home

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heallog.R
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.model.InjuryStatus
import com.heallog.ui.components.VoiceCommandFab
import com.heallog.ui.theme.HealLogSpacing
import com.heallog.ui.theme.HealLogTheme
import com.heallog.util.EmojiMapper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    onNavigateToBodyMap: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToNotificationSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val voiceFabEnabled by viewModel.voiceFabEnabled.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        voiceFabEnabled = voiceFabEnabled,
        onNavigateToBodyMap = onNavigateToBodyMap,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToNotificationSettings = onNavigateToNotificationSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    voiceFabEnabled: Boolean,
    onNavigateToBodyMap: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToNotificationSettings: () -> Unit = {}
) {
    val injuryCount = if (uiState is HomeUiState.Success) uiState.activeItems.size else 0
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("HealLog", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToNotificationSettings) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.cd_notification_settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                            contentDescription = stringResource(R.string.cd_injury_count),
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
                verticalArrangement = Arrangement.spacedBy(HealLogSpacing.ItemSpacing)
            ) {
                if (voiceFabEnabled) {
                    VoiceCommandFab(
                        onNavigateToBodyMap = onNavigateToBodyMap,
                        onShowMessage = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                }
                SmallFloatingActionButton(onClick = onNavigateToBodyMap) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_injury))
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
                activeItems = uiState.activeItems,
                healedItems = uiState.healedItems,
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
        Spacer(Modifier.height(HealLogSpacing.LargeSpacing))
        Text(
            text = stringResource(R.string.empty_no_injuries),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_go_to_bodymap),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onNavigateToBodyMap) {
            Text(stringResource(R.string.action_go_to_bodymap))
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
    activeItems: List<InjuryWithLatestLog>,
    healedItems: List<InjuryWithLatestLog>,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var healedExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(HealLogSpacing.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(HealLogSpacing.ItemSpacing)
    ) {
        if (activeItems.isEmpty() && healedItems.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_active_injuries),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        items(activeItems, key = { it.injury.id }) { item ->
            InjuryCard(
                item = item,
                onClick = { onNavigateToDetail(item.injury.id) }
            )
        }

        if (healedItems.isNotEmpty()) {
            item(key = "healed_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (activeItems.isNotEmpty()) 8.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.healed_records, healedItems.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { healedExpanded = !healedExpanded }) {
                        Text(stringResource(if (healedExpanded) R.string.action_collapse else R.string.action_expand))
                    }
                }
            }

            if (healedExpanded) {
                items(healedItems, key = { "healed_${it.injury.id}" }) { item ->
                    InjuryCard(
                        item = item,
                        onClick = { onNavigateToDetail(item.injury.id) }
                    )
                }
            }
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
                Text(EmojiMapper.getEmojiForBodyPart(item.injury.bodyPart), fontSize = 22.sp)
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
        painLevel <= 3 -> MaterialTheme.colorScheme.tertiary
        painLevel <= 6 -> MaterialTheme.colorScheme.secondary
        else           -> MaterialTheme.colorScheme.error
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
                activeItems = listOf(InjuryWithLatestLog(injury, log)),
                healedItems = emptyList()
            ),
            voiceFabEnabled = false,
            onNavigateToBodyMap = {},
            onNavigateToDetail = {},
            onNavigateToSettings = {}
        )
    }
}
