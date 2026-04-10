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

class HealLogMediumWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MediumWidgetContent(context)
        }
    }

    private suspend fun MediumWidgetContent(context: Context) {
        val data = WidgetDataProvider.loadWidgetData(context)

        Column(
            modifier = fillMaxSize()
                .padding(8.dp)
        ) {
            // Header
            Row(
                modifier = fillMaxWidth()
            ) {
                Text(
                    text = "🩺 HealLog",
                    style = TextStyle(fontSize = sp(14))
                )
                Spacer(modifier = width(4.dp))
                Text(
                    text = "${data.totalActiveCount}건",
                    style = TextStyle(fontSize = sp(10))
                )
            }

            Spacer(modifier = height(4.dp))

            // Injuries list
            data.activeInjuries.forEach { injury ->
                Row(
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
                    Text(
                        text = injury.bodyPartEmoji,
                        style = TextStyle(fontSize = sp(16))
                    )
                    Spacer(modifier = width(4.dp))
                    Column(modifier = androidx.glance.layout.defaultWeight(1f)) {
                        Text(
                            text = injury.title,
                            maxLines = 1,
                            style = TextStyle(fontSize = sp(11))
                        )
                        // Pain level as dots
                        Text(
                            text = "●".repeat(injury.currentPainLevel) + "○".repeat(10 - injury.currentPainLevel),
                            style = TextStyle(fontSize = sp(8))
                        )
                    }
                }
                Spacer(modifier = height(2.dp))
            }

            Spacer(modifier = height(4.dp))

            // Action button
            Row(
                modifier = fillMaxWidth()
                    .clickable(
                        actionStartActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("heallog://bodymap")
                            }
                        )
                    )
                    .padding(4.dp)
            ) {
                Text(
                    text = "기록 추가 +",
                    style = TextStyle(fontSize = sp(11))
                )
            }
        }
    }
}
