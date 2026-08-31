package com.awra.catalogue.ui.handout

import androidx.lifecycle.ViewModel
import com.awra.catalogue.data.model.ChapterWithTopics
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.Flow

class HandoutViewModel(
    private val repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    val chapters: Flow<List<ChapterWithTopics>> = repository.getChapters(subjectId)
}
