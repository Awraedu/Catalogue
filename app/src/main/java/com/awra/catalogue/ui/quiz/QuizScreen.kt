package com.awra.catalogue.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.SubjectColors
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizScreen(
    subject: Subject,
    viewModel: QuizViewModel,
    onBackClick: () -> Unit
) {
    val questions by viewModel.questions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val subjectColors = getSubjectAdaptiveColors(subject.id)
    
    var showExplanationSheet by remember { mutableStateOf(false) }

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
                                text = "Question ${uiState.currentQuestionIndex + 1} of ${questions.size}",
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
                if (questions.isNotEmpty()) {
                    LinearProgressIndicator(
                        progress = { (uiState.currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier.fillMaxWidth(),
                        color = subjectColors.content,
                        trackColor = subjectColors.content.copy(alpha = 0.2f)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isQuizFinished) {
            QuizResultScreen(
                score = uiState.score,
                totalQuestions = questions.size,
                subjectColors = subjectColors,
                onBackClick = onBackClick,
                modifier = Modifier.padding(innerPadding)
            )
        } else if (questions.isNotEmpty()) {
            val currentQuestion = questions[uiState.currentQuestionIndex]
            
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Question Text (No Card)
                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                // Options (Segmented List Design)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = uiState.selectedOptionIndex == index
                        val isCorrect = uiState.isAnswerSubmitted && index == currentQuestion.correctOptionIndex
                        val isWrong = uiState.isAnswerSubmitted && isSelected && index != currentQuestion.correctOptionIndex
                        
                        OptionItem(
                            text = option,
                            isSelected = isSelected,
                            isCorrect = isCorrect,
                            isWrong = isWrong,
                            index = index,
                            count = currentQuestion.options.size,
                            enabled = !uiState.isAnswerSubmitted,
                            onClick = { viewModel.selectOption(index) }
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hint / Why? Button
                    TextButton(
                        onClick = { showExplanationSheet = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = subjectColors.content)
                    ) {
                        val label = if (uiState.isAnswerSubmitted) "Why?" else "Hint"
                        val icon = if (uiState.isAnswerSubmitted) Icons.Default.QuestionMark else Icons.Default.Lightbulb
                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(label, fontWeight = FontWeight.SemiBold)
                    }

                    // Action Button
                    Button(
                        onClick = {
                            if (uiState.isAnswerSubmitted) {
                                viewModel.nextQuestion()
                            } else {
                                viewModel.submitAnswer()
                            }
                        },
                        enabled = uiState.selectedOptionIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = ContinuousRoundedRectangle(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = subjectColors.content,
                            contentColor = subjectColors.container
                        )
                    ) {
                        Text(
                            text = if (uiState.isAnswerSubmitted) "Next Question" else "Submit Answer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (showExplanationSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showExplanationSheet = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .padding(bottom = 32.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (uiState.isAnswerSubmitted) "Explanation" else "Hint",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.isAnswerSubmitted) currentQuestion.explanation else currentQuestion.hint,
                            style = MaterialTheme.typography.bodyLarge
                        )
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
private fun OptionItem(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    index: Int,
    count: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val outerRadius = 28.dp
    val innerRadius = 8.dp
    
    // Smoothly animate corner radii for morphing
    val topRadius by animateDpAsState(
        targetValue = if (isSelected || index == 0) outerRadius else innerRadius,
        label = "topRadius"
    )
    val bottomRadius by animateDpAsState(
        targetValue = if (isSelected || index == count - 1) outerRadius else innerRadius,
        label = "bottomRadius"
    )

    val itemShape = RoundedCornerShape(
        topStart = topRadius,
        topEnd = topRadius,
        bottomStart = bottomRadius,
        bottomEnd = bottomRadius
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCorrect -> Color(0xFF4CAF50) // Material Green 500
            isWrong -> Color(0xFFF44336)   // Material Red 500
            isSelected -> Color(0xFF2196F3) // Material Blue 500 (Standard contrast color)
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isCorrect || isWrong || isSelected -> Color.White
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "contentColor"
    )

    SegmentedListItem(
        onClick = onClick,
        enabled = enabled,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            supportingContentColor = contentColor.copy(alpha = 0.7f),
            disabledContainerColor = backgroundColor,
            disabledContentColor = contentColor,
            disabledSupportingContentColor = contentColor.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun QuizResultScreen(
    score: Int,
    totalQuestions: Int,
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
        Card(
            modifier = Modifier.size(200.dp),
            shape = ContinuousRoundedRectangle(60.dp),
            colors = CardDefaults.cardColors(containerColor = subjectColors.container)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$score / $totalQuestions",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = subjectColors.content
                )
                Text(
                    text = "Correct Answers",
                    style = MaterialTheme.typography.labelLarge,
                    color = subjectColors.content.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Great job!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "You've completed the quiz for this topic.",
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
            Text("Finish", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
