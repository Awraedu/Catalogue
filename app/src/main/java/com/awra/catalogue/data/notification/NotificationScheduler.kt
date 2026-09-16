package com.awra.catalogue.data.notification

import android.content.Context
import androidx.work.*
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME = "study_notification_work"

    fun scheduleDailyCheck(context: Context) {
        val prefs = PreferenceManager(context)
        val remindersEnabled = runBlocking { prefs.studyReminders.first() }
        
        if (!remindersEnabled) {
            cancelAllWork(context)
            return
        }

        // Change from a simple day delay check to a more frequent hourly interval task.
        // This ensures class starting hours can be detected on time!
        val workRequest = PeriodicWorkRequestBuilder<StudyNotificationWorker>(
            30, TimeUnit.MINUTES
        )
            .addTag(WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelAllWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME)
    }
}
