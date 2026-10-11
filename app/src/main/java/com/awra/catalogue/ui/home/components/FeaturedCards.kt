package com.awra.catalogue.ui.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.awra.catalogue.R
import com.awra.catalogue.data.model.FeaturedItem
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle

class TicketShape(
    private val cornerRadius: Float,
    private val cutoutRadius: Float,
    private val cutoutPositionPct: Float = 0.65f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            reset()
            // Start from top-left after corner
            moveTo(cornerRadius, 0f)
            lineTo(size.width - cornerRadius, 0f)
            // Top-right corner
            arcTo(Rect(size.width - cornerRadius * 2, 0f, size.width, cornerRadius * 2), 270f, 90f, false)
            
            // Right side with cutout
            val cutoutY = size.height * cutoutPositionPct
            lineTo(size.width, cutoutY - cutoutRadius)
            arcTo(Rect(size.width - cutoutRadius, cutoutY - cutoutRadius, size.width + cutoutRadius, cutoutY + cutoutRadius), 270f, -180f, false)
            lineTo(size.width, size.height - cornerRadius)
            
            // Bottom-right corner
            arcTo(Rect(size.width - cornerRadius * 2, size.height - cornerRadius * 2, size.width, size.height), 0f, 90f, false)
            lineTo(cornerRadius, size.height)
            
            // Bottom-left corner
            arcTo(Rect(0f, size.height - cornerRadius * 2, cornerRadius * 2, size.height), 90f, 90f, false)
            
            // Left side with cutout
            lineTo(0f, cutoutY + cutoutRadius)
            arcTo(Rect(-cutoutRadius, cutoutY - cutoutRadius, cutoutRadius, cutoutY + cutoutRadius), 90f, -180f, false)
            lineTo(0f, cornerRadius)
            
            // Top-left corner
            arcTo(Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2), 180f, 90f, false)
            close()
        })
    }
}

