package com.example.sologym.database.dao

import androidx.room.*
import com.example.sologym.database.entity.Serie
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serie: Serie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(series: List<Serie>)

    @Update
    suspend fun update(serie: Serie)

    @Delete
    suspend fun delete(serie: Serie)

    @Query("DELETE FROM serie WHERE exercicioId = :exercicioId")
    suspend fun deleteByExerciseId(exercicioId: Long)

    @Query("SELECT * FROM serie WHERE exercicioId = :exercicioId ORDER BY ordem ASC")
    fun getByExerciseId(exercicioId: Long): Flow<List<Serie>>
}
