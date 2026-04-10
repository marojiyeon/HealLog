package com.heallog.widget

import android.content.Context
import com.heallog.data.local.database.HealLogDatabase
import com.heallog.util.EmojiMapper
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class WidgetInjury(
    val id: Long,
    val bodyPartEmoji: String,
    val title: String,
    val currentPainLevel: Int,
    val daysSinceOccurred: Long,
    val last7DaysPainLevels: List<Int>
)

data class WidgetData(
    val activeInjuries: List<WidgetInjury>,
    val totalActiveCount: Int
)

/**
 * Provides widget data from the database.
 * This should only be called from suspend contexts (e.g., GlanceAppWidget.provideGlance).
 */
object WidgetDataProvider {
    /**
     * Loads widget data from the database.
     * Must be called from a suspend context.
     *
     * @param context Android context for accessing the database
     * @return WidgetData containing active injuries and count
     */
    suspend fun loadWidgetData(context: Context): WidgetData {
        val db = HealLogDatabase.getInstance(context)
        val injuryDao = db.injuryDao()
        val painLogDao = db.painLogDao()

        val injuries = injuryDao.getAllActiveInjuries().firstOrNull() ?: emptyList()
        val injuryCount = injuries.size

        val widgetInjuries = injuries.map { injury ->
            val painLogs = painLogDao.getLogsForInjury(injury.id).firstOrNull() ?: emptyList()
            val last7Days = painLogs.takeLast(7).map { it.painLevel }
            val daysSince = ChronoUnit.DAYS.between(injury.occurredAt, LocalDate.now())

            WidgetInjury(
                id = injury.id,
                bodyPartEmoji = EmojiMapper.getEmojiForBodyPart(injury.bodyPart),
                title = injury.title,
                currentPainLevel = painLogs.lastOrNull()?.painLevel ?: 0,
                daysSinceOccurred = daysSince,
                last7DaysPainLevels = last7Days
            )
        }

        return WidgetData(
            activeInjuries = widgetInjuries.take(3),
            totalActiveCount = injuryCount
        )
    }
}
