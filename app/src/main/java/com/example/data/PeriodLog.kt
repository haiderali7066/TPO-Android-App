package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_logs")
data class PeriodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String, // yyyy-MM-dd
    val endDate: String    // yyyy-MM-dd
)
