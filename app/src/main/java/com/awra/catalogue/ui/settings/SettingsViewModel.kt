package com.awra.catalogue.ui.settings

import androidx.lifecycle.ViewModel
import com.awra.catalogue.data.util.PreferenceManager

class SettingsViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    val darkMode = preferenceManager.darkMode
    val studyReminders = preferenceManager.studyReminders
    val notificationTime = preferenceManager.notificationTime
    val selectedTheme = preferenceManager.selectedTheme

    fun setDarkMode(mode: String) = preferenceManager.setDarkMode(mode)
    fun setStudyReminders(enabled: Boolean) = preferenceManager.setStudyReminders(enabled)
    fun setNotificationTime(time: String) = preferenceManager.setNotificationTime(time)
    fun setSelectedTheme(theme: String) = preferenceManager.setSelectedTheme(theme)

    fun clearCache() {
        // Mock clear cache logic
    }
}
