package com.heallog.data.local.database

import androidx.room.TypeConverter
import com.heallog.model.InjuryStatus
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun toLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun fromLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun toLocalDateTime(dateTime: LocalDateTime?): String? = dateTime?.toString()

    @TypeConverter
    fun fromInjuryStatus(value: String?): InjuryStatus? = value?.let { InjuryStatus.valueOf(it) }

    @TypeConverter
    fun toInjuryStatus(status: InjuryStatus?): String? = status?.name
}
