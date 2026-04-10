package com.heallog.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "pain_logs",
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
data class PainLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val injuryId: Long,
    val painLevel: Int,
    val note: String,
    val photoUris: String? = null,
    val loggedAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
)
