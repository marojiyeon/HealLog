package com.heallog.ui.profile

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.heallog.model.BodyParts
import com.heallog.model.UserProfile
import com.heallog.ui.theme.HealLogTheme
import java.time.LocalDate
import java.time.Period

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val injuryStats by viewModel.injuryStats.collectAsStateWithLifecycle()

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfileImageUri(it.toString()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 정보") },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "프로필 편집")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeader(
                    profile = userProfile,
                    onPhotoClick = { photoLauncher.launch("image/*") }
                )
            }
            item { BodyInfoCard(profile = userProfile) }
            item { SportsInfoCard(profile = userProfile) }
            item { MedicalInfoCard(profile = userProfile) }
            item { InjuryStatsCard(stats = injuryStats) }
            item {
                Button(
                    onClick = onNavigateToEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("프로필 편집")
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    onPhotoClick: () -> Unit
) {
    val age = remember(profile.birthDate) {
        if (profile.birthDate.isEmpty()) null
        else runCatching {
            Period.between(LocalDate.parse(profile.birthDate), LocalDate.now()).years
        }.getOrNull()
    }

    val genderLabel = when (profile.gender) {
        "MALE" -> "남성"
        "FEMALE" -> "여성"
        "OTHER" -> "기타"
        else -> null
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable(onClick = onPhotoClick),
            contentAlignment = Alignment.Center
        ) {
            if (profile.profileImageUri.isNotEmpty()) {
                AsyncImage(
                    model = profile.profileImageUri,
                    contentDescription = "프로필 사진",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (profile.nickname.isNotEmpty()) profile.nickname else "닉네임 없음",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        val subtitle = buildString {
            if (age != null) append("${age}세")
            if (age != null && genderLabel != null) append("  ·  ")
            if (genderLabel != null) append(genderLabel)
        }
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BodyInfoCard(profile: UserProfile) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "신체 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BodyStatItem(
                    label = "키",
                    value = if (profile.heightCm > 0f) "${profile.heightCm.toInt()} cm" else "—"
                )
                BodyStatItem(
                    label = "체중",
                    value = if (profile.weightKg > 0f) "${profile.weightKg.toInt()} kg" else "—"
                )
                val bmi = profile.bmi
                val bmiLabel = when {
                    bmi <= 0f -> "—"
                    else -> "%.1f".format(bmi)
                }
                val bmiColor = bmiColor(bmi)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = bmiLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (bmi > 0f) bmiColor else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "BMI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (bmi > 0f) {
                        Text(
                            text = bmiCategory(bmi),
                            style = MaterialTheme.typography.labelSmall,
                            color = bmiColor
                        )
                    }
                }
            }

            if (profile.bloodType.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "혈액형",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(64.dp)
                    )
                    Text(
                        text = "${profile.bloodType}형",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SportsInfoCard(profile: UserProfile) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "운동 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (profile.exerciseFrequency > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "운동 빈도",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(72.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "주 ${profile.exerciseFrequency}회",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (profile.sports.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    profile.sports.forEach { sport ->
                        AssistChip(
                            onClick = {},
                            label = { Text(sport) }
                        )
                    }
                }
            } else {
                Text(
                    text = "운동 종목 없음",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MedicalInfoCard(profile: UserProfile) {
    var expanded by remember { mutableStateOf(false) }
    val hasInfo = profile.medicalConditions.isNotEmpty() || profile.allergies.isNotEmpty()

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "의료 정보",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "접기" else "펼치기",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!hasInfo) {
                        Text(
                            text = "등록된 의료 정보가 없습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        if (profile.medicalConditions.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "기저 질환",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = profile.medicalConditions,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (profile.allergies.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "알레르기",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = profile.allergies,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InjuryStatsCard(stats: InjuryStats) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "부상 통계",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "전체 부상", value = "${stats.totalCount}건")
                StatItem(label = "현재 진행 중", value = "${stats.activeCount}건")
                val avgDays = if (stats.avgRecoveryDays > 0)
                    "%.0f일".format(stats.avgRecoveryDays) else "—"
                StatItem(label = "평균 회복", value = avgDays)
            }

            val partName = stats.mostInjuredPart?.let { id ->
                BodyParts.findById(id)?.nameKo ?: id
            }
            if (partName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "부상 잦은 부위",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(96.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = partName,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun bmiColor(bmi: Float): Color = when {
    bmi <= 0f -> Color.Transparent
    bmi < 18.5f -> Color(0xFF2196F3)
    bmi < 25f -> Color(0xFF4CAF50)
    bmi < 30f -> Color(0xFFFF9800)
    else -> Color(0xFFF44336)
}

private fun bmiCategory(bmi: Float): String = when {
    bmi < 18.5f -> "저체중"
    bmi < 25f -> "정상"
    bmi < 30f -> "과체중"
    else -> "비만"
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    HealLogTheme {
        val profile = UserProfile(
            nickname = "김지연",
            birthDate = "1995-05-20",
            gender = "FEMALE",
            heightCm = 165f,
            weightKg = 55f,
            bloodType = "A",
            sports = listOf("러닝", "수영"),
            exerciseFrequency = 4
        )
        val stats = InjuryStats(totalCount = 5, activeCount = 1, avgRecoveryDays = 14.0, mostInjuredPart = "left_knee")
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileHeader(profile = profile, onPhotoClick = {})
            Spacer(Modifier.height(16.dp))
            BodyInfoCard(profile = profile)
            Spacer(Modifier.height(16.dp))
            SportsInfoCard(profile = profile)
            Spacer(Modifier.height(16.dp))
            InjuryStatsCard(stats = stats)
        }
    }
}
