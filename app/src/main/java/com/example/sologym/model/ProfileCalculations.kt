package com.example.sologym.model

import java.time.LocalDate
import java.time.Period

object ProfileCalculations {
    fun ageOn(birthDate: LocalDate, date: LocalDate = LocalDate.now()): Int =
        Period.between(birthDate, date).years.coerceAtLeast(0)
}
