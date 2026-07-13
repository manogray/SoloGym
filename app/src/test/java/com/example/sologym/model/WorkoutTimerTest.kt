package com.example.sologym.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutTimerTest {
    @Test
    fun calculatesElapsedTimeFromClockInsteadOfUpdateCount() {
        val oneHourInMillis = 60L * 60L * 1_000L

        val elapsed = WorkoutTimer.elapsedSeconds(
            startedAtMillis = 5_000L,
            currentTimeMillis = 5_000L + oneHourInMillis
        )

        assertEquals(3_600L, elapsed)
    }

    @Test
    fun ignoresIncompleteSecond() {
        val elapsed = WorkoutTimer.elapsedSeconds(
            startedAtMillis = 1_000L,
            currentTimeMillis = 3_999L
        )

        assertEquals(2L, elapsed)
    }

    @Test
    fun neverReturnsNegativeDuration() {
        val elapsed = WorkoutTimer.elapsedSeconds(
            startedAtMillis = 5_000L,
            currentTimeMillis = 4_000L
        )

        assertEquals(0L, elapsed)
    }

    @Test
    fun restoresElapsedTimeAfterDeviceReboot() {
        val currentElapsedRealtime = 10_000L
        val oneHourInMillis = 3_600_000L
        val restoredStart = WorkoutTimer.restoreMonotonicStart(
            startedAtEpochMillis = 1_000_000L,
            currentEpochMillis = 1_000_000L + oneHourInMillis,
            currentElapsedRealtimeMillis = currentElapsedRealtime
        )

        val elapsed = WorkoutTimer.elapsedSeconds(
            startedAtMillis = restoredStart,
            currentTimeMillis = currentElapsedRealtime
        )

        assertEquals(3_600L, elapsed)
    }
}
