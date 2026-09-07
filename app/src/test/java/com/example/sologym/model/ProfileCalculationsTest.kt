package com.example.sologym.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileCalculationsTest {
    @Test
    fun ageDoesNotIncreaseBeforeBirthday() {
        val birthDate = LocalDate.of(2000, 9, 8)

        assertEquals(25, ProfileCalculations.ageOn(birthDate, LocalDate.of(2026, 9, 7)))
    }

    @Test
    fun ageIncreasesOnBirthday() {
        val birthDate = LocalDate.of(2000, 9, 7)

        assertEquals(26, ProfileCalculations.ageOn(birthDate, LocalDate.of(2026, 9, 7)))
    }
}
