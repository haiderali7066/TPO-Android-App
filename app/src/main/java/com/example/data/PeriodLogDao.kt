package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodLogDao {
    @Query("SELECT * FROM period_logs ORDER BY startDate DESC")
    fun getAllLogs(): Flow<List<PeriodLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PeriodLog)

    @Delete
    suspend fun deleteLog(log: PeriodLog)

    @Query("DELETE FROM period_logs")
    suspend fun clearAll()
}
