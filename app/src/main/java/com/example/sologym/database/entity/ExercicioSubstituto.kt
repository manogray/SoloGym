package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "exercicio_substituto",
    primaryKeys = ["exercicioId", "substitutoId"],
    foreignKeys = [
        ForeignKey(
            entity = Exercicio::class,
            parentColumns = ["id"],
            childColumns = ["exercicioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercicio::class,
            parentColumns = ["id"],
            childColumns = ["substitutoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercicioId"]),
        Index(value = ["substitutoId"])
    ]
)
data class ExercicioSubstituto(
    val exercicioId: Long,
    val substitutoId: Long
)
