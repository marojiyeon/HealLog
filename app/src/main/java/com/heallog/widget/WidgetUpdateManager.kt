package com.heallog.widget

import android.content.Context
import android.util.Log
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
        listOf(
            "Small" to suspend { HealLogSmallWidget().updateAll(context) },
            "Medium" to suspend { HealLogMediumWidget().updateAll(context) },
            "Large" to suspend { HealLogLargeWidget().updateAll(context) }
        ).forEach { (name, updateFn) ->
            try {
                updateFn()
            } catch (e: Exception) {
                Log.e("WidgetUpdateManager", "$name widget update failed", e)
            }
        }
    }
}
