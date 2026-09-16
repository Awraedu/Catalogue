package com.awra.catalogue.ui.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun SummaryHeroCard(
    quizzesCount: Int,
    flashcardsCount: Int,
    handoutsProgress: Float,
    daysActive: Int,
    modifier: Modifier = Modifier
) {
    // Threshold for "viewable result": 5% handouts, 2 quizzes, or 5 flashcards
    val isLowProgress = handoutsProgress < 0.05f && quizzesCount < 2 && flashcardsCount < 5
    val isDark = isSystemInDarkTheme()

    // Peak Vibrance Ethiopian Flag Colors (Material Accent variants for better dark mode visibility)
    val ethioGreen = if (isDark) Color(0xFF00E676) else Color(0xFF00AD59)
    val ethioYellow = if (isDark) Color(0xFFFFEA00) else Color(0xFFFFD600)
    val ethioRed = if (isDark) Color(0xFFFF1744) else Color(0xFFEF4135)

    // Choose title and message based on progress and days
    val title = when {
        isLowProgress -> "Ready for Day $daysActive?"
        else -> "Day $daysActive Progress"
    }

    val message = when {
        isLowProgress && daysActive == 1 -> "Every expert was once a beginner. Start your journey today!"
        isLowProgress && daysActive > 1 -> "Consistency is key! Let's pick up where you left off."
        else -> "You're making great strides in your academic journey!"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp), // Increased from 240.dp to prevent cropping
        shape = ContinuousRoundedRectangle(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Chart Area - Positioned as background centered at bottom-right
            SummaryRingsChart(
                modifier = Modifier.fillMaxSize(),
                trackColor = MaterialTheme.colorScheme.onSurface,
                stats = if (isLowProgress) {
                    listOf(
                        RingStat(0.08f, ethioGreen),
                        RingStat(0.06f, ethioYellow),
                        RingStat(0.04f, ethioRed)
                    )
                } else {
                    listOf(
                        RingStat(handoutsProgress.coerceIn(0f, 1f), ethioGreen), // Outer - Handouts Progress
                        RingStat((quizzesCount / 50f).coerceIn(0f, 1f), ethioYellow), // Middle - Quizzes Taken
                        RingStat((flashcardsCount / 100f).coerceIn(0f, 1f), ethioRed)  // Inner - Flashcards Reviewed
                    )
                }
            )

            // Foreground Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(200.dp) // Slightly wider to better use space
                )

                Spacer(modifier = Modifier.height(16.dp)) // Reduced from 24.dp

                // Stats Column
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Reduced from 16.dp
                ) {
                    SummaryStatItem(
                        value = if (handoutsProgress < 0.05f) "Start" else "${(handoutsProgress * 100).toInt()}%",
                        label = "Handouts Read",
                        color = ethioGreen
                    )
                    SummaryStatItem(
                        value = if (quizzesCount < 3) "Join" else "$quizzesCount",
                        label = "Quizzes Taken",
                        color = ethioYellow
                    )
                    SummaryStatItem(
                        value = if (flashcardsCount < 5) "Try" else "$flashcardsCount",
                        label = "Flashcards Done",
                        color = ethioRed
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryStatItem(
    value: String,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Vertical indicator line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(32.dp)
                .background(color, RoundedCornerShape(1.dp))
        )

        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp
            )
        }
    }
}

private data class RingStat(val progress: Float, val color: Color)

@Composable
private fun SummaryRingsChart(
    modifier: Modifier = Modifier,
    trackColor: Color,
    stats: List<RingStat>
) {
    // Animation state to trigger the "fill" effect once when entering view
    var animationPlayed by remember { mutableStateOf(false) }
    val progressFactor by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "rings_fill_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Canvas(modifier = modifier) {
        val strokeWidth = 40.dp.toPx() // Increased stroke width
        val spacing = 8.dp.toPx()
        
        // Center at bottom right corner
        val centerX = size.width
        val centerY = size.height

        stats.forEachIndexed { index, stat ->
            // Outer ring has index 0, reduced radius
            val radius = (size.height * 0.7f) - (index * (strokeWidth + spacing))

            // Background track - Uses theme-aware color so it's visible but not muddy in dark mode
            drawArc(
                color = trackColor.copy(alpha = 0.1f),
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )

            // Progress arc - multiplied by progressFactor for initial entry animation
            drawArc(
                color = stat.color,
                startAngle = 180f,
                sweepAngle = 90f * stat.progress * progressFactor,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )
        }
    }
}

@Preview
@Composable
fun SummaryHeroCardPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryHeroCard(
                quizzesCount = 12,
                flashcardsCount = 45,
                handoutsProgress = 0.65f,
                daysActive = 3
            )
            SummaryHeroCard(
                quizzesCount = 0,
                flashcardsCount = 0,
                handoutsProgress = 0f,
                daysActive = 1
            )
        }
    }
}
