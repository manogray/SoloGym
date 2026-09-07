package com.example.sologym.repository

import androidx.room.withTransaction
import com.example.sologym.database.SoloGymDatabase
import com.example.sologym.database.dao.HistoryDao
import com.example.sologym.database.dao.PlayerDao
import com.example.sologym.database.dao.WorkoutDao
import com.example.sologym.database.dao.ActiveWorkoutSessionDao
import com.example.sologym.database.entity.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.sologym.model.PlayerProgression
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val database: SoloGymDatabase,
    private val playerDao: PlayerDao,
    private val workoutDao: WorkoutDao,
    private val historyDao: HistoryDao,
    private val sessionDao: ActiveWorkoutSessionDao
) {
    fun observePlayer(): Flow<Player> = playerDao.observePlayer().map { it ?: Player() }

    suspend fun resetProgressAndHistory(today: LocalDate = LocalDate.now()) {
        database.withTransaction {
            getOrCreatePlayer(today)
            historyDao.deleteAll()
            playerDao.update(PlayerProgression.reset(today))
        }
    }

    suspend fun evaluateMissedWorkouts(today: LocalDate = LocalDate.now()) {
        database.withTransaction {
            var player = getOrCreatePlayer(today)
            var dateToEvaluate = player.proximaDataFalha
            val activeSession = sessionDao.getActiveSession()
            val activeSessionDate = activeSession?.let {
                Instant.ofEpochMilli(it.startedAtEpochMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }

            while (dateToEvaluate.isBefore(today)) {
                val hasScheduledWorkout = workoutDao.existsByDayOfWeek(dateToEvaluate.dayOfWeek, 0)
                val completed = historyDao.countByPeriodOnce(
                    dateToEvaluate.atStartOfDay(),
                    dateToEvaluate.plusDays(1).atStartOfDay()
                ) > 0
                val hasActiveWorkoutForDate = activeSessionDate == dateToEvaluate

                if (!completed && !hasActiveWorkoutForDate) {
                    player = if (hasScheduledWorkout) {
                        PlayerProgression.missedWorkout(player)
                    } else {
                        PlayerProgression.completedRestDay(player)
                    }
                }
                dateToEvaluate = dateToEvaluate.plusDays(1)
            }

            playerDao.update(player.copy(proximaDataFalha = today))
        }
    }

    private suspend fun getOrCreatePlayer(today: LocalDate): Player {
        playerDao.getPlayer()?.let { return it }
        val player = Player(proximaDataFalha = today)
        playerDao.insert(player)
        return playerDao.getPlayer() ?: player
    }
}
