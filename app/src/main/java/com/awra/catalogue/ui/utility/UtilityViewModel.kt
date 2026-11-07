package com.awra.catalogue.ui.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.Utility
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class UtilityViewModel(
    repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    val utilities: StateFlow<List<Utility>> =
        repository.getUtilities(subjectId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}