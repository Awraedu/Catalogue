package com.awra.catalogue.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.RecentlyOpenedDisplayItem
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val repository: CatalogueRepository) : AndroidViewModel(application) {

    private val _selectedStream = MutableStateFlow(Stream.NATURAL)
    val selectedStream: StateFlow<Stream> = _selectedStream.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val subjects: Flow<List<Subject>> = _selectedStream.flatMapLatest { stream ->
        repository.getSubjects(stream)
    }

    val pinnedItems: Flow<List<RecentlyOpenedDisplayItem>> = repository.getPinnedItems()

    val quizzesCount: StateFlow<Int> = repository.getGlobalQuizAnalytics()
        .map { it.totalAttempts }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val flashcardsCount: StateFlow<Int> = repository.getGlobalFlashcardCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val handoutsProgress: StateFlow<Float> = repository.getGlobalHandoutProgress()
        .map { it ?: 0f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        viewModelScope.launch {
            repository.seedDatabase(getApplication())
        }
    }

    fun selectStream(stream: Stream) {
        _selectedStream.value = stream
    }
}
