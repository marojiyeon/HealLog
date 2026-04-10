package com.heallog.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class HealLogSmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = HealLogSmallWidget()
}

class HealLogMediumWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = HealLogMediumWidget()
}

class HealLogLargeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = HealLogLargeWidget()
}
