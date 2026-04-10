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
import androidx.glance.layout.width
import androidx.glance.text.TextStyle
import androidx.glance.unit.dp
import androidx.glance.unit.sp
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity

class HealLogSmallWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            SmallWidgetContent(context)
        }
    }

    private suspend fun SmallWidgetContent(context: Context) {
        val data = WidgetDataProvider.loadWidgetData(context)
        val injury = data.activeInjuries.firstOrNull()

        Column(
            modifier = fillMaxSize()
                .padding(8.dp)
                .clickable(
                    actionStartActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            data = if (injury != null) {
                                Uri.parse("heallog://injury/${injury.id}")
                            } else {
                                Uri.parse("heallog://home")
                            }
                        }
                    )
                )
        ) {
            if (injury != null) {
                Row(
                    modifier = fillMaxWidth()
                ) {
                    Text(
                        text = injury.bodyPartEmoji,
                        style = TextStyle(fontSize = sp(20))
                    )
                    Spacer(modifier = width(4.dp))
                    Text(
                        text = injury.title,
                        maxLines = 1,
                        style = TextStyle(fontSize = sp(12))
                    )
                }
                Row {
                    Text(
                        text = "통증: ${injury.currentPainLevel}/10",
                        style = TextStyle(fontSize = sp(10))
                    )
                    Spacer(modifier = width(4.dp))
                    Text(
                        text = "${injury.daysSinceOccurred}일",
                        style = TextStyle(fontSize = sp(10))
                    )
                }
            } else {
                Text(
                    text = "활성 부상 없음 ✨",
                    style = TextStyle(fontSize = sp(12))
                )
            }
        }
    }
}
