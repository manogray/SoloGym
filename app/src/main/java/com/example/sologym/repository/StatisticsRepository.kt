package com.example.sologym.repository

import com.example.sologym.database.dao.HistoryDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getWorkoutsThisWeek(): Flow<Int> {
        val now = LocalDateTime.now()
        val start = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0)
        val end = now.withHour(23).withMinute(59)
        return historyDao.countByPeriod(start, end)
    }

    fun getWorkoutsThisMonth(): Flow<Int> {
        val now = LocalDateTime.now()
        val start = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0)
        val end = now.withHour(23).withMinute(59)
        return historyDao.countByPeriod(start, end)
    }

    fun getWorkoutsThisYear(): Flow<Int> {
        val now = LocalDateTime.now()
        val start = now.with(TemporalAdjusters.firstDayOfYear()).withHour(0).withMinute(0)
        val end = now.withHour(23).withMinute(59)
        return historyDao.countByPeriod(start, end)
    }

    fun getTotalDuration(): Flow<Long?> = historyDao.getTotalDuration()
}
