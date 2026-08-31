package com.awra.catalogue.ui.subject

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subject: Subject,
    onBackClick: () -> Unit,
    onCategoryClick: (MaterialType) -> Unit,
    onContinueClick: () -> Unit
) {
    val subjectColors = getSubjectAdaptiveColors(subject.id)
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        subject.name, 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Card: Resume Study
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = ContinuousRoundedRectangle(32.dp),
                colors = CardDefaults.cardColors(containerColor = subjectColors.container)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Resume Study",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = subjectColors.content
                            )
                            Text(
                                text = "Chapter 3: Quantum Mechanics",
                                style = MaterialTheme.typography.bodyMedium,
                                color = subjectColors.content.copy(alpha = 0.7f)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(subject.shape.toShape())
                                .background(subjectColors.content.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = subjectColors.content,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LinearWavyProgressIndicator(
                            progress = { 0.65f },
                            modifier = Modifier.fillMaxWidth(),
                            color = subjectColors.content,
                            trackColor = subjectColors.content.copy(alpha = 0.2f),
                            amplitude = { 1f } // Significant wave height increase
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "45% Completed",
                                style = MaterialTheme.typography.labelMedium,
                                color = subjectColors.content
                            )
                            
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val cornerRadius by animateDpAsState(targetValue = if (isPressed) 12.dp else 24.dp)
                            
                            Button(
                                onClick = onContinueClick,
                                interactionSource = interactionSource,
                                shape = ContinuousRoundedRectangle(cornerRadius),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = subjectColors.content,
                                    contentColor = subjectColors.container
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Resume")
                            }
                        }
                    }
                }
            }

            Text(
                text = "Learning Materials",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Categories Grid with diverse adaptive colors
            val categories = listOf(
                CategoryItem(
                    MaterialType.HANDOUT, 
                    "Handouts", 
                    "Study notes and PDF materials.",
                    Icons.Default.Description, 
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                ),
                CategoryItem(
                    MaterialType.QUIZ, 
                    "Quizzes", 
                    "Chapter-wise practice tests.",
                    Icons.Default.Quiz, 
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer
                ),
                CategoryItem(
                    MaterialType.FLASHCARD, 
                    "Flashcards", 
                    "Quick memory aid tools.",
                    Icons.Default.Style, 
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.onTertiaryContainer
                ),
                CategoryItem(
                    MaterialType.INFOGRAPHIC, 
                    "Infographics", 
                    "Visual summaries of topics.",
                    Icons.Default.Image, 
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer
                ),
                CategoryItem(
                    MaterialType.UTILITY, 
                    "Utilities", 
                    "Calculators, formula sheets, and more.",
                    Icons.Default.Build, 
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories) { item ->
                    CategoryCard(item, onCategoryClick)
                }
            }
        }
    }
}

private data class CategoryItem(
    val type: MaterialType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoryCard(
    item: CategoryItem,
    onClick: (MaterialType) -> Unit
) {
    Card(
        onClick = { onClick(item.type) },
        shape = ContinuousRoundedRectangle(24.dp),
        colors = CardDefaults.cardColors(containerColor = item.containerColor),
        modifier = Modifier.height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = item.contentColor
            )
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = item.contentColor
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = item.contentColor.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubjectDetailPreview() {
    YourAppTheme {
        SubjectDetailScreen(
            subject = Subject(
                id = 1,
                name = "Physics",
                description = "Fundamentals of mechanics and heat.",
                stream = Stream.NATURAL,
                shape = ExpressiveShape.CLOVER_4
            ),
            onBackClick = {},
            onCategoryClick = {},
            onContinueClick = {}
        )
    }
}
