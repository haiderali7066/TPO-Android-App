package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.worker.NotificationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TPOViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)
    private val database = AppDatabase.getDatabase(application)
    private val repository = PeriodRepository(database.periodLogDao())

    val settingsState: StateFlow<UserSettings?> = settingsManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val logsState: StateFlow<List<PeriodLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Derived State: Combine settings and Room logs to compute cycle predictions
    val predictionsState: StateFlow<List<CyclePrediction>> = combine(settingsState, logsState) { settings, logs ->
        if (settings == null) {
            emptyList()
        } else {
            val predictions = CycleCalculator.calculatePredictions(
                lastPeriodStartStr = settings.lastPeriodStart,
                cycleLength = settings.cycleLength,
                periodDuration = settings.periodDuration,
                logs = logs,
                count = 12
            )
            // Automatically schedule notifications in background whenever calculations update
            NotificationScheduler.scheduleNotifications(
                context = getApplication(),
                predictions = predictions,
                enabled = settings.notificationsEnabled
            )
            predictions
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Calendar Screen State
    val currentMonth = MutableStateFlow<LocalDate>(LocalDate.now().withDayOfMonth(1))
    val selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())

    fun selectMonth(month: LocalDate) {
        currentMonth.value = month.withDayOfMonth(1)
    }

    fun selectDate(date: LocalDate?) {
        selectedDate.value = date
    }

    fun saveSettings(
        name: String,
        age: Int,
        cycleLength: Int,
        periodDuration: Int,
        lastPeriodStart: String,
        notificationsEnabled: Boolean,
        themeMode: String = settingsState.value?.themeMode ?: "system"
    ) {
        viewModelScope.launch {
            val updated = UserSettings(
                onboardingCompleted = true,
                name = name,
                age = age,
                cycleLength = cycleLength,
                periodDuration = periodDuration,
                lastPeriodStart = lastPeriodStart,
                notificationsEnabled = notificationsEnabled,
                themeMode = themeMode
            )
            settingsManager.saveSettings(updated)
        }
    }

    fun addPeriodLog(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            repository.insertLog(PeriodLog(startDate = startDate.toString(), endDate = endDate.toString()))
            // Update the DataStore last period start if this is more recent
            val currentSettings = settingsState.value
            if (currentSettings != null) {
                val latestLogDate = startDate
                val existingLastPeriod = LocalDate.parse(currentSettings.lastPeriodStart)
                if (latestLogDate.isAfter(existingLastPeriod)) {
                    settingsManager.saveSettings(
                        currentSettings.copy(lastPeriodStart = latestLogDate.toString())
                    )
                }
            }
        }
    }

    fun deletePeriodLog(log: PeriodLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.clearAll()
            settingsManager.resetData()
        }
    }
}
