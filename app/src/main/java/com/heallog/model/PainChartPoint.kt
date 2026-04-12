package com.heallog.model

import java.time.LocalDate

data class PainChartPoint(
    val date: LocalDate,
    val painLevel: Int,
    val note: String? = null
)
