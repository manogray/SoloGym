package com.example.sologym.repository

import com.example.sologym.database.dao.ActiveWorkoutSessionDao
import com.example.sologym.database.entity.ActiveWorkoutSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionRepository @Inject constructor(
    private val sessionDao: ActiveWorkoutSessionDao
) {
    fun observeActiveSession(): Flow<ActiveWorkoutSession?> =
        sessionDao.observeActiveSession()

    suspend fun startSession(workoutId: Long, startedAtEpochMillis: Long) {
        sessionDao.save(
            ActiveWorkoutSession(
                workoutId = workoutId,
                startedAtEpochMillis = startedAtEpochMillis
            )
        )
    }
}
