package com.example.sologym.database.dao

import androidx.room.*
import com.example.sologym.database.entity.Treino
import com.example.sologym.model.WorkoutStatus
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(treino: Treino): Long

    @Update
    suspend fun update(treino: Treino)

    @Delete
    suspend fun delete(treino: Treino)

    @Query("SELECT * FROM treino WHERE id = :id")
    suspend fun getById(id: Long): Treino?

    @Query("SELECT * FROM treino WHERE diaSemana = :diaSemana")
    fun getByDayOfWeek(diaSemana: DayOfWeek): Flow<Treino?>

    @Transaction
    @Query("SELECT * FROM treino WHERE diaSemana = :diaSemana")
    fun getCompleteByDayOfWeek(diaSemana: DayOfWeek): Flow<com.example.sologym.database.relation.CompleteWorkout?>

    @Transaction
    @Query("SELECT * FROM treino WHERE id = :id")
    fun getCompleteById(id: Long): Flow<com.example.sologym.database.relation.CompleteWorkout?>

    @Transaction
    @Query("SELECT * FROM treino ORDER BY diaSemana")
    fun getAllComplete(): Flow<List<com.example.sologym.database.relation.CompleteWorkout>>

    @Query("SELECT * FROM treino")
    fun getAll(): Flow<List<Treino>>

    @Query("SELECT EXISTS(SELECT 1 FROM treino WHERE diaSemana = :day AND id != :excludeId)")
    suspend fun existsByDayOfWeek(day: DayOfWeek, excludeId: Long): Boolean

    @Query("UPDATE treino SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: WorkoutStatus)
}
