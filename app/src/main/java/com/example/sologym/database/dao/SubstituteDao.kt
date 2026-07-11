package com.example.sologym.database.dao

import androidx.room.*
import com.example.sologym.database.entity.ExercicioSubstituto
import kotlinx.coroutines.flow.Flow

@Dao
interface SubstituteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercicioSubstituto: ExercicioSubstituto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(substitutos: List<ExercicioSubstituto>)

    @Delete
    suspend fun delete(exercicioSubstituto: ExercicioSubstituto)

    @Query("DELETE FROM exercicio_substituto WHERE exercicioId = :exercicioId")
    suspend fun deleteByExerciseId(exercicioId: Long)

    @Query("SELECT * FROM exercicio_substituto WHERE exercicioId = :exercicioId")
    fun getByExerciseId(exercicioId: Long): Flow<List<ExercicioSubstituto>>
}
