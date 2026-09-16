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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.ui.theme.getSubjectBackgroundImage
import com.awra.catalogue.ui.theme.getSubjectIcon
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
    val backgroundImage = getSubjectBackgroundImage(subject.name)
    val subjectColors = com.awra.catalogue.ui.theme.getSubjectAdaptiveColors(subject.id)
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        subject.name, 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
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
            // Hero Card: Redesigned for Maximum Contrast with Black Text
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = ContinuousRoundedRectangle(32.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Base Image
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = backgroundImage),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // 2. Light Progressive Blur (White-ish for black text contrast)
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = backgroundImage),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(70.dp)
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        0.2f to Color.Transparent,
                                        0.6f to subjectColors.container
                                    ),
                                    blendMode = BlendMode.DstIn
                                )
                            },
                        contentScale = ContentScale.FillHeight
                    )
                    
                    // 3. Light Overlay for guaranteed black text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.3f to Color.Transparent,
                                    1f to subjectColors.container.copy(alpha = 0.7f)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Section: Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Subject Icon (Themed)
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(subject.shape.toShape())
                                    .background(subjectColors.content),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = getSubjectIcon(subject.name)),
                                    contentDescription = null,
                                    tint = subjectColors.container,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Colored Progress Tag Pill
                            Surface(
                                shape = CircleShape,
                                color = subjectColors.container,
                                modifier = Modifier.height(28.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "65%",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Bottom Section: Title, Description, and Action (Black Text)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFEFEFEFE), // Deep Black 000000
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Chapter 3: Quantum Mechanics",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFEFEFEFE), // Deep Black 000000
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val cornerRadius by animateDpAsState(targetValue = if (isPressed) 12.dp else 24.dp)
                            
                            Button(
                                onClick = onContinueClick,
                                interactionSource = interactionSource,
                                shape = ContinuousRoundedRectangle(cornerRadius),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = subjectColors.container,
                                    contentColor = subjectColors.content
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text("Resume", fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelLarge)
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
                    com.awra.catalogue.R.drawable.handout, 
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                ),
                CategoryItem(
                    MaterialType.QUIZ, 
                    "Quizzes", 
                    "Chapter-wise practice tests.",
                    com.awra.catalogue.R.drawable.quiz, 
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer
                ),
                CategoryItem(
                    MaterialType.FLASHCARD, 
                    "Flashcards", 
                    "Quick memory aid tools.",
                    com.awra.catalogue.R.drawable.flashcards, 
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.onTertiaryContainer
                ),
                CategoryItem(
                    MaterialType.INFOGRAPHIC, 
                    "Infographics", 
                    "Visual summaries of topics.",
                    com.awra.catalogue.R.drawable.infographics, 
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer
                ),
                CategoryItem(
                    MaterialType.UTILITY, 
                    "Utilities", 
                    "Calculators, formula sheets, and more.",
                    com.awra.catalogue.R.drawable.utility, 
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
    val iconRes: Int,
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
                painter = painterResource(id = item.iconRes),
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
