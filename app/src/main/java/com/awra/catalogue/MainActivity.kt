package com.awra.catalogue

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.notification.NotificationScheduler
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.data.util.PreferenceManager
import com.awra.catalogue.ui.analytics.AnalyticsScreen
import com.awra.catalogue.ui.analytics.AnalyticsViewModel
import com.awra.catalogue.ui.components.FloatingNavigationBar
import com.awra.catalogue.ui.components.NavDestination
import com.awra.catalogue.ui.flashcard.*
import com.awra.catalogue.ui.handout.HandoutDetailScreen
import com.awra.catalogue.ui.handout.HandoutListScreen
import com.awra.catalogue.ui.handout.HandoutViewModel
import com.awra.catalogue.ui.home.HomeScreenTest
import com.awra.catalogue.ui.home.HomeViewModel
import com.awra.catalogue.ui.infographic.InfographicDetailScreen
import com.awra.catalogue.ui.infographic.InfographicListScreen
import com.awra.catalogue.ui.infographic.InfographicViewModel
import com.awra.catalogue.ui.onboarding.OnboardingScreen
import com.awra.catalogue.ui.onboarding.OnboardingViewModel
import com.awra.catalogue.ui.paywall.PaywallScreen
import com.awra.catalogue.ui.quiz.*
import com.awra.catalogue.ui.schedule.ScheduleScreen
import com.awra.catalogue.ui.schedule.ScheduleViewModel
import com.awra.catalogue.ui.settings.SettingsScreen
import com.awra.catalogue.ui.settings.SettingsViewModel
import com.awra.catalogue.ui.splash.SplashScreen
import com.awra.catalogue.ui.subject.SubjectDetailScreen
import com.awra.catalogue.ui.term.TermsScreen
import com.awra.catalogue.ui.term.TermsViewModel
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.ui.utility.*
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> }

    private val pendingShortcutDestination = MutableStateFlow<String?>(null)

    override fun onResume() {
        super.onResume()
        // Record activity on every app resume for smart Duolingo habit-learning & streak tracking
        PreferenceManager(this).recordUserActivity()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShortcutIntent(intent)
    }

    private fun handleShortcutIntent(intent: Intent?) {
        val destination = intent?.getStringExtra("shortcut_destination")
        if (!destination.isNullOrEmpty()) {
            pendingShortcutDestination.value = destination
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        handleShortcutIntent(intent)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Immediately remove the system splash screen as soon as our Compose hierarchy draws its first frame
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }

        PreferenceManager(this).recordUserActivity()
        NotificationScheduler.scheduleAllReminders(this)

        setupComposeContent()
    }

    private fun setupComposeContent() {
        val database = AppDatabase.getDatabase(this)
        val repository = CatalogueRepository(database.catalogueDao())
        val preferenceManager = PreferenceManager(this)

        val homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(application, repository, preferenceManager) as T
            }
        })[HomeViewModel::class.java]

        val settingsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(application, preferenceManager) as T
            }
        })[SettingsViewModel::class.java]

        val onboardingViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return OnboardingViewModel(preferenceManager) as T
            }
        })[OnboardingViewModel::class.java]

        val authViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return com.awra.catalogue.ui.auth.AuthViewModel(preferenceManager) as T
            }
        })[com.awra.catalogue.ui.auth.AuthViewModel::class.java]

        val termsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TermsViewModel(repository, null) as T
            }
        })[TermsViewModel::class.java]

        val analyticsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AnalyticsViewModel(repository) as T
            }
        })[AnalyticsViewModel::class.java]

        val scheduleViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScheduleViewModel(repository, applicationContext) as T
            }
        })[ScheduleViewModel::class.java]

        val handoutViewModels = mutableMapOf<Long, HandoutViewModel>()
        val infographicViewModels = mutableMapOf<Long, InfographicViewModel>()
        val utilityViewModels = mutableMapOf<Long, UtilityViewModel>()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            val darkMode by settingsViewModel.darkMode.collectAsState()
            val selectedTheme by settingsViewModel.selectedTheme.collectAsState()
            val userName by settingsViewModel.userName.collectAsState()
            val isOnboardingCompleted by preferenceManager.isOnboardingCompleted.collectAsState()
            var firebaseUser by remember { mutableStateOf(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser) }

            // Use rememberSaveable so rotating the screen doesn't replay the splash screen
            var showSplashOverlay by rememberSaveable { mutableStateOf(true) }

            YourAppTheme(
                darkMode = darkMode,
                selectedTheme = selectedTheme
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // =========================================================================
                    // 1. UNDERLYING APP CONTENT (Pre-warmed, fully edge-to-edge, zero weird scale-in)
                    // =========================================================================
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (firebaseUser == null) {
                        com.awra.catalogue.ui.auth.AuthScreen(
                            viewModel = authViewModel,
                            onAuthSuccess = {
                                firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            }
                        )
                    } else if (!isOnboardingCompleted) {
                        OnboardingScreen(
                            viewModel = onboardingViewModel,
                            onComplete = {
                                onboardingViewModel.nextStep()
                            }
                        )
                    } else {
                        val flashcardSky = rememberSky()

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

                        val shortcutDestination by pendingShortcutDestination.collectAsState()

                        LaunchedEffect(shortcutDestination) {
                            shortcutDestination?.let { dest ->
                                when (dest) {
                                    "SCHEDULE" -> {
                                        currentSubject = null
                                        currentMaterialType = MaterialType.SCHEDULE
                                    }
                                    "TERMS" -> {
                                        currentSubject = null
                                        currentMaterialType = MaterialType.TERM
                                    }
                                    "ANALYTICS" -> {
                                        currentSubject = null
                                        currentMaterialType = MaterialType.ANALYTICS
                                    }
                                    "DAILY_QUIZ", "QUIZ" -> {
                                        currentSubject = null
                                        currentMaterialType = MaterialType.QUIZ
                                        showQuizCustomization = true
                                    }
                                }
                                pendingShortcutDestination.value = null
                            }
                        }

                        BackHandler(
                            enabled = !showSplashOverlay && (currentSubject != null || currentMaterialType != null || showQuizCustomization || showFlashcardCustomization)
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

                        val screenState = remember(
                            currentNavDestination,
                            currentMaterialType,
                            currentSubject,
                            currentHandout,
                            currentUtility,
                            currentInfographic
                        ) {
                            ScreenState(
                                currentNavDestination,
                                currentMaterialType,
                                currentSubject,
                                currentHandout,
                                currentUtility,
                                currentInfographic
                            )
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            // Background Layer with .sky(flashcardSky)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .sky(flashcardSky)
                            ) {
                                AnimatedContent(
                                    targetState = screenState,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(200, easing = LinearOutSlowInEasing)) togetherWith
                                                fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing))
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
                                                        onPlanSelected = { _ ->
                                                            currentMaterialType = null
                                                        }
                                                    )
                                                }
                                                state.materialType == MaterialType.ANALYTICS -> {
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
                                                    HomeScreenTest(
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
                                                        onGoPremiumClick = { currentMaterialType = MaterialType.PAYWALL },
                                                        onStartDailyQuiz = { topicId ->
                                                            lifecycleScope.launch {
                                                                val studiedTopics = repository.getStudiedTopics().first()
                                                                val targetTopic = studiedTopics.find { it.id == topicId }

                                                                if (targetTopic != null) {
                                                                    val topicsInSameSubject = studiedTopics.filter {
                                                                        it.subjectName == targetTopic.subjectName
                                                                    }
                                                                    val subject = repository.getAllSubjects().first().find {
                                                                        it.name == targetTopic.subjectName
                                                                    }

                                                                    if (subject != null) {
                                                                        currentSubject = subject
                                                                        quizConfig = QuizConfig(
                                                                            topicIds = topicsInSameSubject.map { it.id },
                                                                            difficulty = Difficulty.MIXED,
                                                                            limit = 5
                                                                        )
                                                                        currentMaterialType = MaterialType.QUIZ
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                                state.materialType == null -> {
                                                    val subject = state.subject!!
                                                    SubjectDetailScreen(
                                                        subject = subject,
                                                        onBackClick = { currentSubject = null },
                                                        onCategoryClick = {
                                                            when (it) {
                                                                MaterialType.QUIZ -> showQuizCustomization = true
                                                                MaterialType.FLASHCARD -> showFlashcardCustomization = true
                                                                else -> currentMaterialType = it
                                                            }
                                                        },
                                                        onContinueClick = { }
                                                    )
                                                }
                                                state.materialType == MaterialType.HANDOUT -> {
                                                    val subject = state.subject
                                                    if (subject != null) {
                                                        val handoutViewModel = remember(subject.id) {
                                                            handoutViewModels.getOrPut(subject.id) {
                                                                HandoutViewModel(repository, subject.id)
                                                            }
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
                                                            val isPinnedFlow = remember(state.handout.id) {
                                                                repository.isItemPinned(state.handout.id, MaterialType.HANDOUT)
                                                            }
                                                            val isPinned by isPinnedFlow.collectAsState(initial = false)
                                                            HandoutDetailScreen(
                                                                handout = state.handout,
                                                                viewModel = handoutViewModel,
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
                                                            infographicViewModels.getOrPut(subject.id) {
                                                                InfographicViewModel(repository, subject.id)
                                                            }
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
                                                            val isPinnedFlow = remember(state.infographic.id) {
                                                                repository.isItemPinned(state.infographic.id, MaterialType.INFOGRAPHIC)
                                                            }
                                                            val isPinned by isPinnedFlow.collectAsState(initial = false)
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
                                                                        repository.recordStudySession(
                                                                            state.infographic.topicId,
                                                                            MaterialType.INFOGRAPHIC,
                                                                            timeSpent
                                                                        )
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
                                                    LaunchedEffect(state.subject?.id) {
                                                        termsViewModel.selectSubject(state.subject?.id)
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
                                                                utilityViewModels.getOrPut(subject.id) {
                                                                    UtilityViewModel(repository, subject.id)
                                                                }
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
                                                            val isPinnedFlow = remember(state.utility.id) {
                                                                repository.isItemPinned(state.utility.id, MaterialType.UTILITY)
                                                            }
                                                            val isPinned by isPinnedFlow.collectAsState(initial = false)
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

                            // Foreground Overlay Layer for Blurred Customization Popups
                            if ((showQuizCustomization || showFlashcardCustomization) && currentSubject != null) {
                                val chaptersFlow = remember(currentSubject!!.id) {
                                    repository.getChapters(currentSubject!!.id)
                                }
                                val chapters by chaptersFlow.collectAsState(initial = emptyList())

                                if (showQuizCustomization) {
                                    QuizCustomizationPopup(
                                        subject = currentSubject!!,
                                        chapters = chapters,
                                        sky = flashcardSky,
                                        onDismiss = { showQuizCustomization = false },
                                        onStartQuiz = { config ->
                                            quizConfig = config
                                            showQuizCustomization = false
                                            currentMaterialType = MaterialType.QUIZ
                                        }
                                    )
                                }

                                if (showFlashcardCustomization) {
                                    FlashcardCustomizationPopup(
                                        subject = currentSubject!!,
                                        chapters = chapters,
                                        sky = flashcardSky,
                                        onDismiss = {
                                            showFlashcardCustomization = false
                                        },
                                        onStartSession = { config ->
                                            flashcardConfig = config
                                            showFlashcardCustomization = false
                                            currentMaterialType = MaterialType.FLASHCARD
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // =========================================================================
                    // 2. COMPOSE SPLASH SCREEN OVERLAY (Fades out smoothly)
                    // =========================================================================
                    if (showSplashOverlay) {
                        SplashScreen(
                            onAnimationFinished = {
                                showSplashOverlay = false
                                requestNotificationPermission()
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
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}