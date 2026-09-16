package com.awra.catalogue.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.ui.theme.CustomColorSchemes
import com.kyant.capsule.ContinuousRoundedRectangle
import com.airbnb.lottie.compose.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val selectedPalette by viewModel.selectedPalette.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    // Support system back press
    BackHandler(enabled = currentStep != OnboardingStep.WELCOME && currentStep != OnboardingStep.LAUNCHPAD) {
        viewModel.previousStep()
    }
    
    // Apply theme preview dynamically during onboarding
    YourAppTheme(
        darkMode = darkMode,
        selectedTheme = selectedPalette
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { it } + fadeIn()) togetherWith 
                            (slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()) togetherWith 
                            (slideOutHorizontally { it } + fadeOut())
                        }.using(SizeTransform(clip = false))
                    },
                    label = "step_transition"
                ) { step ->
                    when (step) {
                        OnboardingStep.WELCOME -> WelcomeStep(onNext = viewModel::nextStep)
                        OnboardingStep.IDENTITY -> IdentityStep(
                            name = viewModel.name.collectAsState().value,
                            onNameChange = viewModel::setName,
                            onNext = viewModel::nextStep
                        )
                        OnboardingStep.STREAM -> StreamStep(
                            selectedStream = viewModel.selectedStream.collectAsState().value,
                            onStreamSelect = viewModel::setStream,
                            onNext = viewModel::nextStep
                        )
                        OnboardingStep.CUSTOMIZATION -> CustomizationStep(
                            selectedPalette = selectedPalette,
                            darkMode = darkMode,
                            onPaletteSelect = viewModel::setPalette,
                            onDarkModeSelect = viewModel::setDarkMode,
                            onNext = viewModel::nextStep
                        )
                        OnboardingStep.GOALS -> GoalStep(
                            selectedMinutes = viewModel.intensityMinutes.collectAsState().value,
                            onMinutesSelect = viewModel::setIntensity,
                            onNext = viewModel::nextStep
                        )
                        OnboardingStep.SCHEDULE -> ScheduleStep(
                            arrivalTime = viewModel.arrivalTime.collectAsState().value,
                            onTimeChange = viewModel::setArrivalTime,
                            onNext = viewModel::nextStep
                        )
                        OnboardingStep.LAUNCHPAD -> LaunchpadStep(
                            userName = viewModel.name.collectAsState().value,
                            onFinish = onComplete
                        )
                    }
                }
                
                // Progress Indicator (Top)
                if (currentStep != OnboardingStep.LAUNCHPAD) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Back Button (Left)
                        if (currentStep != OnboardingStep.WELCOME) {
                            IconButton(
                                onClick = { viewModel.previousStep() },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        StepProgressBar(
                            currentStep = currentStep.ordinal,
                            totalSteps = OnboardingStep.entries.size - 1,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 48.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepProgressBar(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until totalSteps) {
            val progress = when {
                i < currentStep -> 1f
                i == currentStep -> 0.5f
                else -> 0f
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
        }
    }
}

@Composable
fun LottiePlaceholder(rawRes: Int, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

// Fallback for missing raw resources until user adds them
@Composable
fun LottieAsset(type: String, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val rawRes = remember(type) {
        context.resources.getIdentifier(type, "raw", context.packageName)
    }

    if (rawRes != 0) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
        )
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when(type) {
                    "welcome" -> Icons.Default.AutoAwesome
                    "identity" -> Icons.Default.Person
                    "natural" -> Icons.Default.Science
                    "soacial" -> Icons.Default.Public
                    "palette" -> Icons.Default.Palette
                    "goal" -> Icons.Default.Timer
                    "clock" -> Icons.Default.Schedule
                    "launch" -> Icons.Default.RocketLaunch
                    else -> Icons.Default.Block
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Lottie: $type",
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAsset("welcome", modifier = Modifier.fillMaxWidth().height(300.dp))
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Your Academic Command Center",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Master your freshman courses with personalized summaries, quizzes, and smart reminders.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 12.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = ContinuousRoundedRectangle(24.dp)
        ) {
            Text("Start My Journey", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
        }
    }
}

@Composable
private fun IdentityStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(64.dp))
        LottieAsset("identity", modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "First things first,",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ContinuousRoundedRectangle(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text("STUDENT NAME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    TextField(
                        value = name,
                        onValueChange = onNameChange,
                        placeholder = { Text("Type here...") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = ContinuousRoundedRectangle(24.dp)
        ) {
            Text("Looks Good!", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StreamStep(selectedStream: Stream?, onStreamSelect: (Stream) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(text = "The Path", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(text = "Choose your stream", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StreamLargeCard(
                modifier = Modifier.weight(1f),
                title = "Natural",
                lottieType = "natural",
                isSelected = selectedStream == Stream.NATURAL,
                onClick = { onStreamSelect(Stream.NATURAL) }
            )
            StreamLargeCard(
                modifier = Modifier.weight(1f),
                title = "Social",
                lottieType = "soacial",
                isSelected = selectedStream == Stream.SOCIAL,
                onClick = { onStreamSelect(Stream.SOCIAL) }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = selectedStream != null,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = ContinuousRoundedRectangle(24.dp)
        ) {
            Text("Confirm Path", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StreamLargeCard(modifier: Modifier, title: String, lottieType: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(260.dp),
        shape = ContinuousRoundedRectangle(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LottieAsset(lottieType, modifier = Modifier.size(120.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Spacer(Modifier.size(24.dp))
        }
    }
}

@Composable
private fun CustomizationStep(
    selectedPalette: String,
    darkMode: String,
    onPaletteSelect: (String) -> Unit,
    onDarkModeSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(text = "Style", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(text = "Personalize your UI", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val paletteName = when(selectedPalette) {
            "VIBRANT_ORANGE" -> "Vibrant Orange"
            "MIDNIGHT_MONO" -> "Midnight Mono"
            "FOREST_SAGE" -> "Forest Sage"
            "EARTH_BROWN" -> "Earth Brown"
            "TWILIGHT_LAVENDER" -> "Twilight Lavender"
            else -> "Adaptive (System)"
        }
        Text(
            text = "PREVIEW: $paletteName",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        
        // Skeletal Preview
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            shape = ContinuousRoundedRectangle(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    Box(modifier = Modifier.width(100.dp).height(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerHigh))
                }
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceContainerHigh))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.secondaryContainer))
                    Box(modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.tertiaryContainer))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Color Palette", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val isS = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
            val systemPrimary = if (isS) dynamicLightColorScheme(context).primary else MaterialTheme.colorScheme.primary
            val systemSecondary = if (isS) dynamicLightColorScheme(context).secondary else MaterialTheme.colorScheme.secondary
            val systemTertiary = if (isS) dynamicLightColorScheme(context).tertiary else MaterialTheme.colorScheme.tertiary
            
            listOf("DEFAULT", "VIBRANT_ORANGE", "MIDNIGHT_MONO", "FOREST_SAGE", "EARTH_BROWN", "TWILIGHT_LAVENDER").forEach { id ->
                val colors = when(id) {
                    "VIBRANT_ORANGE" -> listOf(Color(0xFFFF6D00), Color(0xFF7C3AED), Color(0xFF059669))
                    "MIDNIGHT_MONO" -> listOf(Color(0xFF000000), Color(0xFF424242), Color(0xFF616161))
                    "FOREST_SAGE" -> listOf(Color(0xFF386B41), Color(0xFF52634F), Color(0xFF386567))
                    "EARTH_BROWN" -> listOf(Color(0xFF7A5900), Color(0xFF6B5D3F), Color(0xFF4A605E))
                    "TWILIGHT_LAVENDER" -> listOf(Color(0xFF6F4DA0), Color(0xFF645A70), Color(0xFF7E525D))
                    else -> listOf(systemPrimary, systemSecondary, systemTertiary)
                }
                
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 56.dp)
                        .clip(ContinuousRoundedRectangle(16.dp))
                        .background(if (selectedPalette == id) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onPaletteSelect(id) }
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    ) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(colors[0]))
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(colors[1]))
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(colors[2]))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Theme Mode", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            listOf("LIGHT", "SYSTEM", "DARK").forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = darkMode == mode,
                    onClick = { onDarkModeSelect(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index, 3),
                    label = { Text(mode.lowercase().capitalize()) }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = ContinuousRoundedRectangle(24.dp)
        ) {
            Text("Perfect Choice", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GoalStep(selectedMinutes: Int, onMinutesSelect: (Int) -> Unit, onNext: () -> Unit) {
    val goals = listOf(
        5 to "Casual",
        15 to "Serious",
        30 to "Intense",
        60 to "Scholar"
    )
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(64.dp))
        LottieAsset("goal", modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "The Heat", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFEF4135))
        Text(text = "Set your daily goal", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(goals.size) { index ->
                val (mins, label) = goals[index]
                val isSelected = selectedMinutes == mins
                Card(
                    onClick = { onMinutesSelect(mins) },
                    shape = ContinuousRoundedRectangle(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFEF4135).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier.height(140.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = mins.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color(0xFFEF4135) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = "min", style = MaterialTheme.typography.labelLarge)
                        Text(text = label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = ContinuousRoundedRectangle(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4135))
        ) {
            Text("Commit to Goal", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleStep(arrivalTime: String, onTimeChange: (String) -> Unit, onNext: () -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    if (showTimePicker) {
        // Simple 12h conversion for picker state initialization
        val timeParts = arrivalTime.split(" ")
        val clockParts = timeParts[0].split(":")
        var h = clockParts[0].toInt()
        val m = clockParts[1].toInt()
        val ampm = timeParts[1]
        
        if (ampm == "PM" && h < 12) h += 12
        if (ampm == "AM" && h == 12) h = 0

        val state = rememberTimePickerState(h, m, false) // false = 12h
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour12 = if (state.hour % 12 == 0) 12 else state.hour % 12
                    val ampmString = if (state.hour < 12) "AM" else "PM"
                    onTimeChange(String.format(java.util.Locale.US, "%02d:%02d %s", hour12, state.minute, ampmString))
                    showTimePicker = false
                }) { Text("Confirm") }
            },
            text = { TimePicker(state) }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(64.dp))
        LottieAsset("clock", modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "The Clock", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFFFD600))
        Text(text = "When do you finish class?", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(ContinuousRoundedRectangle(32.dp))
                .background(Color(0xFFFFD600).copy(alpha = 0.1f))
                .clickable { showTimePicker = true },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(48.dp), tint = Color(0xFFFFD600))
                Spacer(Modifier.height(16.dp))
                Text(
                    text = arrivalTime,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF000000)
                )
                Text("Tap to change", style = MaterialTheme.typography.labelMedium)
            }
        }
        
        Text(
            text = "We'll wait until you're back from campus to send your daily study nudge.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 24.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = ContinuousRoundedRectangle(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600), contentColor = Color.Black)
        ) {
            Text("Sync Notifications", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LaunchpadStep(userName: String, onFinish: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(0f, 1f, animationSpec = tween(3000, easing = LinearOutSlowInEasing)) { value, _ ->
            progress = value
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAsset("launch", modifier = Modifier.size(240.dp))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(160.dp),
                strokeWidth = 10.dp,
                color = MaterialTheme.colorScheme.primary,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "All systems go, $userName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Your personalized academic catalogue is prepared. Let's achieve excellence together.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        if (progress >= 0.99f) {
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = ContinuousRoundedRectangle(24.dp)
            ) {
                Text("Enter Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    }
}
