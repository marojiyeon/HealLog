package com.heallog.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.TextStyle
import androidx.glance.unit.dp
import androidx.glance.unit.sp
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity

class HealLogLargeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            LargeWidgetContent(context)
        }
    }

    private suspend fun LargeWidgetContent(context: Context) {
        val data = WidgetDataProvider.loadWidgetData(context)

        Column(
            modifier = fillMaxSize()
                .padding(8.dp)
        ) {
            // Header
            Text(
                text = "🩺 HealLog 활성 부상 ${data.totalActiveCount}건",
                style = TextStyle(fontSize = sp(13))
            )

            Spacer(modifier = height(6.dp))

            // Injuries with trend
            data.activeInjuries.take(2).forEach { injury ->
                Column(
                    modifier = fillMaxWidth()
                        .clickable(
                            actionStartActivity(
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("heallog://injury/${injury.id}")
                                }
                            )
                        )
                        .padding(4.dp)
                ) {
                    Row {
                        Text(
                            text = injury.bodyPartEmoji,
                            style = TextStyle(fontSize = sp(16))
                        )
                        Spacer(modifier = width(4.dp))
                        Column(modifier = androidx.glance.layout.defaultWeight(1f)) {
                            Text(
                                text = injury.title,
                                maxLines = 1,
                                style = TextStyle(fontSize = sp(12))
                            )
                            Text(
                                text = "통증: ${injury.currentPainLevel}/10",
                                style = TextStyle(fontSize = sp(10))
                            )
                        }
                    }

                    // Trend bars
                    if (injury.last7DaysPainLevels.isNotEmpty()) {
                        Text(
                            text = injury.last7DaysPainLevels.map { level ->
                                when {
                                    level == 0 -> "▁"
                                    level <= 1 -> "▂"
                                    level <= 2 -> "▃"
                                    level <= 3 -> "▄"
                                    level <= 4 -> "▅"
                                    level <= 6 -> "▆"
                                    level <= 8 -> "▇"
                                    else -> "█"
                                }
                            }.joinToString(""),
                            style = TextStyle(fontSize = sp(12))
                        )
                    }
                }
                Spacer(modifier = height(4.dp))
            }

            Spacer(modifier = height(4.dp))

            // Action buttons
            Row(modifier = fillMaxWidth()) {
                Text(
                    text = "빠른 기록",
                    modifier = androidx.glance.layout.defaultWeight(1f)
                        .clickable(
                            actionStartActivity(
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("heallog://bodymap")
                                }
                            )
                        )
                        .padding(4.dp),
                    style = TextStyle(fontSize = sp(11))
                )
                Spacer(modifier = width(4.dp))
                Text(
                    text = "앱 열기",
                    modifier = androidx.glance.layout.defaultWeight(1f)
                        .clickable(
                            actionStartActivity(
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("heallog://home")
                                }
                            )
                        )
                        .padding(4.dp),
                    style = TextStyle(fontSize = sp(11))
                )
            }
        }
    }
}
