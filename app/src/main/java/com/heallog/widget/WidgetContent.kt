package com.heallog.widget

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

/**
 * Shared composable content for widgets.
 * Reduces duplication across HealLogSmallWidget, HealLogMediumWidget, HealLogLargeWidget.
 */

@OptIn(ExperimentalGlanceApi::class)
@Composable
fun InjuryRow(
    injury: WidgetInjury,
    onClick: () -> Unit,
    modifier: GlanceModifier = GlanceModifier,
    showPainBar: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(actionStartActivity(onClick))
            .padding(4.dp)
    ) {
        Text(
            text = injury.bodyPartEmoji,
            style = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = GlanceModifier.width(4.dp))
        Column {
            Text(
                text = injury.title,
                maxLines = 1,
                style = TextStyle(fontSize = 11.sp)
            )
            if (showPainBar) {
                Text(
                    text = "●".repeat(injury.currentPainLevel) + "○".repeat(10 - injury.currentPainLevel),
                    style = TextStyle(fontSize = 8.sp)
                )
            }
        }
    }
}

@OptIn(ExperimentalGlanceApi::class)
@Composable
fun WidgetHeaderRow(
    title: String,
    countText: String
) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = title,
            style = TextStyle(fontSize = 14.sp)
        )
        Spacer(modifier = GlanceModifier.width(4.dp))
        Text(
            text = countText,
            style = TextStyle(fontSize = 10.sp)
        )
    }
}

/**
 * Creates an intent for navigating to injury detail or home screen.
 */
fun createNavigationIntent(injuryId: Long? = null): Intent {
    val deeplink = if (injuryId != null) {
        "heallog://injury/$injuryId"
    } else {
        "heallog://home"
    }
    return Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
}

/**
 * Creates a horizontal pain level bar visualization using Unicode characters.
 */
fun createPainLevelVisualization(painLevel: Int): String =
    "●".repeat(painLevel) + "○".repeat(10 - painLevel)

/**
 * Creates a trend visualization for pain levels over the last 7 days.
 */
fun createTrendVisualization(painLevels: List<Int>): String =
    painLevels.map { level ->
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
    }.joinToString("")
