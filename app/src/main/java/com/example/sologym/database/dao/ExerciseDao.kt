package com.example.sologym.database.dao

import androidx.room.*
import com.example.sologym.database.entity.Exercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercicio: Exercicio): Long

    @Update
    suspend fun update(exercicio: Exercicio)

    @Delete
    suspend fun delete(exercicio: Exercicio)

    @Query("SELECT * FROM exercicio WHERE id = :id")
    suspend fun getById(id: Long): Exercicio?

    @Transaction
    @Query("SELECT * FROM exercicio WHERE id = :id")
    suspend fun getCompleteById(id: Long): com.example.sologym.database.relation.CompleteExercise?

    @Query("SELECT * FROM exercicio ORDER BY nome ASC")
    fun getAll(): Flow<List<Exercicio>>

    @Query("SELECT * FROM exercicio WHERE nome LIKE '%' || :nome || '%'")
    fun searchByName(nome: String): Flow<List<Exercicio>>
}
