package com.example.sologym.database.dao

import androidx.room.*
import com.example.sologym.database.entity.TreinoExercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(treinoExercicio: TreinoExercicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(treinoExercicios: List<TreinoExercicio>)

    @Update
    suspend fun update(treinoExercicio: TreinoExercicio)

    @Delete
    suspend fun delete(treinoExercicio: TreinoExercicio)

    @Query("DELETE FROM treino_exercicio WHERE treinoId = :treinoId")
    suspend fun deleteByWorkoutId(treinoId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM treino_exercicio WHERE exercicioId = :exercicioId)")
    suspend fun isExerciseUsedInWorkouts(exercicioId: Long): Boolean

    @Query("SELECT * FROM treino_exercicio WHERE treinoId = :treinoId ORDER BY ordem ASC")
    fun getByWorkoutId(treinoId: Long): Flow<List<TreinoExercicio>>
}
