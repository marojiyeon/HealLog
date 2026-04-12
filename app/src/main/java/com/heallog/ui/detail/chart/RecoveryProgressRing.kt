package com.heallog.ui.detail.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heallog.model.RecoveryStats
import com.heallog.model.RecoveryTrend
import com.heallog.ui.theme.HealLogTheme

@Composable
fun RecoveryProgressRing(
    stats: RecoveryStats,
    modifier: Modifier = Modifier,
    ringSize: Dp = 80.dp,
    strokeWidth: Dp = 8.dp
) {
    val rate = stats.recoveryRate / 100f
    val clampedRate = rate.coerceIn(0f, 1f)
    val ringColor = recoveryRingColor(clampedRate)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {
                stateDescription = "${stats.recoveryRate.toInt()}% 회복, ${stats.trend.label}, 경과 ${stats.daysSinceInjury}일"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProgressRingCanvas(rate = clampedRate, color = ringColor, size = ringSize, strokeWidth = strokeWidth)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${stats.recoveryRate.toInt()}% 회복",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${stats.trend.icon} ${stats.trend.label}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "부상 경과 ${stats.daysSinceInjury}일",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            stats.estimatedRecoveryDays?.let { days ->
                Text(
                    text = "예상 완치: 약 ${days}일 후",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ProgressRingCanvas(
    rate: Float,
    color: Color,
    size: Dp,
    strokeWidth: Dp
) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)

            // Background track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * rate,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(rate * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/** 0.0(빨강) → 0.5(노랑) → 1.0(초록) 그라데이션 */
private fun recoveryRingColor(rate: Float): Color {
    val red = Color(0xFFE53935)
    val yellow = Color(0xFFFDD835)
    val green = Color(0xFF43A047)
    return if (rate <= 0.5f) {
        lerp(red, yellow, rate * 2f)
    } else {
        lerp(yellow, green, (rate - 0.5f) * 2f)
    }
}

private val RecoveryTrend.label: String
    get() = when (this) {
        RecoveryTrend.IMPROVING -> "호전 중"
        RecoveryTrend.STABLE -> "유지 중"
        RecoveryTrend.WORSENING -> "악화 중"
    }

private val RecoveryTrend.icon: String
    get() = when (this) {
        RecoveryTrend.IMPROVING -> "↗"
        RecoveryTrend.STABLE -> "→"
        RecoveryTrend.WORSENING -> "↘"
    }

@Preview(showBackground = true)
@Composable
private fun RecoveryProgressRingPreview() {
    HealLogTheme {
        Column {
            RecoveryProgressRing(
                stats = RecoveryStats(
                    injuryId = 1L,
                    injuryTitle = "왼쪽 무릎 부상",
                    bodyPart = "LEFT_KNEE",
                    initialPainLevel = 8,
                    currentPainLevel = 3,
                    recoveryRate = 62.5f,
                    daysSinceInjury = 14,
                    estimatedRecoveryDays = 8,
                    trend = RecoveryTrend.IMPROVING
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecoveryProgressRing(
                stats = RecoveryStats(
                    injuryId = 2L,
                    injuryTitle = "허리 통증",
                    bodyPart = "LOWER_BACK",
                    initialPainLevel = 7,
                    currentPainLevel = 7,
                    recoveryRate = 0f,
                    daysSinceInjury = 3,
                    estimatedRecoveryDays = null,
                    trend = RecoveryTrend.STABLE
                )
            )
        }
    }
}
