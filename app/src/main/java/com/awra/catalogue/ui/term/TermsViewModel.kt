package com.awra.catalogue.ui.term

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.model.Term
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TermsViewModel(
    private val repository: CatalogueRepository,
    initialSubjectId: Long?
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showBookmarkedOnly = MutableStateFlow(false)
    val showBookmarkedOnly: StateFlow<Boolean> = _showBookmarkedOnly.asStateFlow()

    private val _selectedSubjectId = MutableStateFlow(initialSubjectId)
    val selectedSubjectId: StateFlow<Long?> = _selectedSubjectId.asStateFlow()

    val subjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .map { it.distinctBy { sub -> sub.name } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val terms: StateFlow<List<Term>> = combine(
        _searchQuery.debounce { if (it.isEmpty()) 0L else 250L },
        _showBookmarkedOnly,
        _selectedSubjectId
    ) { query, bookmarkedOnly, subjectId ->
        Triple(query.trim(), bookmarkedOnly, subjectId)
    }
        .distinctUntilChanged()
        .flatMapLatest { (query, bookmarkedOnly, subjectId) ->
            val baseFlow = if (subjectId != null) {
                repository.searchTerms(subjectId, query)
            } else {
                repository.searchAllTerms(query)
            }

            if (bookmarkedOnly) {
                baseFlow.map { list -> list.filter { it.isBookmarked } }
            } else {
                baseFlow
            }
        }
        .map { list ->
            list.distinctBy { term -> term.word to term.definition }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleBookmarkedOnly() {
        _showBookmarkedOnly.value = !_showBookmarkedOnly.value
    }

    fun selectSubject(subjectId: Long?) {
        _selectedSubjectId.value = subjectId
    }

    fun toggleBookmark(term: Term) {
        viewModelScope.launch {
            repository.toggleBookmark(term)
        }
    }
}