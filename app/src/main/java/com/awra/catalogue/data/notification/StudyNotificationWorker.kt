package com.awra.catalogue.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.awra.catalogue.MainActivity
import com.awra.catalogue.R
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.model.ClassSchedule
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.util.PreferenceManager
import com.awra.catalogue.ui.theme.getSubjectIcon
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class StudyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = PreferenceManager(applicationContext)
        val remindersEnabled = prefs.studyReminders.first()
        if (!remindersEnabled) return Result.success()

        createNotificationChannels()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CatalogueRepository(database.catalogueDao())
        val allSchedules = repository.getAllSchedules().first()

        val type = inputData.getString("type") ?: "NUDGE"

        // =====================================================================
        // 1. HANDLE CLASS & EXAM ALERTS (+ Self-Reschedule for Next Week)
        // =====================================================================
        if (type == "CLASS" || type == "EXAM_START") {
            val subject = inputData.getString("subject") ?: ""
            val room = inputData.getString("room") ?: ""
            val start = inputData.getString("start") ?: ""
            val end = inputData.getString("end") ?: ""
            val leadMins = inputData.getInt("leadMins", 5)

            if (type == "EXAM_START") {
                sendExamStartNotification(subject, room, start, leadMins)
            } else {
                sendClassNotification(subject, room, start, end, leadMins)
            }

            // Re-schedule upcoming classes so weekly classes repeat next week!
            NotificationScheduler.scheduleClassAndExamReminders(applicationContext, allSchedules)
            return Result.success()
        }

        if (type == "DISMISSED") {
            val subject = inputData.getString("subject") ?: ""
            sendDismissedNotification(subject)
            NotificationScheduler.scheduleClassAndExamReminders(applicationContext, allSchedules)
            return Result.success()
        }

        // =====================================================================
        // 2. HANDLE EVENING "STREAK SAVER" ALERT (Duolingo 8:30 PM Rescue)
        // =====================================================================
        if (type == "STREAK_SAVER") {
            val startOfDay = getStartOfDayMillis()
            val studiedTodayCount = repository.getStudySessionsCountToday(startOfDay)
            val streak = prefs.currentStreak
            val userName = prefs.userName.first()

            if (studiedTodayCount == 0 && streak >= 2) {
                sendStreakSaverNotification(userName, streak)
            }
            return Result.success()
        }

        // =====================================================================
        // 3. HANDLE SMART DAILY STUDY NUDGE
        // =====================================================================

        // A. In-Class Suppression: Don't buzz the student with a random study nudge
        // if they are literally sitting in a scheduled class right now!
        if (isUserCurrentlyInClass(allSchedules)) {
            snoozeNudgeByMinutes(45)
            return Result.success()
        }

        val todayMidnight = getStartOfDayMillis()

        // Ensure daily study reminders only trigger once per day
        if (prefs.lastNotifyDate != todayMidnight) {
            val didSend = evaluateAndSendSmartNudge(prefs, repository, allSchedules, todayMidnight)
            if (didSend) {
                prefs.lastNotifyDate = todayMidnight
            }
        }

        // Reschedule tomorrow's daily nudge
        NotificationScheduler.scheduleDailyNudge(applicationContext, prefs)

        return Result.success()
    }

    /**
     * Checks if the current day & time falls inside any active class schedule.
     */
    private fun isUserCurrentlyInClass(schedules: List<ClassSchedule>): Boolean {
        val now = Calendar.getInstance()
        val currentDay = mapCalendarDayToAppDay(now.get(Calendar.DAY_OF_WEEK))
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        return schedules.any { schedule ->
            if (schedule.dayOfWeek != currentDay) return@any false
            val startMins = parseToMinutesOfDay(schedule.startTime) ?: return@any false
            val endMins = schedule.endTime?.let { parseToMinutesOfDay(it) } ?: (startMins + 60)
            currentMinutes in startMins..endMins
        }
    }

    private fun snoozeNudgeByMinutes(minutes: Long) {
        val workRequest = OneTimeWorkRequestBuilder<StudyNotificationWorker>()
            .setInitialDelay(minutes, TimeUnit.MINUTES)
            .setInputData(workDataOf("type" to "NUDGE"))
            .addTag(NotificationScheduler.NUDGE_WORK_NAME)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            NotificationScheduler.NUDGE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    // =========================================================================
    // 🧠 THE DUOLINGO SMART DECISION ENGINE
    // =========================================================================
    private suspend fun evaluateAndSendSmartNudge(
        prefs: PreferenceManager,
        repository: CatalogueRepository,
        allSchedules: List<ClassSchedule>,
        startOfDay: Long
    ): Boolean {
        val userName = prefs.userName.first()
        val goalMinutes = prefs.studyGoal.first().coerceAtLeast(5)
        val streak = prefs.currentStreak
        val ignoredCount = prefs.consecutiveIgnoredNudges

        // 1. Check today's study sessions & time spent
        val studiedTodayCount = repository.getStudySessionsCountToday(startOfDay)
        val dailyTimes = repository.getDailyStudyTime(startOfDay - 1000L).first()
        val minutesStudiedToday = ((dailyTimes.lastOrNull()?.totalTimeMs ?: 0L) / 60000f).roundToInt()

        // 🛑 SMART SUPPRESSION: If they already met their daily minute goal, don't nag them!
        if (minutesStudiedToday >= goalMinutes) {
            prefs.consecutiveIgnoredNudges = 0
            return false
        }

        // 🛑 ANTI-SPAM FATIGUE BACKOFF (Duolingo Rule):
        // If user ignored 4+ nudges in a row, stop daily nagging. Only send a gentle check-in
        // on Day 5 and Day 9, then stay quiet until they open the app again.
        if (ignoredCount >= 4 && ignoredCount != 5 && ignoredCount != 9) {
            prefs.consecutiveIgnoredNudges = ignoredCount + 1
            return false
        }

        // 2. Check upcoming exams (Tomorrow, 3 Days, 7 Days)
        val nowMs = System.currentTimeMillis()
        val tomorrowCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val tomorrowDay = mapCalendarDayToAppDay(tomorrowCal.get(Calendar.DAY_OF_WEEK))
        val tomorrowStart = tomorrowCal.timeInMillis
        val tomorrowEnd = tomorrowStart + 24 * 60 * 60 * 1000L

        val testTomorrow = allSchedules.find {
            it.isTest && (
                    (it.date != null && it.date in tomorrowStart until tomorrowEnd) ||
                            (it.date == null && it.dayOfWeek == tomorrowDay)
                    )
        }
        val testIn3Days = allSchedules.find {
            it.isTest && it.date != null && it.date in (nowMs + 2 * 86400000L) until (nowMs + 3 * 86400000L)
        }
        val testInWeek = allSchedules.find {
            it.isTest && it.date != null && it.date in (nowMs + 6 * 86400000L) until (nowMs + 7 * 86400000L)
        }

        // 3. Check Weakest Topic & Last Class Today
        val topicsPerformance = repository.getAllTopicsPerformance().first()
        val weakTopic = topicsPerformance
            .filter { it.totalAttempts >= 3 }
            .minByOrNull { it.correctAttempts.toFloat() / it.totalAttempts }

        val todayDayOfWeek = mapCalendarDayToAppDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
        val lastClassToday = allSchedules
            .filter { it.dayOfWeek == todayDayOfWeek && !it.isTest }
            .maxByOrNull { it.startTime }

        // 4. Select the Smartest Persona, Title, Message, and Icon
        val remainingMins = (goalMinutes - minutesStudiedToday).coerceAtLeast(1)

        val title: String
        val message: String
        val icon: Int

        when {
            // PRIORITY 1: Exam Tomorrow (Urgent Coach Persona)
            testTomorrow != null -> {
                title = "Exam Tomorrow: ${testTomorrow.subjectName}! 🎯"
                message = NotificationMessages.getTestReminder(
                    testTomorrow.subjectName,
                    testTomorrow.testType ?: "Exam"
                )
                icon = getSubjectIcon(testTomorrow.subjectName)
            }

            // PRIORITY 2: Almost Hit Daily Goal Today (Encouraging Finisher Persona)
            studiedTodayCount > 0 && minutesStudiedToday in 1 until goalMinutes -> {
                title = "So close to your daily goal, $userName! 🌟"
                message = "You've already done $minutesStudiedToday mins today. Just $remainingMins more minutes to crush your ${goalMinutes}-min study goal!"
                icon = R.drawable.verify
            }

            // PRIORITY 3: Already Studied Today (Low-probability bonus challenge)
            studiedTodayCount > 0 -> {
                // Only 25% chance to nudge if they already studied a session
                if (Math.random() > 0.25) return false
                title = "On a roll, $userName! 🔥"
                message = if (weakTopic != null) {
                    "You're crushing it today! Up for a quick 5-question bonus round on ${weakTopic.topicTitle}?"
                } else {
                    "You've already started today. Want to do one quick flashcard review to lock it in?"
                }
                icon = R.drawable.verify
            }

            // PRIORITY 4: Exam in 3 Days
            testIn3Days != null -> {
                title = "3 Days Until ${testIn3Days.subjectName} ⏳"
                message = NotificationMessages.get3DaysExamReminder(
                    testIn3Days.subjectName,
                    testIn3Days.testType ?: "Exam"
                )
                icon = getSubjectIcon(testIn3Days.subjectName)
            }

            // PRIORITY 5: Anti-Spam Hibernation Warning (5 or 9 days ignored)
            ignoredCount >= 5 -> {
                title = "We'll quiet down for now, $userName 🦉"
                message = "Looks like life is busy! We're pausing daily study reminders so we don't spam you. Tap anytime to jump back in."
                icon = R.drawable.ic_launcher_foreground
            }

            // PRIORITY 6: Active Streak Protection (Duolingo Streak Persona)
            streak >= 3 -> {
                title = "Keep your $streak-day streak alive! 🔥"
                message = if (weakTopic != null) {
                    "Don't let your $streak-day streak cool down! Spend $goalMinutes mins reviewing ${weakTopic.topicTitle} (${weakTopic.subjectName})."
                } else {
                    "You're on a $streak-day study streak, $userName! Take $goalMinutes minutes now to keep the fire burning."
                }
                icon = weakTopic?.let { getSubjectIcon(it.subjectName) } ?: R.drawable.ic_launcher_foreground
                // Also schedule the 8:30 PM Streak Saver just in case they miss this afternoon nudge!
                NotificationScheduler.scheduleEveningStreakSaver(applicationContext)
            }

            // PRIORITY 7: Exam in a Week
            testInWeek != null -> {
                title = "1 Week Out: ${testInWeek.subjectName} 📚"
                message = NotificationMessages.getWeekExamReminder(
                    testInWeek.subjectName,
                    testInWeek.testType ?: "Exam"
                )
                icon = getSubjectIcon(testInWeek.subjectName)
            }

            // PRIORITY 8: Weak Topic Mastery Nudge (< 70% accuracy)
            weakTopic != null && (weakTopic.correctAttempts.toFloat() / weakTopic.totalAttempts) < 0.7f -> {
                val accuracy = ((weakTopic.correctAttempts.toFloat() / weakTopic.totalAttempts) * 100).roundToInt()
                title = "Boost your ${weakTopic.subjectName} score 📈"
                message = "${NotificationMessages.getWeakTopicMessage(weakTopic.topicTitle)} (Current mastery: $accuracy%)"
                icon = getSubjectIcon(weakTopic.subjectName)
            }

            // PRIORITY 9: Post-Class Evening Recap
            lastClassToday != null && Math.random() > 0.4 -> {
                title = "Recap today's ${lastClassToday.subjectName} class 🧠"
                message = NotificationMessages.getAfterClassNudge(lastClassToday.subjectName)
                icon = getSubjectIcon(lastClassToday.subjectName)
            }

            // PRIORITY 10: Playful / Ignored 2-3 Days Nudge
            ignoredCount in 2..3 -> {
                title = "Your notes miss you, $userName 👀"
                message = "Got 5 minutes? A quick flashcard session today is 10x better than cramming next week!"
                icon = R.drawable.ic_launcher_foreground
            }

            // DEFAULT: Standard Friendly Nudge
            else -> {
                title = "Study Time, $userName! 📚"
                message = NotificationMessages.getRandomNudge()
                icon = R.drawable.ic_launcher_foreground
            }
        }

        // Increment ignored counter (will be reset to 0 as soon as they open the app and call prefs.recordUserActivity())
        prefs.consecutiveIgnoredNudges = ignoredCount + 1

        val pendingIntent = createMainActivityPendingIntent(
            requestCode = NOTIFICATION_ID,
            extraAction = "OPEN_STUDY"
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_NUDGE_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .addAction(0, "📚 Start ${goalMinutes}-Min Session", pendingIntent)
            .setAutoCancel(true)

        notifySafely(NOTIFICATION_ID, builder)
        return true
    }

    // =========================================================================
    // 🔔 SPECIFIC NOTIFICATION BUILDERS
    // =========================================================================

    private fun sendStreakSaverNotification(userName: String, streak: Int) {
        val pendingIntent = createMainActivityPendingIntent(
            requestCode = NOTIFICATION_ID + 99,
            extraAction = "STREAK_SAVER"
        )
        val message = "Warning $userName! Your $streak-day study streak expires at midnight. Do one quick 3-minute quiz right now to save it! ⏳🔥"

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_URGENT_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ $streak-Day Streak in Danger!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(0, "🔥 Save My Streak", pendingIntent)
            .setAutoCancel(true)

        notifySafely(NOTIFICATION_ID + 99, builder)
    }

    private fun sendExamStartNotification(subjectName: String, room: String, start: String, leadMins: Int) {
        val start12 = formatTime12(start)
        val roomText = if (room.isNotBlank()) " in Room $room" else ""
        val message = "LOCK IN! Your $subjectName exam starts in $leadMins mins$roomText (at $start12). Deep breath—you've got this! 🎯"
        val icon = getSubjectIcon(subjectName)
        val notificationId = NOTIFICATION_ID + subjectName.hashCode() + 2
        val pendingIntent = createMainActivityPendingIntent(notificationId, "EXAM_START")

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_URGENT_ID)
            .setSmallIcon(icon)
            .setContentTitle("🚨 EXAM IN $leadMins MINS: $subjectName")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notifySafely(notificationId, builder)
    }

    private fun sendClassNotification(subjectName: String, room: String, start: String, end: String, leadMins: Int) {
        val start12 = formatTime12(start)
        val end12 = if (end.isNotEmpty()) formatTime12(end) else ""
        val timeRange = if (end12.isNotEmpty()) "$start12 - $end12" else start12
        val roomText = if (room.isNotBlank()) " in Room $room" else ""

        val message = "Your $subjectName class starts in $leadMins mins$roomText ($timeRange)."
        val icon = getSubjectIcon(subjectName)
        val notificationId = NOTIFICATION_ID + subjectName.hashCode()
        val pendingIntent = createMainActivityPendingIntent(notificationId, "CLASS_START")

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_URGENT_ID)
            .setSmallIcon(icon)
            .setContentTitle("🏛️ Class Starting: $subjectName")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notifySafely(notificationId, builder)
    }

    private fun sendDismissedNotification(subjectName: String) {
        val message = NotificationMessages.getAfterClassNudge(subjectName)
        val icon = getSubjectIcon(subjectName)
        val notificationId = NOTIFICATION_ID + subjectName.hashCode() + 1
        val pendingIntent = createMainActivityPendingIntent(notificationId, "POST_CLASS_REVIEW")

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_NUDGE_ID)
            .setSmallIcon(icon)
            .setContentTitle("Class Dismissed: $subjectName ✅")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(0, "⚡ Quick Recap", pendingIntent)
            .setAutoCancel(true)

        notifySafely(notificationId, builder)
    }

    // =========================================================================
    // 🛠️ HELPERS & CHANNELS
    // =========================================================================

    private fun createMainActivityPendingIntent(requestCode: Int, extraAction: String): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_action", extraAction)
        }
        return PendingIntent.getActivity(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun notifySafely(id: Int, builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(applicationContext)) {
            try {
                notify(id, builder.build())
            } catch (e: SecurityException) {
                // Missing POST_NOTIFICATIONS permission on Android 13+
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Daily Study Nudges Channel
            val nudgeChannel = NotificationChannel(
                CHANNEL_NUDGE_ID,
                "Smart Study Nudges",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily study goal reminders, weak topic insights, and post-class recaps"
            }

            // 2. Urgent Class & Exam Alarms Channel
            val urgentChannel = NotificationChannel(
                CHANNEL_URGENT_ID,
                "Class & Exam Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Time-sensitive alerts before classes and exams start, plus streak savers"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(nudgeChannel)
            notificationManager.createNotificationChannel(urgentChannel)
        }
    }

    private fun getStartOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun mapCalendarDayToAppDay(calDay: Int): Int {
        return when (calDay) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    private fun parseToMinutesOfDay(time24: String): Int? {
        return try {
            val parts = time24.trim().split(" ")[0].split(":")
            parts[0].toInt() * 60 + parts[1].toInt()
        } catch (e: Exception) {
            null
        }
    }

    private fun formatTime12(time24: String): String {
        return try {
            val parts = time24.trim().split(" ")[0].split(":")
            val hour = parts[0].toInt()
            val min = parts[1].toInt()
            val ampm = if (hour >= 12) "PM" else "AM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            String.format(Locale.US, "%d:%02d %s", hour12, min, ampm)
        } catch (e: Exception) {
            time24
        }
    }

    companion object {
        const val CHANNEL_NUDGE_ID = "study_reminders"
        const val CHANNEL_URGENT_ID = "class_exam_alerts"
        const val NOTIFICATION_ID = 1001
    }
}