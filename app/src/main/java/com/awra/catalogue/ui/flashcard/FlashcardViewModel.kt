package com.awra.catalogue.ui.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.Difficulty
import com.awra.catalogue.data.model.Flashcard
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val repository: CatalogueRepository,
    val config: FlashcardConfig? = null,
    val topicId: Long? = null
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            val flow = when {
                config != null -> repository.getCustomFlashcards(config.chapterIds, config.difficulty, config.limit)
                topicId != null -> repository.getFlashcards(topicId)
                else -> emptyFlow()
            }
            flow.collect {
                _flashcards.value = it
            }
        }
    }

    fun nextCard() {
        val currentState = _uiState.value
        if (currentState.currentIndex + 1 < _flashcards.value.size) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    isFlipped = false
                )
            }
        } else {
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    fun previousCard() {
        val currentState = _uiState.value
        if (currentState.currentIndex > 0) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex - 1,
                    isFlipped = false
                )
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }
}

data class FlashcardConfig(
    val chapterIds: List<Long>,
    val difficulty: Difficulty,
    val limit: Int
)

data class FlashcardUiState(
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isFinished: Boolean = false
)
