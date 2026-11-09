package com.awra.catalogue.data.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.awra.catalogue.ui.pdf.PdfItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

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

    private val _selectedStream = MutableStateFlow(prefs.getString("selected_stream", "NATURAL") ?: "NATURAL")
    val selectedStream: StateFlow<String> = _selectedStream

    private val _userName = MutableStateFlow(prefs.getString("user_name", "User") ?: "User")
    val userName: StateFlow<String> = _userName

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted

    private val _studyGoal = MutableStateFlow(prefs.getInt("study_goal", 15)) // Default 15 mins
    val studyGoal: StateFlow<Int> = _studyGoal

    var lastNotifyDate: Long
        get() = prefs.getLong("last_notify_date", 0)
        set(value) = prefs.edit().putLong("last_notify_date", value).apply()

    // =========================================================================
    // 🧠 DUOLINGO-STYLE SMART ENGAGEMENT & HABIT LEARNING METRICS
    // =========================================================================

    /** Timestamp of the last time the user opened the app or studied */
    var lastActiveTimestamp: Long
        get() = prefs.getLong("last_active_timestamp", 0L)
        set(value) = prefs.edit().putLong("last_active_timestamp", value).apply()

    /** How many daily nudges were sent without the user opening the app in between */
    var consecutiveIgnoredNudges: Int
        get() = prefs.getInt("consecutive_ignored_nudges", 0)
        set(value) = prefs.edit().putInt("consecutive_ignored_nudges", value).apply()

    /** Rolling average hour of the day (0..23) when the user actually studies (-1 if not learned yet) */
    var learnedStudyHour: Int
        get() = prefs.getInt("learned_study_hour", -1)
        set(value) = prefs.edit().putInt("learned_study_hour", value).apply()

    /** Current daily study streak */
    var currentStreak: Int
        get() = prefs.getInt("current_streak", 0)
        set(value) = prefs.edit().putInt("current_streak", value).apply()

    /** Last day (midnight millis) the streak was incremented */
    var lastStreakDate: Long
        get() = prefs.getLong("last_streak_date", 0L)
        set(value) = prefs.edit().putLong("last_streak_date", value).apply()

    /**
     * Call this in MainActivity.onResume() or when a study session / quiz finishes!
     * It resets notification fatigue, updates their streak, and learns their preferred study hour.
     */
    fun recordUserActivity() {
        val now = Calendar.getInstance()
        val nowMs = now.timeInMillis
        val currentHour = now.get(Calendar.HOUR_OF_DAY)

        // 1. Reset ignored nudges counter because the user is active!
        consecutiveIgnoredNudges = 0
        lastActiveTimestamp = nowMs

        // 2. Habit Learning: Smoothly shift learnedStudyHour toward their actual active hour
        val previousLearned = learnedStudyHour
        learnedStudyHour = if (previousLearned == -1) {
            currentHour
        } else {
            // Weighted average (70% old habit, 30% new activity)
            Math.round(previousLearned * 0.7f + currentHour * 0.3f).coerceIn(6, 23)
        }

        // 3. Update Daily Streak
        val todayMidnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val oneDayMs = 24 * 60 * 60 * 1000L
        val lastDate = lastStreakDate

        when {
            lastDate == todayMidnight -> {
                // Already counted today
            }
            lastDate == todayMidnight - oneDayMs -> {
                // Studied yesterday! Increment streak
                currentStreak += 1
                lastStreakDate = todayMidnight
            }
            else -> {
                // Streak broken or first day
                currentStreak = 1
                lastStreakDate = todayMidnight
            }
        }
    }

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
        // Reset learned hour when user manually picks a new time
        learnedStudyHour = -1
    }

    fun setSelectedTheme(theme: String) {
        prefs.edit().putString("selected_theme", theme).apply()
        _selectedTheme.value = theme
    }

    fun setSelectedStream(stream: String) {
        prefs.edit().putString("selected_stream", stream).apply()
        _selectedStream.value = stream
    }

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
        _userName.value = name
    }

    fun getCustomPdfs(subjectId: Long): List<PdfItemModel> {
        val jsonString = prefs.getString("custom_pdfs_$subjectId", null) ?: return emptyList()
        val list = mutableListOf<PdfItemModel>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id")
                val title = obj.optString("title")
                val subtitle = obj.optString("subtitle")
                val uriStr = obj.optString("uri")
                list.add(
                    PdfItemModel(
                        id = id,
                        title = title,
                        subtitle = subtitle,
                        uri = if (uriStr.isNotEmpty()) Uri.parse(uriStr) else null
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveCustomPdf(subjectId: Long, pdf: PdfItemModel) {
        val current = getCustomPdfs(subjectId).toMutableList()
        current.removeAll { it.id == pdf.id }
        current.add(pdf)

        try {
            val array = JSONArray()
            for (item in current) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("title", item.title)
                obj.put("subtitle", item.subtitle)
                obj.put("uri", item.uri?.toString() ?: "")
                array.put(obj)
            }
            prefs.edit().putString("custom_pdfs_$subjectId", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun getAllCustomPdfs(): List<PdfItemModel> {
        val list = mutableListOf<PdfItemModel>()
        try {
            val allPrefs = prefs.all
            for ((key, value) in allPrefs) {
                if (key.startsWith("custom_pdfs_") && value is String) {
                    val array = JSONArray(value)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val id = obj.optString("id")
                        val title = obj.optString("title")
                        val subtitle = obj.optString("subtitle")
                        val uriStr = obj.optString("uri")
                        list.add(
                            PdfItemModel(
                                id = id,
                                title = title,
                                subtitle = subtitle,
                                uri = if (uriStr.isNotEmpty()) Uri.parse(uriStr) else null
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {}
        return list
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