package com.awra.catalogue.data.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _darkMode = MutableStateFlow(prefs.getString("dark_mode", "LIGHT") ?: "LIGHT")
    val darkMode: StateFlow<String> = _darkMode

    private val _studyReminders = MutableStateFlow(prefs.getBoolean("study_reminders", true))
    val studyReminders: StateFlow<Boolean> = _studyReminders

    private val _notificationTime = MutableStateFlow(prefs.getString("notification_time", "17:00") ?: "17:00")
    val notificationTime: StateFlow<String> = _notificationTime

    private val _selectedTheme = MutableStateFlow(prefs.getString("selected_theme", "VIBRANT_ORANGE") ?: "VIBRANT_ORANGE")
    val selectedTheme: StateFlow<String> = _selectedTheme

    private val _userName = MutableStateFlow(prefs.getString("user_name", "User") ?: "User")
    val userName: StateFlow<String> = _userName

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted

    private val _studyGoal = MutableStateFlow(prefs.getInt("study_goal", 15)) // Default 15 mins
    val studyGoal: StateFlow<Int> = _studyGoal

    var lastNotifyDate: Long
        get() = prefs.getLong("last_notify_date", 0)
        set(value) = prefs.edit().putLong("last_notify_date", value).apply()

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

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
        _userName.value = name
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
        _isOnboardingCompleted.value = completed
    }

    fun setStudyGoal(minutes: Int) {
        prefs.edit().putInt("study_goal", minutes).apply()
        _studyGoal.value = minutes
    }
}
