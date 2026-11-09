package com.awra.catalogue.ui.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.awra.catalogue.MainActivity
import com.awra.catalogue.R
import com.awra.catalogue.data.database.AppDatabase
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Locale

class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()
}

class ScheduleWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).catalogueDao()
        val schedules = try { dao.getAllSchedules().first() } catch (_: Exception) { emptyList() }
        val subjects = try { dao.getAllSubjects().first() } catch (_: Exception) { emptyList() }

        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = if (currentDay == Calendar.SUNDAY) 7 else currentDay - 1
        val currentTimeStr = String.format(
            Locale.US,
            "%02d:%02d",
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE)
        )

        val nextClass = schedules
            .filter { !it.isTest && it.dayOfWeek == dayOfWeek && it.startTime > currentTimeStr }
            .minByOrNull { it.startTime }

        val subjectName = nextClass?.subjectName ?: "No classes"
        val roomText = if (nextClass != null) "Room ${nextClass.room}" else "Free time!"
        val timeRemainingMins = if (nextClass != null) calculateDiffMins(currentTimeStr, nextClass.startTime) else 0L
        val timeBadgeText = if (nextClass != null) "In ${timeRemainingMins}m" else "All Done"

        // Default to subjectId 2L (tertiaryContainer) matching NextClassCard on home screen
        val subjectId = if (nextClass != null) {
            subjects.firstOrNull { it.name.equals(nextClass.subjectName, ignoreCase = true) }?.id ?: 2L
        } else {
            2L
        }

        provideContent {
            GlanceTheme {
                val subjectColors = getGlanceSubjectColors(subjectId)
                val containerColor = subjectColors.container
                val contentColor = subjectColors.content
                val rawContentColor = contentColor.getColor(context)
                val iconBgColor = ColorProvider(
                    day = rawContentColor.copy(alpha = 0.2f),
                    night = rawContentColor.copy(alpha = 0.2f)
                )
                val roomColor = ColorProvider(
                    day = rawContentColor.copy(alpha = 0.65f),
                    night = rawContentColor.copy(alpha = 0.65f)
                )

                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .cornerRadius(32.dp)
                            .background(containerColor)
                            .padding(18.dp)
                            .clickable(actionStartActivity<MainActivity>()),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column(
                            modifier = GlanceModifier.fillMaxSize()
                        ) {
                            // Header Row
                            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                                Box(
                                    modifier = GlanceModifier
                                        .size(38.dp)
                                        .cornerRadius(12.dp)
                                        .background(iconBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        provider = ImageProvider(R.drawable.schedule),
                                        contentDescription = null,
                                        modifier = GlanceModifier.size(18.dp),
                                        colorFilter = ColorFilter.tint(contentColor)
                                    )
                                }
                                Spacer(GlanceModifier.width(8.dp))
                                Text(
                                    text = "Schedule",
                                    style = TextStyle(
                                        color = contentColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(GlanceModifier.defaultWeight())

                            // Middle Body
                            Column {
                                Text(
                                    text = subjectName,
                                    style = TextStyle(
                                        color = contentColor,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 2
                                )
                                Spacer(GlanceModifier.height(2.dp))
                                Text(
                                    text = roomText,
                                    style = TextStyle(
                                        color = roomColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    maxLines = 1
                                )
                            }

                            Spacer(GlanceModifier.defaultWeight())

                            // Bottom Pill Badge
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Horizontal.End
                            ) {
                                Box(
                                    modifier = GlanceModifier
                                        .cornerRadius(100.dp)
                                        .background(contentColor)
                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = timeBadgeText,
                                        style = TextStyle(
                                            color = containerColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateDiffMins(current: String, start: String): Long {
        return try {
            val c = current.split(":")
            val s = start.split(":")
            val cMin = c[0].toInt() * 60 + c[1].toInt()
            val sMin = s[0].toInt() * 60 + s[1].toInt()
            (sMin - cMin).toLong()
        } catch (_: Exception) {
            0L
        }
    }
}
