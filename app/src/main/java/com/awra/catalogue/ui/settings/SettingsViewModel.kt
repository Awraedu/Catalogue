package com.awra.catalogue.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsViewModel(application: Application, private val preferenceManager: PreferenceManager) : AndroidViewModel(application) {
    val darkMode = preferenceManager.darkMode
    val studyReminders = preferenceManager.studyReminders
    val notificationTime = preferenceManager.notificationTime
    val selectedTheme = preferenceManager.selectedTheme
    val userName = preferenceManager.userName

    private val _cacheSize = MutableStateFlow("Calculated...")
    val cacheSize: StateFlow<String> = _cacheSize

    val appVersion: String = try {
        val context = getApplication<Application>()
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionCode = androidx.core.content.pm.PackageInfoCompat.getLongVersionCode(packageInfo)
        "${packageInfo.versionName} ($versionCode)"
    } catch (e: Exception) {
        "1.0.0 (Unknown)"
    }

    init {
        updateCacheSize()
    }

    fun setDarkMode(mode: String) = preferenceManager.setDarkMode(mode)
    fun setStudyReminders(enabled: Boolean) = preferenceManager.setStudyReminders(enabled)
    fun setNotificationTime(time: String) = preferenceManager.setNotificationTime(time)
    fun setSelectedTheme(theme: String) = preferenceManager.setSelectedTheme(theme)
    fun setUserName(name: String) = preferenceManager.setUserName(name)

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            
            // Delete contents of internal cache
            context.cacheDir.listFiles()?.forEach { deleteDir(it) }
            
            // Delete contents of external cache if it exists
            context.externalCacheDir?.listFiles()?.forEach { deleteDir(it) }
            
            // Update UI
            updateCacheSize()
        }
    }

    private fun updateCacheSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            var size = getDirSize(context.cacheDir)
            context.externalCacheDir?.let { size += getDirSize(it) }
            
            _cacheSize.value = formatFileSize(size)
        }
    }

    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getDirSize(file) else file.length()
        }
        return size
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { deleteDir(it) }
        }
        return dir.delete()
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0.0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        val formattedValue = size / Math.pow(1024.0, digitGroups.toDouble())
        return String.format("%.1f %s", formattedValue, units[digitGroups.coerceIn(0, units.size - 1)])
    }
}
