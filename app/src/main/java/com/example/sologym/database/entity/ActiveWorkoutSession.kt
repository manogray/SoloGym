package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_workout_session")
data class ActiveWorkoutSession(
    @PrimaryKey val id: Int = SINGLE_ACTIVE_SESSION_ID,
    val workoutId: Long,
    val startedAtEpochMillis: Long
) {
    companion object {
        const val SINGLE_ACTIVE_SESSION_ID = 1
    }
}
