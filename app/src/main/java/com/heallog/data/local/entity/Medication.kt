package com.heallog.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = Injury::class,
            parentColumns = ["id"],
            childColumns = ["injuryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HospitalVisit::class,
            parentColumns = ["id"],
            childColumns = ["hospitalVisitId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("injuryId"), Index("hospitalVisitId")]
)
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val injuryId: Long,
    val hospitalVisitId: Long? = null,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val sideEffectNote: String? = null,
    val isActive: Boolean = true,
    val updatedAt: LocalDateTime? = null
)
