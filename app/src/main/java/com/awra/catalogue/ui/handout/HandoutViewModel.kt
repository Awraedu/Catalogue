package com.awra.catalogue.ui.handout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ChapterWithTopics
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HandoutViewModel(
    private val repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    val chapters: Flow<List<ChapterWithTopics>> = repository.getChapters(subjectId)

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

