package com.example.data

import kotlinx.coroutines.flow.Flow

class PeriodRepository(private val periodLogDao: PeriodLogDao) {
    val allLogs: Flow<List<PeriodLog>> = periodLogDao.getAllLogs()

    suspend fun insertLog(log: PeriodLog) {
        periodLogDao.insertLog(log)
    }

    suspend fun deleteLog(log: PeriodLog) {
        periodLogDao.deleteLog(log)
    }

    suspend fun clearAll() {
        periodLogDao.clearAll()
    }
}
