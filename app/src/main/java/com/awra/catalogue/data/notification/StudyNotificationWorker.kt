package com.awra.catalogue.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.awra.catalogue.MainActivity
import com.awra.catalogue.R
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.util.PreferenceManager
import com.awra.catalogue.ui.theme.getSubjectIcon
import kotlinx.coroutines.flow.first
import java.util.*

class StudyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = PreferenceManager(applicationContext)
        val remindersEnabled = prefs.studyReminders.first()
        
        if (!remindersEnabled) return Result.success()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CatalogueRepository(database.catalogueDao())

        val notificationTime = prefs.notificationTime.first()
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timeParts = notificationTime.split(":")
        val targetHour = timeParts[0].toInt()
        val targetMinute = timeParts[1].toInt()

        val isTimeForAfterClassNudge = currentHour > targetHour || (currentHour == targetHour && currentMinute >= targetMinute)

        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }

        val allSchedules = repository.getAllSchedules().first()
        val currentTimeString = String.format(Locale.US, "%02d:%02d", currentHour, currentMinute)
        val currentClassStarting = allSchedules.find {
            it.dayOfWeek == dayOfWeek && !it.isTest && it.startTime == currentTimeString
        }

        // Send a class reminder if it's currently starting, or check if it's the right window for daily study nudges
        if (currentClassStarting != null) {
            sendClassNotification(currentClassStarting.subjectName, currentClassStarting.room)
            return Result.success()
        }

        // Ensure daily study reminders only trigger once per day
        val lastNotifyDate = prefs.lastNotifyDate
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (lastNotifyDate == today) return Result.success()

        if (isTimeForAfterClassNudge) {
            sendNotification(prefs, repository, allSchedules, calendar, dayOfWeek, today)
            prefs.lastNotifyDate = today
        }

        return Result.success()
    }

    private fun sendClassNotification(subjectName: String, room: String) {
        val message = NotificationMessages.getClassStartReminder(subjectName, room)
        val icon = getSubjectIcon(subjectName)
        
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle("Class Reminder 🏛️")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            try { notify(NOTIFICATION_ID + 1, builder.build()) } catch (e: SecurityException) {}
        }
    }

    private suspend fun sendNotification(
        prefs: PreferenceManager,
        repository: CatalogueRepository,
        allSchedules: List<com.awra.catalogue.data.model.ClassSchedule>,
        calendar: Calendar,
        dayOfWeek: Int,
        startOfDay: Long
    ) {
        val studiedTodayCount = repository.getStudySessionsCountToday(startOfDay)
        if (studiedTodayCount > 0) {
            if (Math.random() > 0.3) return // 70% chance to skip if already studied
        }
        
        val topicsPerformance = repository.getAllTopicsPerformance().first()
        val weakTopic = topicsPerformance
            .filter { it.totalAttempts > 0 }
            .minByOrNull { it.correctAttempts.toFloat() / it.totalAttempts }

        val tomorrow = Calendar.getInstance().apply { 
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val tomorrowDay = when (tomorrow.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
        
        tomorrow.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val tomorrowStart = tomorrow.timeInMillis
        val tomorrowEnd = tomorrowStart + 24 * 60 * 60 * 1000

        // Tiered exam checks using timestamps
        val nowMs = System.currentTimeMillis()
        val threeDaysStart = nowMs + 2 * 24 * 60 * 60 * 1000
        val threeDaysEnd = nowMs + 3 * 24 * 60 * 60 * 1000
        val weekStart = nowMs + 6 * 24 * 60 * 60 * 1000
        val weekEnd = nowMs + 7 * 24 * 60 * 60 * 1000

        val testTomorrow = allSchedules.find { 
            it.isTest && (
                (it.date != null && it.date >= tomorrowStart && it.date < tomorrowEnd) ||
                (it.date == null && it.dayOfWeek == tomorrowDay)
            )
        }

        val testIn3Days = allSchedules.find {
            it.isTest && it.date != null && it.date >= threeDaysStart && it.date < threeDaysEnd
        }

        val testInWeek = allSchedules.find {
            it.isTest && it.date != null && it.date >= weekStart && it.date < weekEnd
        }

        val lastClassToday = allSchedules
            .filter { it.dayOfWeek == dayOfWeek && !it.isTest }
            .maxByOrNull { it.startTime }

        val userName = prefs.userName.first()
        val title = when {
            testTomorrow != null -> "Exam Alert: Tomorrow! 🎯"
            testIn3Days != null -> "Exam Coming Up! ⏳"
            testInWeek != null -> "Upcoming Exam Reminder 📚"
            studiedTodayCount > 0 -> "Great job, $userName! 🌟"
            else -> "Study Time, $userName! 📚"
        }
        
        val icon = when {
            testTomorrow != null -> getSubjectIcon(testTomorrow.subjectName)
            testIn3Days != null -> getSubjectIcon(testIn3Days.subjectName)
            testInWeek != null -> getSubjectIcon(testInWeek.subjectName)
            studiedTodayCount > 0 -> R.drawable.verify
            lastClassToday != null -> getSubjectIcon(lastClassToday.subjectName)
            weakTopic != null -> getSubjectIcon(weakTopic.subjectName)
            else -> R.drawable.ic_launcher_foreground
        }

        val message = when {
            testTomorrow != null -> 
                NotificationMessages.getTestReminder(testTomorrow.subjectName, testTomorrow.testType ?: "Test")
            testIn3Days != null ->
                NotificationMessages.get3DaysExamReminder(testIn3Days.subjectName, testIn3Days.testType ?: "Test")
            testInWeek != null ->
                NotificationMessages.getWeekExamReminder(testInWeek.subjectName, testInWeek.testType ?: "Test")
            studiedTodayCount > 0 -> "You've already started today. Want to do one more quick session?"
            lastClassToday != null && Math.random() > 0.5 -> 
                NotificationMessages.getAfterClassNudge(lastClassToday.subjectName)
            weakTopic != null && (weakTopic.correctAttempts.toFloat() / (weakTopic.totalAttempts.coerceAtLeast(1))) < 0.7f -> 
                NotificationMessages.getWeakTopicMessage(weakTopic.topicTitle)
            else -> NotificationMessages.getRandomNudge()
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel()

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Handle missing permission on Android 13+
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Study Reminders"
            val descriptionText = "Daily nudges and weak topic alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "study_reminders"
        const val NOTIFICATION_ID = 1001
    }
}
