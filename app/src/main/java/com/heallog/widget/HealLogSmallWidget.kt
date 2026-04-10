package com.heallog.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.action.clickable
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class HealLogSmallWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // WidgetDataProvider.loadWidgetData is now a suspend function (no runBlocking needed)
        val data = WidgetDataProvider.loadWidgetData(context)
        provideContent {
            SmallWidgetContent(data)
        }
    }

    @OptIn(ExperimentalGlanceApi::class)
    @Composable
    private fun SmallWidgetContent(data: WidgetData) {
        val injury = data.activeInjuries.firstOrNull()

        Column(
            modifier = GlanceModifier.fillMaxSize()
                .padding(8.dp)
                .clickable(
                    actionStartActivity(createNavigationIntent(injury?.id))
                )
        ) {
            if (injury != null) {
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        text = injury.bodyPartEmoji,
                        style = TextStyle(fontSize = 20.sp)
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = injury.title,
                        maxLines = 1,
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
                Row {
                    Text(
                        text = "통증: ${injury.currentPainLevel}/10",
                        style = TextStyle(fontSize = 10.sp)
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = "${injury.daysSinceOccurred}일",
                        style = TextStyle(fontSize = 10.sp)
                    )
                }
            } else {
                Text(
                    text = "활성 부상 없음 ✨",
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }
    }
}
