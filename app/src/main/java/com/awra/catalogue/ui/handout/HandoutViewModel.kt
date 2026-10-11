package com.awra.catalogue.ui.handout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ChapterWithTopics
import com.awra.catalogue.data.model.Handout
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HandoutDownloadState {
    object NotDownloaded : HandoutDownloadState
    data class Downloading(val progress: Float) : HandoutDownloadState
    data class Downloaded(val content: String) : HandoutDownloadState
    data class Error(val message: String) : HandoutDownloadState
}

class HandoutViewModel(
    private val repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _downloadStates = MutableStateFlow<Map<Long, HandoutDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<Long, HandoutDownloadState>> = _downloadStates.asStateFlow()

    private val allChapters: Flow<List<ChapterWithTopics>> = repository.getChapters(subjectId)

    val chapters: Flow<List<ChapterWithTopics>> = combine(allChapters, _searchQuery) { chapters, query ->
        if (query.isBlank()) chapters
        else {
            chapters.mapNotNull { chapterWithTopics ->
                val filteredTopics = chapterWithTopics.topics.filter { 
                    it.topic.title.contains(query, ignoreCase = true) 
                }
                if (filteredTopics.isNotEmpty() || chapterWithTopics.chapter.title.contains(query, ignoreCase = true)) {
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

    fun isHandoutDownloaded(context: Context, handout: Handout): Boolean {
        return repository.isHandoutDownloaded(context, handout)
    }

    fun getHandoutContent(context: Context, handout: Handout): String? {
        return repository.getEncryptedHandoutContent(context, handout)
    }

    fun downloadHandout(context: Context, handout: Handout) {
        viewModelScope.launch {
            _downloadStates.update { it + (handout.id to HandoutDownloadState.Downloading(0f)) }
            val result = repository.downloadAndEncryptHandout(
                context = context,
                handout = handout,
                onProgress = { progress ->
                    _downloadStates.update { current ->
                        current + (handout.id to HandoutDownloadState.Downloading(progress))
                    }
                }
            )
            result.onSuccess { content ->
                _downloadStates.update { it + (handout.id to HandoutDownloadState.Downloaded(content)) }
            }.onFailure { error ->
                _downloadStates.update { 
                    it + (handout.id to HandoutDownloadState.Error(error.localizedMessage ?: "Download failed")) 
                }
            }
        }
    }

    fun deleteHandout(context: Context, handout: Handout) {
        repository.deleteDownloadedHandout(context, handout)
        _downloadStates.update { it - handout.id }
    }

    fun recordStudy(topicId: Long, timeSpentMs: Long) {
        viewModelScope.launch {
            repository.recordStudySession(topicId, MaterialType.HANDOUT, timeSpentMs)
        }
    }
}

