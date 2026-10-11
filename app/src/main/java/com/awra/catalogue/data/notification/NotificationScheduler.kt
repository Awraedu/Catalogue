package com.awra.catalogue.data.notification

import android.content.Context
import androidx.work.*
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val NUDGE_WORK_NAME = "study_nudge_work"
    private const val CLASS_WORK_TAG = "class_reminder"

    fun scheduleAllReminders(context: Context) {
        // Clear all previous tasks to avoid orphans (especially when deleting items)
        cancelAllWork(context)

        val prefs = PreferenceManager(context)
        val remindersEnabled = runBlocking { prefs.studyReminders.first() }
        
        if (!remindersEnabled) {
            cancelAllWork(context)
            return
        }

        val database = AppDatabase.getDatabase(context)
        val repository = CatalogueRepository(database.catalogueDao())

        // 1. Schedule the Daily Study Nudge (Arrival Time)
        scheduleDailyNudge(context, prefs)

        // 2. Schedule Starting & Dismissed Reminders for the next 24 hours
        val allSchedules = runBlocking { repository.getAllSchedules().first() }
        val now = Calendar.getInstance()
        val currentDayOfWeek = when (now.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }

        allSchedules.forEach { schedule ->
            // Schedule "Starting Soon" reminder (5 mins ahead) for both classes AND tests
            val delay = calculateClassDelay(schedule.dayOfWeek, schedule.startTime, currentDayOfWeek, isAhead = true)
            if (delay >= 0) {
                val data = workDataOf(
                    "type" to if (schedule.isTest) "EXAM_START" else "CLASS",
                    "subject" to schedule.subjectName,
                    "room" to schedule.room,
                    "start" to schedule.startTime,
                    "end" to schedule.endTime
                )
                enqueueUniqueClassWork(context, "start_${schedule.id}", delay, data)
            }

            // Schedule "Class Dismissed" reminder (Exactly at endTime) - Only for classes
            if (!schedule.isTest) {
                schedule.endTime?.let { endTime ->
                    val dismissDelay = calculateClassDelay(schedule.dayOfWeek, endTime, currentDayOfWeek, isAhead = false)
                    if (dismissDelay >= 0) {
                        val dismissData = workDataOf(
                            "type" to "DISMISSED",
                            "subject" to schedule.subjectName
                        )
                        enqueueUniqueClassWork(context, "end_${schedule.id}", dismissDelay, dismissData)
                    }
                }
            }
        }
    }

    private fun enqueueUniqueClassWork(context: Context, id: String, delay: Long, data: Data) {
        val workRequest = OneTimeWorkRequestBuilder<StudyNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(CLASS_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            id,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun scheduleDailyNudge(context: Context, prefs: PreferenceManager) {
        val notificationTime = runBlocking { prefs.notificationTime.first() }
        val timeParts = notificationTime.split(" ")
        val clockParts = timeParts[0].split(":")
        var h = clockParts[0].toInt()
        val m = clockParts[1].toInt()
        val ampm = if (timeParts.size > 1) timeParts[1] else "PM"
        
        if (ampm == "PM" && h < 12) h += 12
        if (ampm == "AM" && h == 12) h = 0

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

    private fun calculateClassDelay(targetDay: Int, startTime: String, currentDay: Int, isAhead: Boolean): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance()
        
        val timeParts = startTime.split(":")
        val hour = timeParts[0].toInt()
        val min = timeParts[1].toInt()

        target.set(Calendar.HOUR_OF_DAY, hour)
        target.set(Calendar.MINUTE, min)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)

        // Subtract 5 minutes if it's a "starting soon" reminder
        if (isAhead) target.add(Calendar.MINUTE, -5)

        // Calculate day difference
        var daysDiff = targetDay - currentDay
        if (daysDiff < 0 || (daysDiff == 0 && target.before(now))) {
            daysDiff += 7
        }
        
        target.add(Calendar.DAY_OF_YEAR, daysDiff)
        return target.timeInMillis - now.timeInMillis
    }

    private fun calculateTimeDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis - now.timeInMillis
    }

    fun cancelAllWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(NUDGE_WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag(CLASS_WORK_TAG)
    }
}
