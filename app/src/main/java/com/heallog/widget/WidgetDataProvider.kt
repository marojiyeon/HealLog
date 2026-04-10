package com.heallog.widget

import android.content.Context
import com.heallog.data.local.database.HealLogDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

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

object WidgetDataProvider {
    fun loadWidgetData(context: Context): WidgetData = runBlocking {
        val db = HealLogDatabase.getInstance(context)
        val injuryDao = db.injuryDao()
        val painLogDao = db.painLogDao()

        val injuries = injuryDao.getAllActiveInjuries().firstOrNull() ?: emptyList()
        val injuryCount = injuries.size

        val widgetInjuries = injuries.map { injury ->
            val painLogs = painLogDao.getLogsForInjury(injury.id).firstOrNull() ?: emptyList()
            val last7Days = painLogs.takeLast(7).map { it.painLevel }
            val daysSince = if (injury.dateOccurred != null) {
                (System.currentTimeMillis() - injury.dateOccurred) / (1000 * 60 * 60 * 24)
            } else {
                0L
            }

            WidgetInjury(
                id = injury.id,
                bodyPartEmoji = mapBodyPartToEmoji(injury.bodyPartId),
                title = injury.title,
                currentPainLevel = painLogs.lastOrNull()?.painLevel ?: 0,
                daysSinceOccurred = daysSince,
                last7DaysPainLevels = last7Days
            )
        }

        WidgetData(
            activeInjuries = widgetInjuries.take(3),
            totalActiveCount = injuryCount
        )
    }

    private fun mapBodyPartToEmoji(bodyPartId: String): String = when (bodyPartId) {
        "head" -> "🧠"
        "neck" -> "💜"
        "shoulder" -> "🏋️"
        "arm_left" -> "👈"
        "arm_right" -> "👉"
        "elbow_left" -> "👈"
        "elbow_right" -> "👉"
        "wrist_left" -> "⌚"
        "wrist_right" -> "⌚"
        "chest" -> "💪"
        "back" -> "🔙"
        "waist" -> "🤸"
        "hip" -> "🤸"
        "leg_left" -> "🦵"
        "leg_right" -> "🦵"
        "knee_left" -> "🦵"
        "knee_right" -> "🦵"
        "ankle_left" -> "👟"
        "ankle_right" -> "👟"
        "foot_left" -> "🦶"
        "foot_right" -> "🦶"
        else -> "🏥"
    }
}
