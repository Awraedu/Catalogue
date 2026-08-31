package com.awra.catalogue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.ui.handout.HandoutListScreen
import com.awra.catalogue.ui.handout.HandoutViewModel
import com.awra.catalogue.ui.home.HomeScreen
import com.awra.catalogue.ui.home.HomeViewModel
import com.awra.catalogue.ui.quiz.*
import com.awra.catalogue.ui.flashcard.*
import com.awra.catalogue.ui.term.TermsScreen
import com.awra.catalogue.ui.term.TermsViewModel
import com.awra.catalogue.ui.utility.*
import com.awra.catalogue.ui.subject.SubjectDetailScreen
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.data.model.Topic

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = CatalogueRepository(database.catalogueDao())
        
        val homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(application, repository) as T
            }
        })[HomeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            YourAppTheme {
                var currentSubject by remember { mutableStateOf<Subject?>(null) }
                var currentMaterialType by remember { mutableStateOf<MaterialType?>(null) }
                var currentQuizTopic by remember { mutableStateOf<Topic?>(null) }
                var currentUtility by remember { mutableStateOf<Utility?>(null) }
                var quizConfig by remember { mutableStateOf<QuizConfig?>(null) }
                var showQuizCustomization by remember { mutableStateOf(false) }

                var flashcardConfig by remember { mutableStateOf<FlashcardConfig?>(null) }
                var showFlashcardCustomization by remember { mutableStateOf(false) }
                
                when {
                    currentSubject == null && currentMaterialType != MaterialType.TERM -> {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onSubjectClick = { currentSubject = it },
                            onTermsClick = { currentMaterialType = MaterialType.TERM }
                        )
                    }
                    currentMaterialType == null -> {
                        SubjectDetailScreen(
                            subject = currentSubject!!,
                            onBackClick = { currentSubject = null },
                            onCategoryClick = { 
                                when (it) {
                                    MaterialType.QUIZ -> showQuizCustomization = true
                                    MaterialType.FLASHCARD -> showFlashcardCustomization = true
                                    else -> currentMaterialType = it
                                }
                            },
                            onContinueClick = { /* Handle resume */ }
                        )

                        if (showQuizCustomization) {
                            val chapters by repository.getChapters(currentSubject!!.id).collectAsState(initial = emptyList())
                            QuizCustomizationPopup(
                                subject = currentSubject!!,
                                chapters = chapters,
                                onDismiss = { showQuizCustomization = false },
                                onStartQuiz = { config ->
                                    quizConfig = config
                                    showQuizCustomization = false
                                    currentMaterialType = MaterialType.QUIZ
                                }
                            )
                        }

                        if (showFlashcardCustomization) {
                            val chapters by repository.getChapters(currentSubject!!.id).collectAsState(initial = emptyList())
                            FlashcardCustomizationPopup(
                                subject = currentSubject!!,
                                chapters = chapters,
                                onDismiss = { showFlashcardCustomization = false },
                                onStartSession = { config ->
                                    flashcardConfig = config
                                    showFlashcardCustomization = false
                                    currentMaterialType = MaterialType.FLASHCARD
                                }
                            )
                        }
                    }
                    currentMaterialType == MaterialType.HANDOUT -> {
                        val handoutViewModel = remember(currentSubject!!.id) {
                            HandoutViewModel(repository, currentSubject!!.id)
                        }
                        HandoutListScreen(
                            subject = currentSubject!!,
                            viewModel = handoutViewModel,
                            onBackClick = { currentMaterialType = null },
                            onHandoutClick = { /* Handle opening handout */ }
                        )
                    }
                    currentMaterialType == MaterialType.QUIZ -> {
                        val quizViewModel = remember(quizConfig, currentQuizTopic) {
                            QuizViewModel(
                                repository = repository,
                                config = quizConfig,
                                topicId = currentQuizTopic?.id
                            )
                        }
                        QuizScreen(
                            subject = currentSubject!!,
                            viewModel = quizViewModel,
                            onBackClick = { 
                                currentMaterialType = null
                                quizConfig = null
                                currentQuizTopic = null
                            }
                        )
                    }
                    currentMaterialType == MaterialType.FLASHCARD -> {
                        val flashcardViewModel = remember(flashcardConfig) {
                            FlashcardViewModel(
                                repository = repository,
                                config = flashcardConfig
                            )
                        }
                        FlashcardScreen(
                            subject = currentSubject!!,
                            viewModel = flashcardViewModel,
                            onBackClick = {
                                currentMaterialType = null
                                flashcardConfig = null
                            }
                        )
                    }
                    currentMaterialType == MaterialType.TERM -> {
                        val termsViewModel = remember(currentSubject?.id) {
                            TermsViewModel(repository, currentSubject?.id)
                        }
                        TermsScreen(
                            subject = currentSubject,
                            viewModel = termsViewModel,
                            onBackClick = { 
                                currentMaterialType = null
                            }
                        )
                    }
                    currentMaterialType == MaterialType.UTILITY -> {
                        if (currentUtility == null) {
                            val utilityViewModel = remember(currentSubject!!.id) {
                                UtilityViewModel(repository, currentSubject!!.id)
                            }
                            UtilityListScreen(
                                subject = currentSubject!!,
                                viewModel = utilityViewModel,
                                onBackClick = { currentMaterialType = null },
                                onUtilityClick = { currentUtility = it }
                            )
                        } else {
                            UtilityDetailScreen(
                                utility = currentUtility!!,
                                onBackClick = { currentUtility = null }
                            )
                        }
                    }
                    else -> {
                        Button(onClick = { currentMaterialType = null }) {
                            Text("Feature coming soon. Go Back")
                        }
                    }
                }
            }
        }
    }
}
