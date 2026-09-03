package com.awra.catalogue.data.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _darkMode = MutableStateFlow(prefs.getString("dark_mode", "SYSTEM") ?: "SYSTEM")
    val darkMode: StateFlow<String> = _darkMode

    private val _studyReminders = MutableStateFlow(prefs.getBoolean("study_reminders", true))
    val studyReminders: StateFlow<Boolean> = _studyReminders

    private val _notificationTime = MutableStateFlow(prefs.getString("notification_time", "17:00") ?: "17:00")
    val notificationTime: StateFlow<String> = _notificationTime

    private val _selectedTheme = MutableStateFlow(prefs.getString("selected_theme", "DEFAULT") ?: "DEFAULT")
    val selectedTheme: StateFlow<String> = _selectedTheme

    fun setDarkMode(mode: String) {
        prefs.edit().putString("dark_mode", mode).apply()
        _darkMode.value = mode
    }

    fun setStudyReminders(enabled: Boolean) {
        prefs.edit().putBoolean("study_reminders", enabled).apply()
        _studyReminders.value = enabled
    }

    fun setNotificationTime(time: String) {
        prefs.edit().putString("notification_time", time).apply()
        _notificationTime.value = time
    }

    fun setSelectedTheme(theme: String) {
        prefs.edit().putString("selected_theme", theme).apply()
        _selectedTheme.value = theme
    }
}
