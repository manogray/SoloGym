package com.example.sologym.database.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.ExercicioSubstituto

data class ExerciseWithSubstitutes(
    @Embedded val exercicio: Exercicio,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExercicioSubstituto::class,
            parentColumn = "exercicioId",
            entityColumn = "substitutoId"
        )
    )
    val substitutos: List<Exercicio>
)
