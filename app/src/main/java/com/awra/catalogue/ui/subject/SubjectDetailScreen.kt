package com.awra.catalogue.ui.subject

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.R
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.ui.theme.getSubjectBackgroundImage
import com.awra.catalogue.ui.theme.getSubjectIcon
import com.awra.catalogue.ui.theme.toShape
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect
import com.kyant.capsule.ContinuousRoundedRectangle
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky
import com.skydoves.cloudy.cloudy

private data class CategoryItem(
    val type: MaterialType,
    val title: String,
    val description: String,
    val iconRes: Int,
    val containerColor: Color,
    val contentColor: Color
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subject: Subject,
    onBackClick: () -> Unit,
    onCategoryClick: (MaterialType) -> Unit,
    onContinueClick: () -> Unit
) {
    val backgroundImage = getSubjectBackgroundImage(subject.name)
    val subjectColors = getSubjectAdaptiveColors(subject.id)

    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    val onSecondaryContainer = MaterialTheme.colorScheme.onSecondaryContainer
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val onTertiaryContainer = MaterialTheme.colorScheme.onTertiaryContainer
    val errorContainer = MaterialTheme.colorScheme.errorContainer
    val onErrorContainer = MaterialTheme.colorScheme.onErrorContainer
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val surfaceContainerHighest = MaterialTheme.colorScheme.surfaceContainerHighest
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val categories = remember(
        primaryContainer,
        secondaryContainer,
        tertiaryContainer,
        errorContainer,
        surfaceContainerHigh,
        surfaceContainerHighest
    ) {
        listOf(
            CategoryItem(
                MaterialType.HANDOUT,
                "Handouts",
                "Study notes and PDF materials.",
                R.drawable.handout,
                primaryContainer,
                onPrimaryContainer
            ),
            CategoryItem(
                MaterialType.QUIZ,
                "Quizzes",
                "Chapter-wise practice tests.",
                R.drawable.quiz,
                secondaryContainer,
                onSecondaryContainer
            ),
            CategoryItem(
                MaterialType.FLASHCARD,
                "Flashcards",
                "Quick memory aid tools.",
                R.drawable.flashcards,
                tertiaryContainer,
                onTertiaryContainer
            ),
            CategoryItem(
                MaterialType.INFOGRAPHIC,
                "Infographics",
                "Visual summaries of topics.",
                R.drawable.infographics,
                errorContainer,
                onErrorContainer
            ),
            CategoryItem(
                MaterialType.UTILITY,
                "Utilities",
                "Calculators, formula sheets, and more.",
                R.drawable.utility,
                surfaceContainerHigh,
                onSurfaceVariant
            ),
            CategoryItem(
                MaterialType.PDF,
                "PDFs",
                "Imported PDF documents & notes.",
                R.drawable.pdfs,
                surfaceContainerHighest,
                onSurfaceVariant
            )
        )
    }

    val chunkedCategories = remember(categories) { categories.chunked(2) }

    val subjectSky = rememberSky()
    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            // Static Adaptive Mesh Gradient Background attached to .sky() for 0-lag GPU sampling
            AmbientMeshBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .sky(subjectSky),
                primaryColor = subjectColors.content
            )

            // Unified Whole-Screen Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Spacer for floating status bar glass pill
                item {
                    Spacer(modifier = Modifier.statusBarsPadding().height(48.dp))
                }

                // Hero Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = ContinuousRoundedRectangle(32.dp),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // 1. Base Image
                            Image(
                                painter = painterResource(id = backgroundImage),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // 2. Light Progressive Blur
                            Image(
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

                            // 3. Light Overlay
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
                                // Top Section: Subject Icon
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(subject.shape.toShape())
                                            .background(subjectColors.content.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = getSubjectIcon(subject.name)),
                                            contentDescription = null,
                                            tint = subjectColors.content,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                // Bottom Section: Title, Description, and Action
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
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Chapter 3: Quantum Mechanics",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.90f),
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
                }

                // Section Title
                item {
                    Text(
                        text = "Learning Materials",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Categories Grid Items
                items(chunkedCategories) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CategoryCard(item = pair[0], onClick = onCategoryClick)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            if (pair.size > 1) {
                                CategoryCard(item = pair[1], onClick = onCategoryClick)
                            } else {
                                Spacer(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        }

        // Top Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(135.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(40f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = 1.0 - smoothstep(size.y * 0.1, size.y, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        // Bottom Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(40f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = smoothstep(0.0, size.y * 0.9, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        // Floating Circular Translucent Glass Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .size(48.dp)
                .align(Alignment.TopStart)
                .cloudy(
                    sky = subjectSky,
                    radius = 30,
                    tint = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

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