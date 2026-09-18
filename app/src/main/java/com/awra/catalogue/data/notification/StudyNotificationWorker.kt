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

        val type = inputData.getString("type") ?: "NUDGE"
        
        if (type == "CLASS" || type == "EXAM_START") {
            val subject = inputData.getString("subject") ?: ""
            val room = inputData.getString("room") ?: ""
            val start = inputData.getString("start") ?: ""
            val end = inputData.getString("end") ?: ""
            if (type == "EXAM_START") {
                sendExamStartNotification(subject, room, start)
            } else {
                sendClassNotification(subject, room, start, end)
            }
            return Result.success()
        }

        if (type == "DISMISSED") {
            val subject = inputData.getString("subject") ?: ""
            sendDismissedNotification(subject)
            return Result.success()
        }

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CatalogueRepository(database.catalogueDao())
        val allSchedules = repository.getAllSchedules().first()

        // Ensure daily study reminders only trigger once per day
        val lastNotifyDate = prefs.lastNotifyDate
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (lastNotifyDate != today) {
            sendNotification(prefs, repository, allSchedules)
            prefs.lastNotifyDate = today
        }

        // Reschedule for the next day
        NotificationScheduler.scheduleAllReminders(applicationContext)

        return Result.success()
    }

    private fun sendExamStartNotification(subjectName: String, room: String, start: String) {
        val start12 = formatTime12(start)
        val message = "LOCK IN! Your $subjectName exam is starting soon in Room $room (at $start12). You've got this! 🎯"
        val icon = getSubjectIcon(subjectName)
        
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle("EXAM STARTING: $subjectName")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            try { notify((NOTIFICATION_ID + subjectName.hashCode() + 2), builder.build()) } catch (e: SecurityException) {}
        }
    }

    private fun sendClassNotification(subjectName: String, room: String, start: String, end: String) {
        val start12 = formatTime12(start)
        val end12 = if (end.isNotEmpty()) formatTime12(end) else ""
        val timeRange = if (end12.isNotEmpty()) "$start12 - $end12" else start12
        
        val message = "Your $subjectName session is starting soon in Room $room ($timeRange)."
        val icon = getSubjectIcon(subjectName)
        
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle("Class Reminder: $subjectName 🏛️")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            try { notify((NOTIFICATION_ID + subjectName.hashCode()), builder.build()) } catch (e: SecurityException) {}
        }
    }

    private fun sendDismissedNotification(subjectName: String) {
        val message = NotificationMessages.getAfterClassNudge(subjectName)
        val icon = getSubjectIcon(subjectName)
        
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle("Class Dismissed: $subjectName ✅")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            try { notify((NOTIFICATION_ID + subjectName.hashCode() + 1), builder.build()) } catch (e: SecurityException) {}
        }
    }

    private fun formatTime12(time24: String): String {
        return try {
            val parts = time24.split(":")
            val hour = parts[0].toInt()
            val min = parts[1].toInt()
            val ampm = if (hour >= 12) "PM" else "AM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            String.format(Locale.US, "%d:%02d %s", hour12, min, ampm)
        } catch (e: Exception) {
            time24
        }
    }

    private suspend fun sendNotification(
        prefs: PreferenceManager,
        repository: CatalogueRepository,
        allSchedules: List<com.awra.catalogue.data.model.ClassSchedule>
    ) {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val studiedTodayCount = repository.getStudySessionsCountToday(startOfDay)
        if (studiedTodayCount > 0 && Math.random() > 0.3) return 
        
        val topicsPerformance = repository.getAllTopicsPerformance().first()
        val weakTopic = topicsPerformance
            .filter { it.totalAttempts > 0 }
            .minByOrNull { it.correctAttempts.toFloat() / it.totalAttempts }

        val calendar = Calendar.getInstance()
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

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
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
        
        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)
        val tomorrowStart = tomorrow.timeInMillis
        val tomorrowEnd = tomorrowStart + 24 * 60 * 60 * 1000

        val nowMs = System.currentTimeMillis()
        val testTomorrow = allSchedules.find { 
            it.isTest && (
                (it.date != null && it.date >= tomorrowStart && it.date < tomorrowEnd) ||
                (it.date == null && it.dayOfWeek == tomorrowDay)
            )
        }
        
        val testIn3Days = allSchedules.find {
            it.isTest && it.date != null && it.date >= (nowMs + 2*24*60*60*1000) && it.date < (nowMs + 3*24*60*60*1000)
        }
        
        val testInWeek = allSchedules.find {
            it.isTest && it.date != null && it.date >= (nowMs + 6*24*60*60*1000) && it.date < (nowMs + 7*24*60*60*1000)
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
