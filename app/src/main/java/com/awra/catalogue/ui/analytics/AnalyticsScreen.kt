package com.awra.catalogue.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.awra.catalogue.ui.analytics.components.*
import com.awra.catalogue.ui.theme.YourAppTheme

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Performance Lab", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        if (topicPerformance.isEmpty() && dailyTime.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QueryStats, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("No study data yet.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Start studying to see your performance lab!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ... (summary stats and chart)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PerformanceSummaryCard(
                            label = "Overall Accuracy",
                            value = globalQuiz?.let { if (it.totalAttempts > 0) "${(it.correctAttempts.toFloat() / it.totalAttempts * 100).toInt()}%" else "0%" } ?: "0%",
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

                item {
                    MainPerformanceChart(dailyStudyTime = dailyTime)
                }

                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MasteryComparisonCard(
                            bestSubject = bestSubject,
                            worstSubject = worstSubject
                        )
                    }
                }

                item {
                    Text(
                        text = "Detailed Mastery",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp)
                    )
                }

                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MasteryOverview(
                            subjects = subjectMastery,
                            allTopics = topicPerformance
                        )
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
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
