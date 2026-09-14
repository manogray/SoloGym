package com.example.sologym.repository

import com.example.sologym.database.dao.ExerciseDao
import com.example.sologym.database.dao.SeriesDao
import com.example.sologym.database.dao.SubstituteDao
import com.example.sologym.database.dao.WorkoutExerciseDao
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.ExercicioSubstituto
import com.example.sologym.database.entity.Serie
import com.example.sologym.model.AerobicExercise
import com.example.sologym.model.ExerciseType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val seriesDao: SeriesDao,
    private val substituteDao: SubstituteDao,
    private val workoutExerciseDao: WorkoutExerciseDao
) {
    fun getAllExercises(): Flow<List<Exercicio>> = exerciseDao.getAll()

    suspend fun getCompleteExercise(id: Long) = exerciseDao.getCompleteById(id)

    suspend fun insertExercise(exercicio: Exercicio, series: List<Serie>, substitutos: List<Long>): Long {
        validateExercise(exercicio, series, substitutos)
        val exerciseId = exerciseDao.insert(exercicio)
        
        val seriesWithId = series.map { it.copy(exercicioId = exerciseId) }
        seriesDao.insertAll(seriesWithId)
        
        val substitutesWithId = substitutos.map { ExercicioSubstituto(exerciseId, it) }
        substituteDao.insertAll(substitutesWithId)
        
        return exerciseId
    }

    suspend fun updateExercise(exercicio: Exercicio, series: List<Serie>, substitutos: List<Long>) {
        validateExercise(exercicio, series, substitutos)
        exerciseDao.update(exercicio)
        
        seriesDao.deleteByExerciseId(exercicio.id)
        seriesDao.insertAll(series.map { it.copy(exercicioId = exercicio.id) })
        
        substituteDao.deleteByExerciseId(exercicio.id)
        substituteDao.insertAll(substitutos.map { ExercicioSubstituto(exercicio.id, it) })
    }

    suspend fun deleteExercise(exercicio: Exercicio) {
        if (workoutExerciseDao.isExerciseUsedInWorkouts(exercicio.id)) {
            throw IllegalStateException("Não é possível excluir: o exercício faz parte de um ou mais treinos.")
        }
        exerciseDao.delete(exercicio)
    }

    private fun validateExercise(
        exercicio: Exercicio,
        series: List<Serie>,
        substitutos: List<Long>,
    ) {
        require(exercicio.nome.isNotBlank()) { "Nome é obrigatório." }
        when (exercicio.tipo) {
            ExerciseType.STRENGTH -> {
                require(exercicio.descansoSegundos > 0) { "Descanso inválido." }
                require(series.isNotEmpty()) { "O exercício deve possuir ao menos uma série." }
                require(exercicio.duracaoMinutos == null) {
                    "Exercícios de força não possuem duração aeróbica."
                }
            }
            ExerciseType.AEROBIC -> {
                require(AerobicExercise.entries.any { it.displayName == exercicio.nome }) {
                    "Exercício aeróbico inválido."
                }
                require((exercicio.duracaoMinutos ?: 0) > 0) { "Duração inválida." }
                require(series.isEmpty()) { "Exercícios aeróbicos não possuem séries." }
                require(substitutos.isEmpty()) { "Exercícios aeróbicos não possuem substitutos." }
            }
        }
    }
}
