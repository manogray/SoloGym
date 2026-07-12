package com.example.sologym.repository

import androidx.room.withTransaction
import com.example.sologym.database.SoloGymDatabase
import com.example.sologym.database.dao.HistoryDao
import com.example.sologym.database.dao.PlayerDao
import com.example.sologym.database.dao.WorkoutDao
import com.example.sologym.database.entity.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.sologym.model.PlayerProgression
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val database: SoloGymDatabase,
    private val playerDao: PlayerDao,
    private val workoutDao: WorkoutDao,
    private val historyDao: HistoryDao
) {
    fun observePlayer(): Flow<Player> = playerDao.observePlayer().map { it ?: Player() }

    suspend fun evaluateMissedWorkouts(today: LocalDate = LocalDate.now()) {
        database.withTransaction {
            var player = getOrCreatePlayer(today)
            var dateToEvaluate = player.proximaDataFalha

            while (dateToEvaluate.isBefore(today)) {
                val hasScheduledWorkout = workoutDao.existsByDayOfWeek(dateToEvaluate.dayOfWeek, 0)
                val completed = historyDao.countByPeriodOnce(
                    dateToEvaluate.atStartOfDay(),
                    dateToEvaluate.plusDays(1).atStartOfDay()
                ) > 0

                if (hasScheduledWorkout && !completed) {
                    player = PlayerProgression.missedWorkout(player)
                }
                dateToEvaluate = dateToEvaluate.plusDays(1)
            }

            playerDao.update(player.copy(proximaDataFalha = today))
        }
    }

    suspend fun recordCompletedWorkout(today: LocalDate = LocalDate.now()) {
        database.withTransaction {
            val player = getOrCreatePlayer(today)
            playerDao.update(PlayerProgression.completedWorkout(player))
        }
    }

    private suspend fun getOrCreatePlayer(today: LocalDate): Player {
        playerDao.getPlayer()?.let { return it }
        val player = Player(proximaDataFalha = today)
        playerDao.insert(player)
        return playerDao.getPlayer() ?: player
    }
}
