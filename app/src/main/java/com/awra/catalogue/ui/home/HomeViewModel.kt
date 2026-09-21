package com.awra.catalogue.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.RecentlyOpenedDisplayItem
import com.awra.catalogue.data.model.FeaturedItem
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(application: Application, private val repository: CatalogueRepository, private val preferenceManager: PreferenceManager) : AndroidViewModel(application) {

    private val _selectedStream = MutableStateFlow(
        try { Stream.valueOf(preferenceManager.selectedStream.value) } 
        catch (e: Exception) { Stream.ALL }
    )
    val selectedStream: StateFlow<Stream> = _selectedStream.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val subjects: Flow<List<Subject>> = _selectedStream.flatMapLatest { stream ->
        repository.getSubjects(stream)
    }

    val pinnedItems: StateFlow<List<RecentlyOpenedDisplayItem>> = repository.getPinnedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val featuredItems: StateFlow<List<FeaturedItem>> = repository.getStudiedTopics().flatMapLatest { studiedTopics ->
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 1. Daily Quiz logic: Most recently studied subject, topics already read
        val quizFlow = if (studiedTopics.isNotEmpty()) {
            val latestSubjectName = studiedTopics.first().subjectName
            val topicsInSameSubject = studiedTopics.filter { it.subjectName == latestSubjectName }
            val topicIds = topicsInSameSubject.map { it.id }
            
            // Check if user did any quiz on these topics today
            combine(
                repository.getTotalQuizAttemptsForTopicsToday(topicIds, startOfDay),
                repository.getTotalCorrectQuizAttemptsForTopicsToday(topicIds, startOfDay)
            ) { count, correct ->
                FeaturedItem.DailyQuiz(
                    topicId = topicIds.first(),
                    topicTitle = latestSubjectName,
                    subjectName = latestSubjectName,
                    isCompleted = count >= 5, // Mark as completed if they did at least 5 questions today
                    lastScore = correct
                )
            }
        } else {
            flowOf(FeaturedItem.EmptyDailyQuiz)
        }

        combine(
            quizFlow,
            repository.searchAllTerms(""),
            repository.getAllSchedules()
        ) { quiz, terms, schedules ->
            val featured = mutableListOf<FeaturedItem>()
            
            // 1. Daily Quiz
            featured.add(quiz)

            // 2. Daily Term
            if (terms.isNotEmpty()) {
                val calendar = Calendar.getInstance()
                val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
                val term = terms[dayOfYear % terms.size]
                featured.add(
                    FeaturedItem.DailyTerm(
                        termId = term.id,
                        word = term.word,
                        definition = term.definition,
                        subjectName = ""
                    )
                )
            } else {
                featured.add(FeaturedItem.EmptyDailyTerm)
            }

            // 3. Next Class
            val now = Calendar.getInstance()
            val currentDay = now.get(Calendar.DAY_OF_WEEK)
            val dayOfWeek = if (currentDay == 1) 7 else currentDay - 1
            val currentTimeStr = String.format(Locale.US, "%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))

            val nextClass = schedules.filter { it.dayOfWeek == dayOfWeek && it.startTime > currentTimeStr }
                .minByOrNull { it.startTime }

            if (nextClass != null) {
                featured.add(
                    FeaturedItem.NextClass(
                        subjectName = nextClass.subjectName,
                        startTime = nextClass.startTime,
                        room = nextClass.room,
                        timeRemainingMins = calculateDiffMins(currentTimeStr, nextClass.startTime)
                    )
                )
            } else {
                featured.add(FeaturedItem.EmptyNextClass)
            }

            // 4. Daily Insight
            if (terms.size > 1) {
                val calendar = Calendar.getInstance()
                val seed = calendar.get(Calendar.DAY_OF_YEAR) + 1
                val term = terms[seed % terms.size]
                featured.add(
                    FeaturedItem.DailyInsight(
                        word = term.word,
                        definition = term.definition,
                        subjectName = "" 
                    )
                )
            }

            featured
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(
        FeaturedItem.EmptyDailyQuiz,
        FeaturedItem.EmptyDailyTerm,
        FeaturedItem.EmptyNextClass
    ))

    private fun calculateDiffMins(current: String, start: String): Long {
        return try {
            val c = current.split(":")
            val s = start.split(":")
            val cMin = c[0].toInt() * 60 + c[1].toInt()
            val sMin = s[0].toInt() * 60 + s[1].toInt()
            (sMin - cMin).toLong()
        } catch (e: Exception) { 0 }
    }

    val quizzesCount: StateFlow<Int> = repository.getGlobalQuizAnalytics()
        .map { it.totalAttempts }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val flashcardsCount: StateFlow<Int> = repository.getGlobalFlashcardCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val handoutsProgress: StateFlow<Float> = repository.getGlobalHandoutProgress()
        .map { it ?: 0f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val daysActive: StateFlow<Int> = repository.getUniqueStudyDaysCount()
        .map { maxOf(1, it) } // Minimum 1 day
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    init {
        viewModelScope.launch {
            repository.seedDatabase(getApplication())
        }
    }

    fun selectStream(stream: Stream) {
        _selectedStream.value = stream
    }
}
