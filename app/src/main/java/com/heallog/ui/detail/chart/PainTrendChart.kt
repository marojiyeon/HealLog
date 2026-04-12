package com.heallog.ui.detail.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heallog.model.ChartPeriod
import com.heallog.model.PainChartPoint
import com.heallog.ui.theme.HealLogTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PainTrendChart(
    chartPoints: List<PainChartPoint>,
    selectedPeriod: ChartPeriod,
    onPeriodChange: (ChartPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(chartPoints) {
        modelProducer.runTransaction {
            lineSeries {
                if (chartPoints.isNotEmpty()) {
                    series(chartPoints.map { it.painLevel.toFloat() })
                } else {
                    series(emptyList<Float>())
                }
            }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        PeriodSelector(selectedPeriod = selectedPeriod, onPeriodChange = onPeriodChange)

        Spacer(modifier = Modifier.height(12.dp))

        if (chartPoints.isEmpty()) {
            EmptyChartState()
        } else {
            val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
            val bottomValueFormatter = CartesianValueFormatter { context, x, _ ->
                chartPoints.getOrNull(x.toInt())?.date?.format(dateFormatter) ?: ""
            }
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomValueFormatter
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))
            ChartSummary(chartPoints = chartPoints)
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: ChartPeriod,
    onPeriodChange: (ChartPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChartPeriod.entries.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodChange(period) },
                label = { Text(period.label) }
            )
        }
    }
}

@Composable
private fun ChartSummary(chartPoints: List<PainChartPoint>) {
    if (chartPoints.isEmpty()) return
    val avg = chartPoints.map { it.painLevel }.average()
    val max = chartPoints.maxBy { it.painLevel }
    val min = chartPoints.minBy { it.painLevel }
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryItem(label = "평균", value = String.format("%.1f", avg))
        SummaryItem(label = "최고", value = "${max.painLevel} (${max.date.format(dateFormatter)})")
        SummaryItem(label = "최저", value = "${min.painLevel} (${min.date.format(dateFormatter)})")
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyChartState() {
    Text(
        text = "아직 통증 기록이 없어요.\n통증 일지 탭에서 기록을 추가해 보세요.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp)
    )
}

private val ChartPeriod.label: String
    get() = when (this) {
        ChartPeriod.WEEK -> "1주"
        ChartPeriod.MONTH -> "1개월"
        ChartPeriod.ALL -> "전체"
    }

@Preview(showBackground = true)
@Composable
private fun PainTrendChartPreview() {
    HealLogTheme {
        PainTrendChart(
            chartPoints = listOf(
                PainChartPoint(LocalDate.now().minusDays(6), 8),
                PainChartPoint(LocalDate.now().minusDays(5), 7, "조금 나아짐"),
                PainChartPoint(LocalDate.now().minusDays(4), 6),
                PainChartPoint(LocalDate.now().minusDays(3), 5),
                PainChartPoint(LocalDate.now().minusDays(2), 4, "운동 재개"),
                PainChartPoint(LocalDate.now().minusDays(1), 3),
                PainChartPoint(LocalDate.now(), 2),
            ),
            selectedPeriod = ChartPeriod.WEEK,
            onPeriodChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PainTrendChartEmptyPreview() {
    HealLogTheme {
        PainTrendChart(
            chartPoints = emptyList(),
            selectedPeriod = ChartPeriod.WEEK,
            onPeriodChange = {}
        )
    }
}
