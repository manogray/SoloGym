package com.example.sologym.database.dao

import androidx.room.*
import com.example.sologym.database.entity.HistoricoTreino
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historicoTreino: HistoricoTreino)

    @Query("SELECT * FROM historico_treino ORDER BY data DESC")
    fun getAll(): Flow<List<HistoricoTreino>>

    @Query("DELETE FROM historico_treino")
    suspend fun deleteAll()

    @Query("SELECT * FROM historico_treino WHERE data >= :start AND data <= :end")
    fun getByPeriod(start: LocalDateTime, end: LocalDateTime): Flow<List<HistoricoTreino>>

    @Query("SELECT COUNT(*) FROM historico_treino WHERE data >= :start AND data <= :end")
    fun countByPeriod(start: LocalDateTime, end: LocalDateTime): Flow<Int>

    @Query("SELECT COUNT(*) FROM historico_treino WHERE data >= :start AND data < :endExclusive")
    suspend fun countByPeriodOnce(start: LocalDateTime, endExclusive: LocalDateTime): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM historico_treino
            WHERE treinoId = :workoutId
              AND data >= :start
              AND data < :endExclusive
        )
        """
    )
    fun observeWorkoutCompletedByPeriod(
        workoutId: Long,
        start: LocalDateTime,
        endExclusive: LocalDateTime
    ): Flow<Boolean>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM historico_treino
            WHERE data >= :start AND data < :endExclusive
        )
        """
    )
    fun observeAnyWorkoutCompletedByPeriod(
        start: LocalDateTime,
        endExclusive: LocalDateTime
    ): Flow<Boolean>

    @Query(
        """
        SELECT COUNT(*) FROM historico_treino
        WHERE treinoId = :workoutId
          AND data >= :start
          AND data < :endExclusive
        """
    )
    suspend fun countWorkoutByPeriodOnce(
        workoutId: Long,
        start: LocalDateTime,
        endExclusive: LocalDateTime
    ): Int

    @Query("SELECT SUM(duracaoSegundos) FROM historico_treino")
    fun getTotalDuration(): Flow<Long?>

    @Query("SELECT * FROM historico_treino ORDER BY data DESC LIMIT :limit")
    fun getLastWorkouts(limit: Int): Flow<List<HistoricoTreino>>
}
