package com.awra.catalogue.ui.flashcard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.SubjectColors
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlashcardScreen(
    subject: Subject,
    viewModel: FlashcardViewModel,
    onBackClick: () -> Unit
) {
    val flashcards by viewModel.flashcards.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val subjectColors = getSubjectAdaptiveColors(subject.id)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(subject.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Card ${uiState.currentIndex + 1} of ${flashcards.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                if (flashcards.isNotEmpty()) {
                    LinearProgressIndicator(
                        progress = { (uiState.currentIndex + 1).toFloat() / flashcards.size },
                        modifier = Modifier.fillMaxWidth(),
                        color = subjectColors.content,
                        trackColor = subjectColors.content.copy(alpha = 0.2f)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isFinished) {
            FlashcardResultScreen(
                subjectColors = subjectColors,
                onBackClick = onBackClick,
                modifier = Modifier.padding(innerPadding)
            )
        } else if (flashcards.isNotEmpty()) {
            val currentFlashcard = flashcards[uiState.currentIndex]
            
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Flashcard Flip Component
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (uiState.isFlipped) 180f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "cardFlip"
                    )

                    Surface(
                        onClick = { viewModel.flipCard() },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 20f * density
                                
                                // Physical effect: Scale down and lift the card slightly during the midpoint of the flip
                                val rad = Math.toRadians(rotation.toDouble())
                                val sinFactor = kotlin.math.abs(kotlin.math.sin(rad)).toFloat()
                                
                                val scaleFactor = 1f - (sinFactor * 0.1f)
                                scaleX = scaleFactor
                                scaleY = scaleFactor
                                
                                // Lift the card towards the user (Z-axis simulation via Y translation)
                                translationY = -sinFactor * 20.dp.toPx()
                            },
                        shape = ContinuousRoundedRectangle(40.dp),
                        color = if (rotation <= 90f || rotation >= 270f) 
                                    MaterialTheme.colorScheme.surfaceContainerHigh 
                                else subjectColors.container,
                        tonalElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation <= 90f || rotation >= 270f) {
                                // Front Content
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Text(
                                        text = currentFlashcard.front,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 40.sp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.RotateRight,
                                        contentDescription = "Flip",
                                        tint = subjectColors.content.copy(alpha = 0.5f)
                                    )
                                }
                            } else {
                                // Back Content (mirrored)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(32.dp)
                                        .graphicsLayer { rotationY = 180f }
                                ) {
                                    Text(
                                        text = currentFlashcard.back,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        color = subjectColors.content
                                    )
                                }
                            }
                        }
                    }
                }

                // Navigation & Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousCard() },
                        enabled = uiState.currentIndex > 0,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (uiState.currentIndex > 0) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceDim)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                    }

                    // "Missed" Button
                    Button(
                        onClick = { viewModel.recordAttempt(false) },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f),
                        shape = ContinuousRoundedRectangle(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Missed", fontWeight = FontWeight.Bold)
                    }

                    // "Got it" Button
                    Button(
                        onClick = { viewModel.recordAttempt(true) },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f),
                        shape = ContinuousRoundedRectangle(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50), // Standard green for "Correct"
                            contentColor = Color.White
                        )
                    ) {
                        Text("Got it", fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { viewModel.nextCard() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Skip")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = subjectColors.content)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FlashcardResultScreen(
    subjectColors: SubjectColors,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(200.dp),
            shape = ContinuousRoundedRectangle(60.dp),
            color = subjectColors.container
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = subjectColors.content
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "You've reviewed all the selected cards.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = ContinuousRoundedRectangle(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = subjectColors.content,
                contentColor = subjectColors.container
            )
        ) {
            Text("Back to Subject", fontWeight = FontWeight.Bold)
        }
    }
}
