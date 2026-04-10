package com.heallog.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class HealLogLargeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // WidgetDataProvider.loadWidgetData is now a suspend function (no runBlocking needed)
        val data = WidgetDataProvider.loadWidgetData(context)
        provideContent {
            LargeWidgetContent(data)
        }
    }

    @OptIn(ExperimentalGlanceApi::class)
    @Composable
    private fun LargeWidgetContent(data: WidgetData) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .padding(8.dp)
                .clickable(
                    actionStartActivity(createNavigationIntent())
                )
        ) {
            Text(
                text = "🩺 HealLog 활성 부상 ${data.totalActiveCount}건",
                style = TextStyle(fontSize = 13.sp)
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            data.activeInjuries.take(2).forEach { injury ->
                Column(
                    modifier = GlanceModifier.fillMaxWidth()
                        .clickable(
                            actionStartActivity(createNavigationIntent(injury.id))
                        )
                        .padding(4.dp)
                ) {
                    Row {
                        Text(
                            text = injury.bodyPartEmoji,
                            style = TextStyle(fontSize = 16.sp)
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Column {
                            Text(
                                text = injury.title,
                                maxLines = 1,
                                style = TextStyle(fontSize = 12.sp)
                            )
                            Text(
                                text = "통증: ${injury.currentPainLevel}/10",
                                style = TextStyle(fontSize = 10.sp)
                            )
                        }
                    }

                    if (injury.last7DaysPainLevels.isNotEmpty()) {
                        Text(
                            text = createTrendVisualization(injury.last7DaysPainLevels),
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                }
                Spacer(modifier = GlanceModifier.height(4.dp))
            }
        }
    }
}
