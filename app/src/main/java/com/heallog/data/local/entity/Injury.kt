package com.heallog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.heallog.model.InjuryStatus
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "injuries")
data class Injury(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bodyPart: String,
    val title: String,
    val description: String,
    val painLevel: Int,
    val occurredAt: LocalDate,
    val createdAt: LocalDateTime,
    val status: InjuryStatus = InjuryStatus.ACTIVE,
    val updatedAt: LocalDateTime? = null
)
