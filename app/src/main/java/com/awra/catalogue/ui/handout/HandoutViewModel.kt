package com.awra.catalogue.ui.handout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ChapterWithTopics
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HandoutViewModel(
    private val repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val allChapters: Flow<List<ChapterWithTopics>> = repository.getChapters(subjectId)

    val chapters: Flow<List<ChapterWithTopics>> = combine(allChapters, _searchQuery) { chapters, query ->
        if (query.isBlank()) chapters
        else {
            chapters.mapNotNull { chapterWithTopics ->
                val filteredTopics = chapterWithTopics.topics.filter { 
                    it.topic.title.contains(query, ignoreCase = true) 
                }
                if (filteredTopics.isNotEmpty() || chapterWithTopics.chapter.title.contains(query, ignoreCase = true)) {
                    // If chapter matches but topics don't, we show the chapter but filtered topics might be empty if we want to show ALL topics of a matching chapter, but usually it's better to show matching topics.
                    // If query matches chapter title, maybe show all topics of that chapter? 
                    // Let's stick to showing matching topics, OR all topics if chapter matches.
                    val topicsToShow = if (chapterWithTopics.chapter.title.contains(query, ignoreCase = true)) {
                        chapterWithTopics.topics
                    } else {
                        filteredTopics
                    }
                    if (topicsToShow.isNotEmpty()) {
                        chapterWithTopics.copy(topics = topicsToShow)
                    } else null
                } else null
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun recordStudy(topicId: Long, timeSpentMs: Long) {
        viewModelScope.launch {
            repository.recordStudySession(topicId, MaterialType.HANDOUT, timeSpentMs)
        }
    }

    fun markRecentlyOpened(handoutId: Long) {
        viewModelScope.launch {
            repository.markAsRecentlyOpened(handoutId, MaterialType.HANDOUT)
        }
    }
}

