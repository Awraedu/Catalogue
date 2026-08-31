package com.awra.catalogue.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.database.DatabaseSeeder
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CatalogueRepository) : ViewModel() {

    private val _selectedStream = MutableStateFlow(Stream.NATURAL)
    val selectedStream: StateFlow<Stream> = _selectedStream.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val subjects: Flow<List<Subject>> = _selectedStream.flatMapLatest { stream ->
        repository.getSubjects(stream)
    }

    init {
        viewModelScope.launch {
            repository.seedDatabase()
        }
    }

    fun selectStream(stream: Stream) {
        _selectedStream.value = stream
    }
}
