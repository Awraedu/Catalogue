package com.awra.catalogue.ui.infographic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ChapterWithInfographicTopics
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InfographicViewModel(
    private val repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    val chapters: Flow<List<ChapterWithInfographicTopics>> = repository.getChaptersWithInfographics(subjectId)

}
