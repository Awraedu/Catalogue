package com.awra.catalogue.ui.term

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.Stream
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
    val subjects: Flow<List<Subject>> = repository.getSubjects(Stream.COMMON)
        .combine(repository.getSubjects(Stream.NATURAL)) { common, natural -> (common + natural).distinctBy { it.id } }
        .combine(repository.getSubjects(Stream.SOCIAL)) { acc, social -> (acc + social).distinctBy { it.id } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val terms: StateFlow<List<Term>> = combine(
        _searchQuery, 
        _showBookmarkedOnly, 
        _selectedSubjectId
    ) { query, bookmarkedOnly, subjectId ->
        Triple(query, bookmarkedOnly, subjectId)
    }.flatMapLatest { (query, bookmarkedOnly, subjectId) ->
        if (bookmarkedOnly) {
            repository.getBookmarkedTerms()
        } else if (subjectId != null) {
            repository.searchTerms(subjectId, query)
        } else {
            // New repository method needed or just search across all
            repository.searchAllTerms(query)
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
