package com.example.sologym.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sologym.database.entity.ActiveWorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveWorkoutSessionDao {
    @Query("SELECT * FROM active_workout_session WHERE id = 1")
    fun observeActiveSession(): Flow<ActiveWorkoutSession?>

    @Query("SELECT * FROM active_workout_session WHERE id = 1")
    suspend fun getActiveSession(): ActiveWorkoutSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(session: ActiveWorkoutSession)

    @Query("DELETE FROM active_workout_session WHERE id = 1")
    suspend fun deleteActiveSession()
}
