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

    val recentlyOpened: Flow<List<RecentlyOpenedDisplayItem>> = repository.getRecentlyOpened()

    private val subjectCache = mutableMapOf<Long, Subject>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentSubjects: StateFlow<List<Subject>> = recentlyOpened
        .mapLatest { items ->
            items.map { it.subjectId }
                .distinct()
                .take(5)
                .mapNotNull { id ->
                    subjectCache[id] ?: repository.getSubjectById(id)?.also { subjectCache[id] = it }
                }
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.seedDatabase(getApplication())
        }
    }

    fun selectStream(stream: Stream) {
        _selectedStream.value = stream
    }
}
