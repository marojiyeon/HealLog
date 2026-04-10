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

class HealLogMediumWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // WidgetDataProvider.loadWidgetData is now a suspend function (no runBlocking needed)
        val data = WidgetDataProvider.loadWidgetData(context)
        provideContent {
            MediumWidgetContent(data)
        }
    }

    @OptIn(ExperimentalGlanceApi::class)
    @Composable
    private fun MediumWidgetContent(data: WidgetData) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .padding(8.dp)
                .clickable(
                    actionStartActivity(createNavigationIntent())
                )
        ) {
            WidgetHeaderRow(
                title = "🩺 HealLog",
                countText = "${data.totalActiveCount}건"
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            data.activeInjuries.forEach { injury ->
                InjuryRow(
                    injury = injury,
                    onClick = { createNavigationIntent(injury.id) },
                    showPainBar = true
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
            }
        }
    }
}
