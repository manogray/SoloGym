package com.example.sologym.repository

import com.example.sologym.database.dao.HistoryDao
import com.example.sologym.database.entity.HistoricoTreino
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getAllHistory(): Flow<List<HistoricoTreino>> = historyDao.getAll()

    suspend fun insertHistory(historicoTreino: HistoricoTreino) {
        historyDao.insert(historicoTreino)
    }

    fun getLastWorkouts(limit: Int): Flow<List<HistoricoTreino>> = historyDao.getLastWorkouts(limit)
}
