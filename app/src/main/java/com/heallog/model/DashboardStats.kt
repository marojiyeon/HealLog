package com.heallog.model

data class DashboardStats(
    val totalInjuries: Int,
    val activeInjuries: Int,
    /** 완치된 부상들의 평균 회복 기간(일). 완치 부상이 없으면 null. */
    val avgRecoveryDays: Float?,
    /** 가장 많이 다친 신체 부위 이름. 부상이 없으면 null. */
    val mostInjuredPart: String?,
    val activeRecoveryList: List<RecoveryStats>
)
