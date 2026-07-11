package com.example.sologym.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sologym.database.entity.Treino
import com.example.sologym.database.entity.TreinoExercicio

data class CompleteWorkout(
    @Embedded val treino: Treino,
    @Relation(
        entity = TreinoExercicio::class,
        parentColumn = "id",
        entityColumn = "treinoId"
    )
    val exercicios: List<WorkoutExerciseWithExercise>
)
