package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sologym.model.WorkoutStatus
import java.time.DayOfWeek

@Entity(tableName = "treino")
data class Treino(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diaSemana: DayOfWeek,
    val status: WorkoutStatus = WorkoutStatus.NOT_STARTED
)
