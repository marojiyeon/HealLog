package com.heallog.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "hospital_visits",
    foreignKeys = [
        ForeignKey(
            entity = Injury::class,
            parentColumns = ["id"],
            childColumns = ["injuryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("injuryId")]
)
data class HospitalVisit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val injuryId: Long,
    val visitDate: LocalDate,
    val hospitalName: String,
    val doctorName: String? = null,
    val diagnosis: String? = null,
    val treatmentNote: String,
    val nextAppointment: LocalDate? = null,
    val cost: Int? = null,
    val isInsuranceCovered: Boolean? = null,
    val photoUris: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
)
