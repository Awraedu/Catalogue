package com.awra.catalogue.ui.term

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.model.Term
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TermsViewModel(
    private val repository: CatalogueRepository,
    private val initialSubjectId: Long?
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showBookmarkedOnly = MutableStateFlow(false)
    val showBookmarkedOnly: StateFlow<Boolean> = _showBookmarkedOnly.asStateFlow()

    private val _selectedSubjectId = MutableStateFlow(initialSubjectId)
    val selectedSubjectId: StateFlow<Long?> = _selectedSubjectId.asStateFlow()

    // Get all unique subjects for filtering
    val subjects: Flow<List<Subject>> = repository.getAllSubjects()
        .map { it.distinctBy { sub -> sub.name } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val terms: StateFlow<List<Term>> = combine(
        _searchQuery.debounce(300), 
        _showBookmarkedOnly, 
        _selectedSubjectId
    ) { query, bookmarkedOnly, subjectId ->
        Triple(query, bookmarkedOnly, subjectId)
    }.distinctUntilChanged()
    .flatMapLatest { (query, bookmarkedOnly, subjectId) ->
        if (bookmarkedOnly) {
            repository.getBookmarkedTerms()
        } else if (subjectId != null) {
            repository.searchTerms(subjectId, query)
        } else {
            repository.searchAllTerms(query)
        }
    }.map { list ->
        // Ensure UI only shows unique terms (by word and definition)
        list.distinctBy { term -> 
            term.word.lowercase().trim() + ":" + term.definition.lowercase().trim()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
