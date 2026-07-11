package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "serie",
    foreignKeys = [
        ForeignKey(
            entity = Exercicio::class,
            parentColumns = ["id"],
            childColumns = ["exercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exercicioId"])]
)
data class Serie(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exercicioId: Long,
    val ordem: Int,
    val repeticoes: Int
)
