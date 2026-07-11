package com.example.sologym.repository

import com.example.sologym.database.dao.WorkoutDao
import com.example.sologym.database.dao.WorkoutExerciseDao
import com.example.sologym.database.entity.Treino
import com.example.sologym.database.entity.TreinoExercicio
import com.example.sologym.model.WorkoutStatus
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao
) {
    fun getWorkoutByDay(day: DayOfWeek): Flow<Treino?> = workoutDao.getByDayOfWeek(day)

    fun getCompleteWorkoutByDay(day: DayOfWeek) = workoutDao.getCompleteByDayOfWeek(day)

    fun getCompleteWorkoutById(id: Long) = workoutDao.getCompleteById(id)

    fun getAllWorkouts(): Flow<List<Treino>> = workoutDao.getAll()

    suspend fun insertWorkout(treino: Treino, exercises: List<TreinoExercicio>) {
        if (workoutDao.existsByDayOfWeek(treino.diaSemana, 0)) {
            throw IllegalStateException("Já existe um treino cadastrado para este dia.")
        }
        val workoutId = workoutDao.insert(treino)
        workoutExerciseDao.insertAll(exercises.map { it.copy(treinoId = workoutId) })
    }

    suspend fun updateWorkout(treino: Treino, exercises: List<TreinoExercicio>) {
        if (workoutDao.existsByDayOfWeek(treino.diaSemana, treino.id)) {
            throw IllegalStateException("Já existe um treino cadastrado para este dia.")
        }
        workoutDao.update(treino)
        workoutExerciseDao.deleteByWorkoutId(treino.id)
        workoutExerciseDao.insertAll(exercises.map { it.copy(treinoId = treino.id) })
    }

    suspend fun updateStatus(workoutId: Long, status: WorkoutStatus) {
        workoutDao.updateStatus(workoutId, status)
    }

    suspend fun deleteWorkout(treino: Treino) {
        workoutDao.delete(treino)
    }
}
