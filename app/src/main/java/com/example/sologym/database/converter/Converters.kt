package com.example.sologym.database.converter

import androidx.room.TypeConverter
import com.example.sologym.model.WorkoutStatus
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun fromDayOfWeek(value: Int?): DayOfWeek? {
        return value?.let { DayOfWeek.of(it) }
    }

    @TypeConverter
    fun dayOfWeekToInt(day: DayOfWeek?): Int? {
        return day?.value
    }

    @TypeConverter
    fun fromWorkoutStatus(value: String?): WorkoutStatus? {
        return value?.let { WorkoutStatus.valueOf(it) }
    }

    @TypeConverter
    fun workoutStatusToString(status: WorkoutStatus?): String? {
        return status?.name
    }
}
