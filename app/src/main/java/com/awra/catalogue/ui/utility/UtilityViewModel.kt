package com.awra.catalogue.ui.utility

import androidx.lifecycle.ViewModel
import com.awra.catalogue.data.model.Utility
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.model.MaterialType
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UtilityViewModel(
    private val repository: CatalogueRepository,
    val subjectId: Long
) : ViewModel() {

    val utilities: Flow<List<Utility>> = repository.getUtilities(subjectId)

}

