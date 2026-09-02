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
}
