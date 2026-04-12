package com.heallog.model

data class RecoveryStats(
    val injuryId: Long,
    val injuryTitle: String,
    val bodyPart: String,
    val initialPainLevel: Int,
    val currentPainLevel: Int,
    /** 0.0 ~ 100.0. Clamped to [0, 100] —악화 시 0으로 표시. */
    val recoveryRate: Float,
    val daysSinceInjury: Int,
    /** 현재 회복 속도 기반 선형 추정. PainLog 5건 미만이면 null. */
    val estimatedRecoveryDays: Int?,
    val trend: RecoveryTrend
)

enum class RecoveryTrend {
    IMPROVING,
    STABLE,
    WORSENING
}
