package com.awra.catalogue.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.dao.DailyTime
import com.awra.catalogue.data.dao.OverallAnalytics
import com.awra.catalogue.data.dao.SubjectMastery
import com.awra.catalogue.data.dao.ChapterPerformance
import com.awra.catalogue.data.dao.TopicPerformance
import com.awra.catalogue.data.dao.SubjectTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(repository: CatalogueRepository) : ViewModel() {

    val globalQuizAnalytics: StateFlow<OverallAnalytics?> = repository.getGlobalQuizAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val timePerSubject: StateFlow<List<SubjectTime>> = repository.getTimePerSubject()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectMastery: StateFlow<List<SubjectMastery>> = repository.getSubjectMastery()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapterPerformance: StateFlow<List<ChapterPerformance>> = repository.getAllChaptersPerformance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topicPerformance: StateFlow<List<TopicPerformance>> = repository.getAllTopicsPerformance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSubject = subjectMastery.map { list ->
        list.maxByOrNull { (it.correctAttempts.toDouble() / it.totalAttempts.coerceAtLeast(1)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val worstSubject = subjectMastery.map { list ->
        list.minByOrNull { (it.correctAttempts.toDouble() / it.totalAttempts.coerceAtLeast(1)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Last 7 days
    private val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
    val dailyStudyTime: StateFlow<List<DailyTime>> = repository.getDailyStudyTime(sevenDaysAgo)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyStreak = repository.getDailyStudyTime(0) // Get all study history for streak
        .map { list ->
            if (list.isEmpty()) return@map 0
            
            val sortedDates = list.map { it.date }.sortedDescending()
            val calendar = java.util.Calendar.getInstance()
            
            // Normalize current time to start of day
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val today = calendar.timeInMillis
            
            var streak = 0
            var expectedDate = today
            
            // Check if user studied today
            if (sortedDates.first() < today) {
                // User hasn't studied today yet, check if yesterday's streak is still alive
                expectedDate -= 86400000
            }

            for (date in sortedDates) {
                if (date == expectedDate) {
                    streak++
                    expectedDate -= 86400000
                } else if (date < expectedDate) {
                    break
                }
            }
            streak
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
