package com.awra.catalogue.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.Difficulty
import com.awra.catalogue.data.model.QuizQuestion
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: CatalogueRepository,
    val config: QuizConfig? = null,
    val topicId: Long? = null
) : ViewModel() {
    private var questionStartTime = System.currentTimeMillis()

    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions: StateFlow<List<QuizQuestion>> = _questions.asStateFlow()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val flow = when {
                config != null -> repository.getCustomQuizQuestions(config.chapterIds, config.difficulty, config.limit)
                topicId != null -> repository.getQuizQuestions(topicId)
                else -> emptyFlow()
            }
            val loadedQuestions = flow.firstOrNull() ?: emptyList()
            _questions.value = loadedQuestions.map { question ->
                val indexedOptions = question.options.mapIndexed { index, option -> option to index }
                val shuffledOptions = indexedOptions.shuffled()
                val newCorrectIndex = shuffledOptions.indexOfFirst { it.second == question.correctOptionIndex }
                
                question.copy(
                    options = shuffledOptions.map { it.first },
                    correctOptionIndex = newCorrectIndex
                )
            }.shuffled()
        }
    }

    fun selectOption(index: Int) {
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        val currentQuestion = _questions.value.getOrNull(currentState.currentQuestionIndex) ?: return
        
        val isCorrect = currentState.selectedOptionIndex == currentQuestion.correctOptionIndex
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        val timeTaken = System.currentTimeMillis() - questionStartTime

        _uiState.update {
            it.copy(
                isAnswerSubmitted = true,
                isCorrect = isCorrect,
                score = newScore
            )
        }

        viewModelScope.launch {
            repository.recordQuizAttempt(
                quizQuestionId = currentQuestion.id,
                isCorrect = isCorrect,
                timeTakenMs = timeTaken
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex + 1 < _questions.value.size) {
            questionStartTime = System.currentTimeMillis()
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    isAnswerSubmitted = false,
                    isCorrect = null
                )
            }
        } else {
            _uiState.update { it.copy(isQuizFinished = true) }
        }
    }
}

data class QuizConfig(
    val chapterIds: List<Long>,
    val difficulty: Difficulty,
    val limit: Int
)

data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean? = null,
    val score: Int = 0,
    val isQuizFinished: Boolean = false
)
