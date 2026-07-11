package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercicio")
data class Exercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val descansoSegundos: Int
)
