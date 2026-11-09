package com.awra.catalogue.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.analytics.components.*
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import com.awra.catalogue.ui.theme.YourAppTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onBackClick: () -> Unit
) {
    val globalQuiz by viewModel.globalQuizAnalytics.collectAsState()
    val dailyTime by viewModel.dailyStudyTime.collectAsState()
    val bestSubject by viewModel.bestSubject.collectAsState()
    val worstSubject by viewModel.worstSubject.collectAsState()
    val subjectMastery by viewModel.subjectMastery.collectAsState()
    val topicPerformance by viewModel.topicPerformance.collectAsState()
    val streak by viewModel.studyStreak.collectAsState()

    // Check if the user has actually recorded any quiz/flashcard attempt or study session
    val hasStudyActivity = remember(subjectMastery, dailyTime) {
        subjectMastery.any { it.totalAttempts > 0 } || dailyTime.any { it.totalTimeMs > 0L }
    }

    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            AmbientMeshBackground(modifier = Modifier.fillMaxSize())
            Scaffold(
                containerColor = Color.Transparent
            ) { innerPadding ->
                if (!hasStudyActivity) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QueryStats,
                                null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No study data yet.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Start studying to see your performance lab!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(top = 96.dp, bottom = 140.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item(key = "summary_cards") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                PerformanceSummaryCard(
                                    label = "Overall Accuracy",
                                    value = globalQuiz?.let {
                                        if (it.totalAttempts > 0) "${(it.correctAttempts.toFloat() / it.totalAttempts * 100).toInt()}%" else "0%"
                                    } ?: "0%",
                                    icon = Icons.Default.QueryStats,
                                    modifier = Modifier.weight(1f)
                                )
                                PerformanceSummaryCard(
                                    label = "Study Streak",
                                    value = "$streak Days",
                                    icon = Icons.Default.Timeline,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item(key = "main_chart") {
                            MainPerformanceChart(dailyStudyTime = dailyTime)
                        }

                        if (bestSubject != null || worstSubject != null) {
                            item(key = "mastery_comparison") {
                                MasteryComparisonCard(
                                    bestSubject = bestSubject,
                                    worstSubject = worstSubject
                                )
                            }
                        }

                        item(key = "detailed_mastery_header") {
                            Text(
                                text = "Detailed Mastery",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        item(key = "mastery_overview") {
                            MasteryOverview(
                                subjects = subjectMastery,
                                allTopics = topicPerformance
                            )
                        }

                        item(key = "bottom_spacer") { Spacer(Modifier.height(40.dp)) }
                    }
                }
            }
        }

        // Top Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(135.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(16f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = 1.0 - smoothstep(size.y * 0.1, size.y, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        // Bottom Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(16f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = smoothstep(0.0, size.y * 0.9, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        // Floating Header Title at Top
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Performance Lab",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    YourAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MainPerformanceChart(dailyStudyTime = emptyList())
        }
    }
}
