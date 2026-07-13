package com.example.sologym.model

object WorkoutTimer {
    fun elapsedSeconds(startedAtMillis: Long, currentTimeMillis: Long): Long =
        (currentTimeMillis - startedAtMillis).coerceAtLeast(0) / 1_000

    fun restoreMonotonicStart(
        startedAtEpochMillis: Long,
        currentEpochMillis: Long,
        currentElapsedRealtimeMillis: Long
    ): Long {
        val elapsedBeforeRestore =
            (currentEpochMillis - startedAtEpochMillis).coerceAtLeast(0)
        return currentElapsedRealtimeMillis - elapsedBeforeRestore
    }
}
