package com.example.sologym.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.Serie

data class ExerciseWithSeries(
    @Embedded val exercicio: Exercicio,
    @Relation(
        parentColumn = "id",
        entityColumn = "exercicioId"
    )
    val series: List<Serie>
)