@Composable
fun DailyQuizCard(
    item: FeaturedItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val realItem = item as? FeaturedItem.DailyQuiz
    val colors = getSubjectAdaptiveColors(0L)
    val cardShape = ContinuousRoundedRectangle(32.dp)
    
    Card(
        onClick = if (realItem != null && !realItem.isCompleted) onClick else ({}),
        modifier = modifier.fillMaxWidth().aspectRatio(0.85f),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.container)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Decoration (Bottom Right, Shape only)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp)
                    .graphicsLayer {
                        rotationZ = -15f
                        alpha = 0.1f
                    }
                    .background(colors.content, shape = ExpressiveShape.SOFT_BOOM.toShape())
            )

            Column(
                modifier = Modifier.padding(20.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(ExpressiveShape.SOFT_BOOM.toShape())
                            .background(colors.content.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = if (realItem?.isCompleted == true) painterResource(R.drawable.quiz) else painterResource(R.drawable.quiz),
                            null, 
                            tint = colors.content, 
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Quiz of the Day", color = colors.content, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(
                        text = realItem?.subjectName ?: "Daily Review",
                        color = colors.content,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (realItem?.isCompleted == true) "5 Questions • Done (${realItem.lastScore}/5)" 
                               else if (realItem != null) "5 Questions • Test your knowledge!"
                               else "Study more to unlock.",
                        color = colors.content.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp
                    )
                }

                if (realItem?.isCompleted == true) {
                    Text(
                        text = "DONE FOR TODAY",
                        color = colors.content,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.alpha(0.6f)
                    )
                } else {
                    Surface(
                        color = colors.content,
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "START",
                            color = colors.container,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyTermCard(
    item: FeaturedItem,
    modifier: Modifier = Modifier
) {
    val realItem = item as? FeaturedItem.DailyTerm
    val colors = getSubjectAdaptiveColors(1L)
    var revealed by remember { mutableStateOf(false) }
    val cardShape = ContinuousRoundedRectangle(32.dp)
    
    val rotation by animateFloatAsState(
        targetValue = if (revealed) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flip"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(cardShape)
            .clickable(enabled = realItem != null) { revealed = !revealed },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.container)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Decoration (Bottom Right, Shape only)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp)
                    .graphicsLayer {
                        rotationY = if (rotation > 90f) 180f else 0f
                        rotationZ = 15f
                        alpha = 0.1f
                    }
                    .background(colors.content, shape = ExpressiveShape.PUFFY.toShape())
            )

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = if (rotation > 90f) 180f else 0f
                    },
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val isFront = rotation <= 90f

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(ExpressiveShape.PUFFY.toShape())
                            .background(colors.content.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = if (isFront) painterResource(R.drawable.flashcards) else painterResource(R.drawable.flashcards), // Using same icon for both as requested for "recall card"
                            contentDescription = null,
                            tint = colors.content,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isFront) "Recall" else "Answer",
                        color = colors.content.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (realItem == null) "Wait for terms..." 
                           else if (isFront) realItem.definition else realItem.word,
                    color = colors.content,
                    style = if (!isFront || realItem == null) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = if (!isFront || realItem == null) FontWeight.Black else FontWeight.Medium,
                    maxLines = 5,
                    lineHeight = 22.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (realItem == null) "Add subjects"
                        else if (isFront) "Tap to flip" else "Tap to hide",
                        color = colors.content.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    
                    if (isFront && realItem != null) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = colors.content.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NextClassCard(
    item: FeaturedItem,
    modifier: Modifier = Modifier
) {
    val realItem = item as? FeaturedItem.NextClass
    val colors = getSubjectAdaptiveColors(2L)
    val cardShape = ContinuousRoundedRectangle(32.dp)
    
    Card(
        modifier = modifier.fillMaxWidth().aspectRatio(0.85f),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.container)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Decoration (Bottom Right, Shape only)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp)
                    .graphicsLayer {
                        rotationZ = -5f
                        alpha = 0.1f
                    }
                    .background(colors.content, shape = ExpressiveShape.CLOVER_4.toShape())
            )

            Column(
                modifier = Modifier.padding(20.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(ExpressiveShape.CLOVER_4.toShape())
                            .background(colors.content.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = colors.content, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Schedule", color = colors.content, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(
                        text = realItem?.subjectName ?: "No classes",
                        color = colors.content,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (realItem != null) "Room ${realItem.room}" else "Free time!",
                        color = colors.content.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Surface(
                    color = colors.content,
                    shape = CircleShape
                ) {
                    Text(
                        text = if (realItem != null) "In ${realItem.timeRemainingMins}m" else "All Done",
                        color = colors.container,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DailyInsightCard(
    item: FeaturedItem.DailyInsight,
    modifier: Modifier = Modifier
) {
    val colors = getSubjectAdaptiveColors(3L)
    val cardShape = ContinuousRoundedRectangle(32.dp)

    Card(
        modifier = modifier.fillMaxWidth().aspectRatio(0.85f),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.container)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Decoration (Bottom Right, Shape only)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp)
                    .graphicsLayer {
                        rotationZ = 10f
                        alpha = 0.1f
                    }
                    .background(colors.content, shape = ExpressiveShape.SOFT_BURST.toShape())
            )

            Column(
                modifier = Modifier.padding(20.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(ExpressiveShape.SOFT_BURST.toShape())
                            .background(colors.content.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, null, tint = colors.content, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Insight", color = colors.content, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(
                        text = item.word,
                        color = colors.content,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = item.definition,
                        color = colors.content.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "Did you know?",
                    color = colors.content.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PremiumTicketCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cornerRadius = 32.dp
    val cutoutRadius = 12.dp
    
    val density = androidx.compose.ui.platform.LocalDensity.current
    val ticketShapeWithDensity = remember(density) {
        TicketShape(
            cornerRadius = with(density) { cornerRadius.toPx() },
            cutoutRadius = with(density) { cutoutRadius.toPx() },
            cutoutPositionPct = 0.72f
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        shape = ticketShapeWithDensity,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFAC00)), // Ethiopian Gold/Yellow
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dashed line across the ticket
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cutoutY = size.height * 0.72f
                val dashWidth = 8.dp.toPx()
                val dashSpace = 8.dp.toPx()
                
                val startX = cutoutRadius.toPx()
                val endX = size.width - cutoutRadius.toPx()
                
                var currentX = startX
                while (currentX < endX) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.2f),
                        topLeft = Offset(currentX, cutoutY),
                        size = Size(dashWidth, 1.dp.toPx())
                    )
                    currentX += dashWidth + dashSpace
                }
            }

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.verify),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "PREMIUM",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                }

                Column {
                    Text(
                        text = "Unlock Full Access",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Get all handouts, quizzes and offline mode.",
                        color = Color.Black.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIMITED OFFER",
                        color = Color.Black.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
