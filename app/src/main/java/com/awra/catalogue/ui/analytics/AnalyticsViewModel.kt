package com.awra.catalogue.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.dao.ChapterPerformance
import com.awra.catalogue.data.dao.DailyTime
import com.awra.catalogue.data.dao.OverallAnalytics
import com.awra.catalogue.data.dao.SubjectMastery
import com.awra.catalogue.data.dao.SubjectTime
import com.awra.catalogue.data.dao.TopicPerformance
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class AnalyticsViewModel(repository: CatalogueRepository) : ViewModel() {

    val globalQuizAnalytics: StateFlow<OverallAnalytics?> = repository.getGlobalQuizAnalytics()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val timePerSubject: StateFlow<List<SubjectTime>> = repository.getTimePerSubject()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val subjectMastery: StateFlow<List<SubjectMastery>> = repository.getSubjectMastery()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val chapterPerformance: StateFlow<List<ChapterPerformance>> = repository.getAllChaptersPerformance()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val topicPerformance: StateFlow<List<TopicPerformance>> = repository.getAllTopicsPerformance()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * FIXED: Only considers subjects the user has actually attempted (totalAttempts > 0).
     * Uses Bayesian Laplace smoothing ((correct + 1) / (total + 2)) to break ties fairly
     * so a subject with 20/20 ranks higher than a lucky 1/1.
     */
    val bestSubject: StateFlow<SubjectMastery?> = subjectMastery
        .map { list ->
            list.filter { it.totalAttempts > 0 }
                .maxWithOrNull(
                    compareBy<SubjectMastery> {
                        it.correctAttempts.toDouble() / it.totalAttempts
                    }.thenBy {
                        (it.correctAttempts + 1.0) / (it.totalAttempts + 2.0)
                    }
                )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     * FIXED: Only considers attempted subjects (totalAttempts > 0) and excludes bestSubject
     * if only 1 subject has been attempted so far.
     */
    val worstSubject: StateFlow<SubjectMastery?> = subjectMastery
        .map { list ->
            val attempted = list.filter { it.totalAttempts > 0 }
            if (attempted.size <= 1) {
                // If they only practiced 1 subject, only show it as worst if accuracy < 60%
                attempted.firstOrNull()?.takeIf {
                    (it.correctAttempts.toDouble() / it.totalAttempts) < 0.6
                }
            } else {
                attempted.minWithOrNull(
                    compareBy<SubjectMastery> {
                        it.correctAttempts.toDouble() / it.totalAttempts
                    }.thenBy {
                        -it.totalAttempts // If tied in low accuracy, prioritize the one with more attempts
                    }
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Last 7 days normalized to local midnight, with missing days pre-filled with 0ms
    private val sevenDaysAgoMidnight: Long = getLocalDayMidnightMillis(daysOffset = -6)

    val dailyStudyTime: StateFlow<List<DailyTime>> = repository.getDailyStudyTime(sevenDaysAgoMidnight)
        .map { rawList ->
            // Map raw DB timestamps to local midnight keys so timezone differences never shift days
            val timeByDay = mutableMapOf<Long, Long>()
            rawList.forEach { item ->
                val dayKey = normalizeToLocalMidnight(item.date)
                timeByDay[dayKey] = (timeByDay[dayKey] ?: 0L) + item.totalTimeMs
            }

            // Always emit all 7 consecutive days (oldest -> today) for a rock-solid chart
            (6 downTo 0).map { offset ->
                val dayStart = getLocalDayMidnightMillis(daysOffset = -offset)
                DailyTime(
                    date = dayStart,
                    totalTimeMs = timeByDay[dayStart] ?: 0L
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * FIXED: Normalizes every study date to local midnight before comparing,
     * fixing the UTC vs Local Timezone bug that kept studyStreak at 0.
     */
    val studyStreak: StateFlow<Int> = repository.getDailyStudyTime(0)
        .map { list ->
            if (list.isEmpty()) return@map 0

            val uniqueLocalDays = list
                .filter { it.totalTimeMs > 0L }
                .map { normalizeToLocalMidnight(it.date) }
                .distinct()
                .sortedDescending()

            if (uniqueLocalDays.isEmpty()) return@map 0

            val todayMidnight = getLocalDayMidnightMillis(0)
            val yesterdayMidnight = getLocalDayMidnightMillis(-1)

            // If user hasn't studied today OR yesterday, streak is 0
            val mostRecentStudyDay = uniqueLocalDays.first()
            if (mostRecentStudyDay < yesterdayMidnight) return@map 0

            var streak = 0
            var expectedDayCursor = if (mostRecentStudyDay == todayMidnight) 0 else -1

            for (dayMillis in uniqueLocalDays) {
                val expectedMillis = getLocalDayMidnightMillis(expectedDayCursor)
                if (dayMillis == expectedMillis) {
                    streak++
                    expectedDayCursor--
                } else if (dayMillis < expectedMillis) {
                    break
                }
            }
            streak
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private fun getLocalDayMidnightMillis(daysOffset: Int): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysOffset)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun normalizeToLocalMidnight(timestampMs: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestampMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}