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

class InsightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = InsightWidget()
}

class InsightWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).catalogueDao()
        val terms = try { dao.getFeaturedTerms(50).first() } catch (_: Exception) { emptyList() }
        val subjects = try { dao.getAllSubjects().first() } catch (_: Exception) { emptyList() }

        val calendar = Calendar.getInstance()
        val seed = calendar.get(Calendar.DAY_OF_YEAR) + 1
        val term = if (terms.isNotEmpty()) terms[seed % terms.size] else null

        val word = term?.word ?: "Daily Insight"
        val definition = term?.definition ?: "Study more to unlock daily insights and academic terms."

        // Default to subjectId 3L (errorContainer) matching DailyInsightCard on home screen
        val subjectId = if (term != null && subjects.isNotEmpty()) {
            subjects.firstOrNull { term.definition.contains(it.name, ignoreCase = true) || term.word.contains(it.name, ignoreCase = true) }?.id ?: 3L
        } else {
            3L
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
                val bodyTextColor = ColorProvider(
                    day = rawContentColor.copy(alpha = 0.8f),
                    night = rawContentColor.copy(alpha = 0.8f)
                )
                val footerTextColor = ColorProvider(
                    day = rawContentColor.copy(alpha = 0.65f),
                    night = rawContentColor.copy(alpha = 0.65f)
                )

                // Outer Box fills space and centers the card so it never distorts on wide/tall cells
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
                                        provider = ImageProvider(R.drawable.ic_lightbulb),
                                        contentDescription = null,
                                        modifier = GlanceModifier.size(18.dp),
                                        colorFilter = ColorFilter.tint(contentColor)
                                    )
                                }
                                Spacer(GlanceModifier.width(8.dp))
                                Text(
                                    text = "Insight",
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
                                    text = word,
                                    style = TextStyle(
                                        color = contentColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )
                                Spacer(GlanceModifier.height(4.dp))
                                Text(
                                    text = definition,
                                    style = TextStyle(
                                        color = bodyTextColor,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 3
                                )
                            }

                            Spacer(GlanceModifier.defaultWeight())

                            // Footer Text
                            Text(
                                text = "Did you know?",
                                style = TextStyle(
                                    color = footerTextColor,
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
