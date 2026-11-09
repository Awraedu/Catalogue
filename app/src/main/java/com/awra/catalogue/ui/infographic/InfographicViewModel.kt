package com.awra.catalogue.ui.infographic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ChapterWithInfographicTopics
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class InfographicViewModel(
    repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    val chapters: StateFlow<List<ChapterWithInfographicTopics>> =
        repository.getChaptersWithInfographics(subjectId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}