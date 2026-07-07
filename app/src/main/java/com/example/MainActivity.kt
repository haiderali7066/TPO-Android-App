package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.theme.TPOTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private val viewModel: TPOViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
            val logsState by viewModel.logsState.collectAsStateWithLifecycle()
            val predictionsState by viewModel.predictionsState.collectAsStateWithLifecycle()

            val isDarkTheme = when (settingsState?.themeMode) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            TPOTheme(darkTheme = isDarkTheme) {
                // Loader / Onboarding check
                if (settingsState == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (!settingsState!!.onboardingCompleted) {
                    OnboardingScreen(
                        onComplete = { name, age, cycleLength, periodDuration, lastPeriodStart ->
                            viewModel.saveSettings(
                                name = name,
                                age = age,
                                cycleLength = cycleLength,
                                periodDuration = periodDuration,
                                lastPeriodStart = lastPeriodStart,
                                notificationsEnabled = false // Initial false / 0 as requested
                            )
                        }
                    )
                } else {
                    // Main app with bottom navigation
                    var currentTab by remember { mutableStateOf("home") }

                    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
                    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("bottom_nav_bar"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == "home",
                                    onClick = { currentTab = "home" },
                                    label = { Text("Home") },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    modifier = Modifier.testTag("tab_home")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "calendar",
                                    onClick = { currentTab = "calendar" },
                                    label = { Text("Calendar") },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == "calendar") Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                            contentDescription = "Calendar"
                                        )
                                    },
                                    modifier = Modifier.testTag("tab_calendar")
                                )
                                NavigationBarItem(
                                    selected = currentTab == "settings",
                                    onClick = { currentTab = "settings" },
                                    label = { Text("Settings") },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == "settings") Icons.Filled.Settings else Icons.Outlined.Settings,
                                            contentDescription = "Settings"
                                        )
                                    },
                                    modifier = Modifier.testTag("tab_settings")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = currentTab, label = "tab_transitions") { tab ->
                                when (tab) {
                                    "home" -> HomeScreen(
                                        settings = settingsState!!,
                                        predictions = predictionsState,
                                        currentMonth = currentMonth,
                                        onMonthChange = { viewModel.selectMonth(it) },
                                        selectedDate = selectedDate,
                                        onDateSelect = { viewModel.selectDate(it) }
                                    )
                                    "calendar" -> CalendarScreen(
                                        predictions = predictionsState,
                                        logs = logsState,
                                        onAddLog = { start, end -> viewModel.addPeriodLog(start, end) },
                                        onDeleteLog = { log -> viewModel.deletePeriodLog(log) }
                                    )
                                    "settings" -> SettingsScreen(
                                        currentSettings = settingsState!!,
                                        onSaveSettings = { name, age, cycle, duration, lastStart, notif, theme ->
                                            viewModel.saveSettings(
                                                name = name,
                                                age = age,
                                                cycleLength = cycle,
                                                periodDuration = duration,
                                                lastPeriodStart = lastStart,
                                                notificationsEnabled = notif,
                                                themeMode = theme
                                            )
                                        },
                                        onResetData = { viewModel.resetData() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
