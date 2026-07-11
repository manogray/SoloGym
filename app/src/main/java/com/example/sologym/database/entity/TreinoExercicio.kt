package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "treino_exercicio",
    primaryKeys = ["treinoId", "exercicioId"],
    foreignKeys = [
        ForeignKey(
            entity = Treino::class,
            parentColumns = ["id"],
            childColumns = ["treinoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercicio::class,
            parentColumns = ["id"],
            childColumns = ["exercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["treinoId"]),
        Index(value = ["exercicioId"])
    ]
)
data class TreinoExercicio(
    val treinoId: Long,
    val exercicioId: Long,
    val ordem: Int
)
