package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tpo_settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_AGE = intPreferencesKey("user_age")
        private val KEY_CYCLE_LENGTH = intPreferencesKey("cycle_length")
        private val KEY_PERIOD_DURATION = intPreferencesKey("period_duration")
        private val KEY_LAST_PERIOD_START = stringPreferencesKey("last_period_start")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            onboardingCompleted = preferences[KEY_ONBOARDING_COMPLETED] ?: false,
            name = preferences[KEY_USER_NAME] ?: "",
            age = preferences[KEY_USER_AGE] ?: 25,
            cycleLength = preferences[KEY_CYCLE_LENGTH] ?: 28,
            periodDuration = preferences[KEY_PERIOD_DURATION] ?: 5,
            lastPeriodStart = preferences[KEY_LAST_PERIOD_START] ?: LocalDate.now().toString(),
            notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: false,
            themeMode = preferences[KEY_THEME_MODE] ?: "system"
        )
    }

    suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = settings.onboardingCompleted
            preferences[KEY_USER_NAME] = settings.name
            preferences[KEY_USER_AGE] = settings.age
            preferences[KEY_CYCLE_LENGTH] = settings.cycleLength
            preferences[KEY_PERIOD_DURATION] = settings.periodDuration
            preferences[KEY_LAST_PERIOD_START] = settings.lastPeriodStart
            preferences[KEY_NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
            preferences[KEY_THEME_MODE] = settings.themeMode
        }
    }

    suspend fun resetData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class UserSettings(
    val onboardingCompleted: Boolean,
    val name: String,
    val age: Int,
    val cycleLength: Int,
    val periodDuration: Int,
    val lastPeriodStart: String, // yyyy-MM-dd
    val notificationsEnabled: Boolean,
    val themeMode: String = "system"
)
