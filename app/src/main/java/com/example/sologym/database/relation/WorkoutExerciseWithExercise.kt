package com.example.sologym.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.TreinoExercicio

data class WorkoutExerciseWithExercise(
    @Embedded val treinoExercicio: TreinoExercicio,
    @Relation(
        entity = Exercicio::class,
        parentColumn = "exercicioId",
        entityColumn = "id"
    )
    val exercicio: CompleteExercise
)
