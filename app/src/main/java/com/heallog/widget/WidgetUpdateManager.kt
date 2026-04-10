package com.heallog.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages widget updates when injury or pain log data changes.
 *
 * This class is responsible for notifying all widgets (Small, Medium, Large)
 * when data changes, keeping the widget update logic separate from the repository.
 */
@Singleton
class WidgetUpdateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Updates all widgets to reflect data changes.
     * Called whenever injury or pain log data is modified.
     */
    suspend fun updateAllWidgets() {
        try {
            // Update all widget instances asynchronously
            HealLogSmallWidget().updateAll(context)
            HealLogMediumWidget().updateAll(context)
            HealLogLargeWidget().updateAll(context)
        } catch (e: Exception) {
            // Log but don't crash if widget update fails
            // (widgets may not be installed or may be temporarily unavailable)
            e.printStackTrace()
        }
    }
}
