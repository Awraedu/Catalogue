package com.awra.catalogue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.ui.handout.HandoutListScreen
import com.awra.catalogue.ui.handout.HandoutDetailScreen
import com.awra.catalogue.ui.handout.HandoutViewModel
import com.awra.catalogue.ui.home.HomeScreen
import com.awra.catalogue.ui.home.HomeViewModel
import com.awra.catalogue.ui.quiz.*
import com.awra.catalogue.ui.flashcard.*
import com.awra.catalogue.ui.analytics.AnalyticsScreen
import com.awra.catalogue.ui.analytics.AnalyticsViewModel
import com.awra.catalogue.ui.settings.SettingsScreen
import com.awra.catalogue.ui.settings.SettingsViewModel
import com.awra.catalogue.ui.term.TermsScreen
import com.awra.catalogue.ui.term.TermsViewModel
import com.awra.catalogue.ui.utility.*
import com.awra.catalogue.ui.infographic.InfographicListScreen
import com.awra.catalogue.ui.infographic.InfographicDetailScreen
import com.awra.catalogue.ui.infographic.InfographicViewModel
import com.awra.catalogue.ui.paywall.PaywallScreen
import com.awra.catalogue.ui.components.FloatingNavigationBar
import com.awra.catalogue.ui.components.NavDestination
import com.awra.catalogue.ui.schedule.ScheduleScreen
import com.awra.catalogue.ui.schedule.ScheduleViewModel
import com.awra.catalogue.ui.subject.SubjectDetailScreen
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.data.model.Topic
import com.awra.catalogue.data.util.PreferenceManager
import com.awra.catalogue.data.notification.NotificationScheduler
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule notifications
        NotificationScheduler.scheduleDailyCheck(this)
        
        // Request notification permission for Android 13+
        requestNotificationPermission()
        
        val database = AppDatabase.getDatabase(this)
        val repository = CatalogueRepository(database.catalogueDao())
        val preferenceManager = PreferenceManager(this)
        
        val homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(application, repository) as T
            }
        })[HomeViewModel::class.java]

        val settingsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(application, preferenceManager) as T
            }
        })[SettingsViewModel::class.java]
        val scheduleViewModel = ScheduleViewModel(repository)

        enableEdgeToEdge()
        setContent {
            val darkMode by settingsViewModel.darkMode.collectAsState()
            val selectedTheme by settingsViewModel.selectedTheme.collectAsState()
            val userName by settingsViewModel.userName.collectAsState()

            YourAppTheme(
                darkMode = darkMode,
                selectedTheme = selectedTheme
            ) {
                var currentSubject by remember { mutableStateOf<Subject?>(null) }
                var currentMaterialType by remember { mutableStateOf<MaterialType?>(null) }
                var currentQuizTopic by remember { mutableStateOf<Topic?>(null) }
                var currentUtility by remember { mutableStateOf<Utility?>(null) }
                var currentHandout by remember { mutableStateOf<Handout?>(null) }
                var currentInfographic by remember { mutableStateOf<Infographic?>(null) }
                var quizConfig by remember { mutableStateOf<QuizConfig?>(null) }
                var showQuizCustomization by remember { mutableStateOf(false) }

                var flashcardConfig by remember { mutableStateOf<FlashcardConfig?>(null) }
                var showFlashcardCustomization by remember { mutableStateOf(false) }

                BackHandler(
                    enabled = currentSubject != null || currentMaterialType != null || showQuizCustomization || showFlashcardCustomization
                ) {
                    when {
                        showQuizCustomization -> showQuizCustomization = false
                        showFlashcardCustomization -> showFlashcardCustomization = false
                        currentHandout != null -> currentHandout = null
                        currentInfographic != null -> currentInfographic = null
                        currentUtility != null -> currentUtility = null
                        currentMaterialType == MaterialType.QUIZ -> {
                            currentMaterialType = null
                            quizConfig = null
                            currentQuizTopic = null
                        }
                        currentMaterialType == MaterialType.FLASHCARD -> {
                            currentMaterialType = null
                            flashcardConfig = null
                        }
                        currentMaterialType == MaterialType.PAYWALL -> {
                            currentMaterialType = null
                        }
                        currentMaterialType != null -> currentMaterialType = null
                        currentSubject != null -> currentSubject = null
                    }
                }

                val currentNavDestination = remember(currentSubject, currentMaterialType) {
                    when {
                        currentSubject != null -> null
                        currentMaterialType == MaterialType.SCHEDULE -> NavDestination.SCHEDULE
                        currentMaterialType == MaterialType.ANALYTICS -> NavDestination.ANALYTICS
                        currentMaterialType == MaterialType.TERM -> NavDestination.TERMS
                        currentMaterialType == null -> NavDestination.HOME
                        else -> null
                    }
                }

                data class ScreenState(
                    val destination: NavDestination?,
                    val materialType: MaterialType?,
                    val subject: Subject?,
                    val handout: Handout?,
                    val utility: Utility?,
                    val infographic: Infographic?
                )

                val screenState = remember(currentNavDestination, currentMaterialType, currentSubject, currentHandout, currentUtility, currentInfographic) {
                    ScreenState(currentNavDestination, currentMaterialType, currentSubject, currentHandout, currentUtility, currentInfographic)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = screenState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) togetherWith
                            fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing))
                        },
                        label = "screen_transition",
                        modifier = Modifier.fillMaxSize()
                    ) { state ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                when {
                                    state.materialType == MaterialType.SETTINGS -> {
                                        SettingsScreen(
                                            viewModel = settingsViewModel,
                                            onBackClick = { currentMaterialType = null }
                                        )
                                    }
                                    state.materialType == MaterialType.PAYWALL -> {
                                        PaywallScreen(
                                            onBackClick = { currentMaterialType = null },
                                            onPlanSelected = { plan ->
                                                // Handle purchase logic
                                                currentMaterialType = null
                                            }
                                        )
                                    }
                                    state.materialType == MaterialType.ANALYTICS -> {
                                        val analyticsViewModel = remember(repository) {
                                            AnalyticsViewModel(repository)
                                        }
                                        AnalyticsScreen(
                                            viewModel = analyticsViewModel,
                                            onBackClick = { currentMaterialType = null }
                                        )
                                    }
                                    state.materialType == MaterialType.SCHEDULE -> {
                                        ScheduleScreen(
                                            viewModel = scheduleViewModel
                                        )
                                    }
                                    state.subject == null && state.materialType != MaterialType.TERM -> {
                                        HomeScreen(
                                            viewModel = homeViewModel,
                                            userName = userName,
                                            onSubjectClick = { currentSubject = it },
                                            onRecentItemClick = { recent ->
                                                lifecycleScope.launch {
                                                    val sub = repository.getSubjectById(recent.subjectId)
                                                    if (sub != null) {
                                                        currentSubject = sub
                                                        when (recent.type) {
                                                            MaterialType.HANDOUT -> {
                                                                currentMaterialType = MaterialType.HANDOUT
                                                                currentHandout = repository.getHandoutById(recent.targetId)
                                                            }
                                                            MaterialType.UTILITY -> {
                                                                currentMaterialType = MaterialType.UTILITY
                                                                currentUtility = repository.getUtilityById(recent.targetId)
                                                            }
                                                            MaterialType.INFOGRAPHIC -> {
                                                                currentMaterialType = MaterialType.INFOGRAPHIC
                                                                currentInfographic = repository.getInfographicById(recent.targetId)
                                                            }
                                                            else -> {}
                                                        }
                                                    }
                                                }
                                            },
                                            onSettingsClick = { currentMaterialType = MaterialType.SETTINGS },
                                            onGoPremiumClick = { currentMaterialType = MaterialType.PAYWALL }
                                        )
                                    }
                                    state.materialType == null -> {
                                        SubjectDetailScreen(
                                            subject = state.subject!!,
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
                                            val chapters by repository.getChapters(state.subject.id).collectAsState(initial = emptyList())
                                            QuizCustomizationPopup(
                                                subject = state.subject,
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
                                            val chapters by repository.getChapters(state.subject.id).collectAsState(initial = emptyList())
                                            FlashcardCustomizationPopup(
                                                subject = state.subject,
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
                                    state.materialType == MaterialType.HANDOUT -> {
                                        val subject = state.subject
                                        if (subject != null) {
                                            val handoutViewModel = remember(subject.id) {
                                                HandoutViewModel(repository, subject.id)
                                            }
                                            if (state.handout == null) {
                                                HandoutListScreen(
                                                    subject = subject,
                                                    viewModel = handoutViewModel,
                                                    onBackClick = { currentMaterialType = null },
                                                    onHandoutClick = { 
                                                        currentHandout = it
                                                    }
                                                )
                                            } else {
                                                val isPinned by repository.isItemPinned(state.handout.id, MaterialType.HANDOUT).collectAsState(initial = false)
                                                HandoutDetailScreen(
                                                    handout = state.handout,
                                                    isPinned = isPinned,
                                                    onTogglePin = {
                                                        lifecycleScope.launch {
                                                            repository.togglePin(state.handout.id, MaterialType.HANDOUT)
                                                        }
                                                    },
                                                    onBackClick = { currentHandout = null },
                                                    onFinish = { timeSpent ->
                                                        handoutViewModel.recordStudy(state.handout.topicId, timeSpent)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    state.materialType == MaterialType.INFOGRAPHIC -> {
                                        val subject = state.subject
                                        if (subject != null) {
                                            val infographicViewModel = remember(subject.id) {
                                                InfographicViewModel(repository, subject.id)
                                            }
                                            if (state.infographic == null) {
                                                InfographicListScreen(
                                                    subject = subject,
                                                    viewModel = infographicViewModel,
                                                    onBackClick = { currentMaterialType = null },
                                                    onInfographicClick = {
                                                        currentInfographic = it
                                                    }
                                                )
                                            } else {
                                                val isPinned by repository.isItemPinned(state.infographic.id, MaterialType.INFOGRAPHIC).collectAsState(initial = false)
                                                InfographicDetailScreen(
                                                    infographic = state.infographic,
                                                    isPinned = isPinned,
                                                    onTogglePin = {
                                                        lifecycleScope.launch {
                                                            repository.togglePin(state.infographic.id, MaterialType.INFOGRAPHIC)
                                                        }
                                                    },
                                                    onBackClick = { currentInfographic = null },
                                                    onFinish = { timeSpent ->
                                                        lifecycleScope.launch {
                                                            repository.recordStudySession(state.infographic.topicId, MaterialType.INFOGRAPHIC, timeSpent)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    state.materialType == MaterialType.QUIZ -> {
                                        val subject = state.subject
                                        if (subject != null) {
                                            val quizViewModel = remember(quizConfig, currentQuizTopic) {
                                                QuizViewModel(
                                                    repository = repository,
                                                    config = quizConfig,
                                                    topicId = currentQuizTopic?.id
                                                )
                                            }
                                            QuizScreen(
                                                subject = subject,
                                                viewModel = quizViewModel,
                                                onBackClick = { 
                                                    currentMaterialType = null
                                                    quizConfig = null
                                                    currentQuizTopic = null
                                                }
                                            )
                                        }
                                    }
                                    state.materialType == MaterialType.FLASHCARD -> {
                                        val subject = state.subject
                                        if (subject != null) {
                                            val flashcardViewModel = remember(flashcardConfig) {
                                                FlashcardViewModel(
                                                    repository = repository,
                                                    config = flashcardConfig
                                                )
                                            }
                                            FlashcardScreen(
                                                subject = subject,
                                                viewModel = flashcardViewModel,
                                                onBackClick = {
                                                    currentMaterialType = null
                                                    flashcardConfig = null
                                                }
                                            )
                                        }
                                    }
                                    state.materialType == MaterialType.TERM -> {
                                        val termsViewModel = remember(state.subject?.id) {
                                            TermsViewModel(repository, state.subject?.id)
                                        }
                                        TermsScreen(
                                            subject = state.subject,
                                            viewModel = termsViewModel,
                                            onBackClick = { 
                                                currentMaterialType = null
                                            },
                                            onAnalyticsClick = {
                                                currentMaterialType = MaterialType.ANALYTICS
                                            },
                                            onScheduleClick = {
                                                currentMaterialType = MaterialType.SCHEDULE
                                            }
                                        )
                                    }
                                    state.materialType == MaterialType.UTILITY -> {
                                        val subject = state.subject
                                        if (subject != null) {
                                            if (state.utility == null) {
                                                val utilityViewModel = remember(subject.id) {
                                                    UtilityViewModel(repository, subject.id)
                                                }
                                                UtilityListScreen(
                                                    subject = subject,
                                                    viewModel = utilityViewModel,
                                                    onBackClick = { currentMaterialType = null },
                                                    onUtilityClick = { 
                                                        currentUtility = it
                                                    }
                                                )
                                            } else {
                                                val isPinned by repository.isItemPinned(state.utility.id, MaterialType.UTILITY).collectAsState(initial = false)
                                                UtilityDetailScreen(
                                                    utility = state.utility,
                                                    isPinned = isPinned,
                                                    onTogglePin = {
                                                        lifecycleScope.launch {
                                                            repository.togglePin(state.utility.id, MaterialType.UTILITY)
                                                        }
                                                    },
                                                    onBackClick = { currentUtility = null }
                                                )
                                            }
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

                    if (currentNavDestination != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            FloatingNavigationBar(
                                currentDestination = currentNavDestination,
                                onNavigate = { dest ->
                                    when (dest) {
                                        NavDestination.HOME -> {
                                            currentSubject = null
                                            currentMaterialType = null
                                        }
                                        NavDestination.SCHEDULE -> {
                                            currentSubject = null
                                            currentMaterialType = MaterialType.SCHEDULE
                                        }
                                        NavDestination.ANALYTICS -> {
                                            currentSubject = null
                                            currentMaterialType = MaterialType.ANALYTICS
                                        }
                                        NavDestination.TERMS -> {
                                            currentSubject = null
                                            currentMaterialType = MaterialType.TERM
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
