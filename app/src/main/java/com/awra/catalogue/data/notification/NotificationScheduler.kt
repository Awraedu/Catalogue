package com.awra.catalogue.data.notification

import android.content.Context
import androidx.work.*
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.model.ClassSchedule
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    const val NUDGE_WORK_NAME = "study_nudge_work"
    const val STREAK_SAVER_WORK_NAME = "streak_saver_work"
    const val CLASS_WORK_TAG = "class_reminder"

    private val schedulerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Non-blocking entry point safe to call from UI, ViewModels, or BroadcastReceivers.
     */
    fun scheduleAllReminders(context: Context) {
        val appContext = context.applicationContext
        schedulerScope.launch {
            scheduleAllRemindersSuspend(appContext)
        }
    }

    /**
     * Suspend entry point for calling directly inside CoroutineWorkers.
     */
    suspend fun scheduleAllRemindersSuspend(context: Context) {
        val prefs = PreferenceManager(context)
        val remindersEnabled = prefs.studyReminders.first()

        if (!remindersEnabled) {
            cancelAllWork(context)
            return
        }

        val database = AppDatabase.getDatabase(context)
        val repository = CatalogueRepository(database.catalogueDao())
        val allSchedules = repository.getAllSchedules().first()

        // 1. Schedule Smart Daily Study Nudge (with Duolingo Send-Time Optimization)
        scheduleDailyNudge(context, prefs)

        // 2. Schedule Class & Exam Reminders
        scheduleClassAndExamReminders(context, allSchedules)
    }

    /**
     * Reschedules ONLY the daily nudge (used by StudyNotificationWorker so it doesn't
     * accidentally cancel ongoing class reminders).
     */
    suspend fun scheduleDailyNudge(context: Context, prefs: PreferenceManager) {
        val notificationTime = prefs.notificationTime.first()
        var (h, m) = parseTime24Or12(notificationTime)

        // 🧠 DUOLINGO SEND-TIME OPTIMIZATION:
        // If user ignored 2+ nudges at their set time, and we learned they actually study at a different hour,
        // automatically shift the nudge to their actual active study hour!
        if (prefs.consecutiveIgnoredNudges >= 2 && prefs.learnedStudyHour in 6..22) {
            h = prefs.learnedStudyHour
            m = 0
        }

        val delay = calculateTimeDelay(h, m)
        val data = workDataOf("type" to "NUDGE")

        val workRequest = OneTimeWorkRequestBuilder<StudyNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(NUDGE_WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            NUDGE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Schedules an evening "Streak Saver" notification (e.g., at 8:30 PM) if the user has an active
     * streak (>= 2 days) and still hasn't completed their goal earlier in the day.
     */
    fun scheduleEveningStreakSaver(context: Context) {
        val now = Calendar.getInstance()
        if (now.get(Calendar.HOUR_OF_DAY) >= 20) return // Don't schedule if it's already past 8 PM

        val delay = calculateTimeDelay(20, 30) // 8:30 PM local time
        val data = workDataOf("type" to "STREAK_SAVER")

        val workRequest = OneTimeWorkRequestBuilder<StudyNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(NUDGE_WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            STREAK_SAVER_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleClassAndExamReminders(context: Context, allSchedules: List<ClassSchedule>) {
        // Clear old class reminders before re-enqueuing fresh ones
        WorkManager.getInstance(context).cancelAllWorkByTag(CLASS_WORK_TAG)

        allSchedules.forEach { schedule ->
            // 1. "Starting Soon" Reminder:
            // Exams give 15 minutes lead time to lock in; regular classes give 5 minutes.
            val leadMinutes = if (schedule.isTest) 15 else 5
            val startDelay = calculateScheduleDelay(
                schedule = schedule,
                timeStr = schedule.startTime,
                offsetMinutes = -leadMinutes
            )

            if (startDelay != null && startDelay > 0) {
                val data = workDataOf(
                    "scheduleId" to schedule.id,
                    "type" to if (schedule.isTest) "EXAM_START" else "CLASS",
                    "subject" to schedule.subjectName,
                    "room" to schedule.room,
                    "start" to schedule.startTime,
                    "end" to (schedule.endTime ?: ""),
                    "leadMins" to leadMinutes
                )
                enqueueUniqueClassWork(context, "start_${schedule.id}", startDelay, data)
            }

            // 2. "Class Dismissed" Post-Class Review Nudge (Only for regular classes)
            if (!schedule.isTest && !schedule.endTime.isNullOrBlank()) {
                val dismissDelay = calculateScheduleDelay(
                    schedule = schedule,
                    timeStr = schedule.endTime,
                    offsetMinutes = 0
                )
                if (dismissDelay != null && dismissDelay > 0) {
                    val dismissData = workDataOf(
                        "scheduleId" to schedule.id,
                        "type" to "DISMISSED",
                        "subject" to schedule.subjectName
                    )
                    enqueueUniqueClassWork(context, "end_${schedule.id}", dismissDelay, dismissData)
                }
            }
        }
    }

    private fun enqueueUniqueClassWork(context: Context, id: String, delay: Long, data: Data) {
        val builder = OneTimeWorkRequestBuilder<StudyNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(CLASS_WORK_TAG)

        WorkManager.getInstance(context).enqueueUniqueWork(
            id,
            ExistingWorkPolicy.REPLACE,
            builder.build()
        )
    }

    /**
     * Accurately calculates delay for BOTH one-time dated exams (`schedule.date != null`)
     * AND recurring weekly classes (`schedule.dayOfWeek`).
     */
    private fun calculateScheduleDelay(
        schedule: ClassSchedule,
        timeStr: String,
        offsetMinutes: Int
    ): Long? {
        return try {
            val (hour, min) = parseTime24Or12(timeStr)
            val now = Calendar.getInstance()
            val target = Calendar.getInstance()

            if (schedule.isTest && schedule.date != null && schedule.date > 0L) {
                // Exact date exam!
                target.timeInMillis = schedule.date
                target.set(Calendar.HOUR_OF_DAY, hour)
                target.set(Calendar.MINUTE, min)
                target.set(Calendar.SECOND, 0)
                target.set(Calendar.MILLISECOND, 0)
                target.add(Calendar.MINUTE, offsetMinutes)

                val diff = target.timeInMillis - now.timeInMillis
                if (diff > 0) diff else null // If exam date already passed, don't schedule!
            } else {
                // Weekly recurring class or weekly test
                val currentDay = when (now.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> 1
                    Calendar.TUESDAY -> 2
                    Calendar.WEDNESDAY -> 3
                    Calendar.THURSDAY -> 4
                    Calendar.FRIDAY -> 5
                    Calendar.SATURDAY -> 6
                    Calendar.SUNDAY -> 7
                    else -> 1
                }

                target.set(Calendar.HOUR_OF_DAY, hour)
                target.set(Calendar.MINUTE, min)
                target.set(Calendar.SECOND, 0)
                target.set(Calendar.MILLISECOND, 0)
                target.add(Calendar.MINUTE, offsetMinutes)

                var daysDiff = schedule.dayOfWeek - currentDay
                if (daysDiff < 0 || (daysDiff == 0 && !target.after(now))) {
                    daysDiff += 7
                }

                target.add(Calendar.DAY_OF_YEAR, daysDiff)
                target.timeInMillis - now.timeInMillis
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateTimeDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    private fun parseTime24Or12(rawTime: String): Pair<Int, Int> {
        return try {
            val cleaned = rawTime.trim().uppercase(Locale.US)
            val timeParts = cleaned.split(" ")
            val clockParts = timeParts[0].split(":")
            var h = clockParts[0].toInt()
            val m = clockParts[1].toInt()

            if (timeParts.size > 1) {
                val ampm = timeParts[1]
                if (ampm == "PM" && h < 12) h += 12
                if (ampm == "AM" && h == 12) h = 0
            }
            h.coerceIn(0, 23) to m.coerceIn(0, 59)
        } catch (e: Exception) {
            17 to 0 // Default 5:00 PM fallback
        }
    }

    fun cancelAllWork(context: Context) {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(NUDGE_WORK_NAME)
        wm.cancelUniqueWork(STREAK_SAVER_WORK_NAME)
        wm.cancelAllWorkByTag(NUDGE_WORK_NAME)
        wm.cancelAllWorkByTag(CLASS_WORK_TAG)
    }
}