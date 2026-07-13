package com.example.sologym.repository

import androidx.room.withTransaction
import com.example.sologym.database.SoloGymDatabase
import com.example.sologym.database.dao.HistoryDao
import com.example.sologym.database.dao.PlayerDao
import com.example.sologym.database.dao.ActiveWorkoutSessionDao
import com.example.sologym.database.entity.HistoricoTreino
import com.example.sologym.database.entity.Player
import com.example.sologym.model.PlayerProgression
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val database: SoloGymDatabase,
    private val historyDao: HistoryDao,
    private val playerDao: PlayerDao,
    private val sessionDao: ActiveWorkoutSessionDao
) {
    fun getAllHistory(): Flow<List<HistoricoTreino>> = historyDao.getAll()

    suspend fun insertHistory(historicoTreino: HistoricoTreino) {
        historyDao.insert(historicoTreino)
    }

    fun observeWorkoutCompletedOnDate(
        workoutId: Long,
        date: LocalDate
    ): Flow<Boolean> = historyDao.observeWorkoutCompletedByPeriod(
        workoutId = workoutId,
        start = date.atStartOfDay(),
        endExclusive = date.plusDays(1).atStartOfDay()
    )

    suspend fun completeWorkout(
        workoutId: Long,
        completedAt: LocalDateTime,
        durationSeconds: Long
    ): Boolean = database.withTransaction {
        val date = completedAt.toLocalDate()
        val alreadyCompleted = historyDao.countWorkoutByPeriodOnce(
            workoutId = workoutId,
            start = date.atStartOfDay(),
            endExclusive = date.plusDays(1).atStartOfDay()
        ) > 0

        if (alreadyCompleted) {
            sessionDao.deleteActiveSession()
            return@withTransaction false
        }

        historyDao.insert(
            HistoricoTreino(
                treinoId = workoutId,
                data = completedAt,
                duracaoSegundos = durationSeconds
            )
        )

        var player = playerDao.getPlayer()
        if (player == null) {
            playerDao.insert(Player(proximaDataFalha = date))
            player = playerDao.getPlayer() ?: Player(proximaDataFalha = date)
        }
        playerDao.update(PlayerProgression.completedWorkout(player))
        sessionDao.deleteActiveSession()
        true
    }

    fun getLastWorkouts(limit: Int): Flow<List<HistoricoTreino>> = historyDao.getLastWorkouts(limit)
}
