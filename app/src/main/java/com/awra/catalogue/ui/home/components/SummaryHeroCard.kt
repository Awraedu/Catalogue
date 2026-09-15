package com.awra.catalogue.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier
) {
    val isEmpty = quizzesCount == 0 && flashcardsCount == 0 && handoutsProgress == 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
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
                stats = if (isEmpty) {
                    listOf(
                        RingStat(0.08f, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        RingStat(0.06f, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
                        RingStat(0.04f, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
                    )
                } else {
                    listOf(
                        RingStat(handoutsProgress.coerceIn(0f, 1f), MaterialTheme.colorScheme.primary), // Outer - Handouts Progress
                        RingStat((quizzesCount / 50f).coerceIn(0f, 1f), MaterialTheme.colorScheme.secondary), // Middle - Quizzes Taken
                        RingStat((flashcardsCount / 100f).coerceIn(0f, 1f), MaterialTheme.colorScheme.tertiary)  // Inner - Flashcards Reviewed
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
                    text = if (isEmpty) "Ready for Day 1?" else "Your Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                if (isEmpty) {
                    Text(
                        text = "Every expert was once a beginner. Start your journey today!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp).width(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (isEmpty) 16.dp else 24.dp))

                // Stats Column
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryStatItem(
                        value = if (isEmpty) "Start" else "${(handoutsProgress * 100).toInt()}%",
                        label = "Handouts Read",
                        color = MaterialTheme.colorScheme.primary
                    )
                    SummaryStatItem(
                        value = if (isEmpty) "Join" else "$quizzesCount",
                        label = "Quizzes Taken",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    SummaryStatItem(
                        value = if (isEmpty) "Try" else "$flashcardsCount",
                        label = "Flashcards Done",
                        color = MaterialTheme.colorScheme.tertiary
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
    stats: List<RingStat>
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 40.dp.toPx() // Increased stroke width
        val spacing = 8.dp.toPx()
        
        // Center at bottom right corner
        val centerX = size.width
        val centerY = size.height

        stats.forEachIndexed { index, stat ->
            // Outer ring has index 0, reduced radius
            val radius = (size.height * 0.7f) - (index * (strokeWidth + spacing))

            // Background track (quarter circle at the corner)
            drawArc(
                color = Color(0xFF2C2C2E).copy(alpha = 0.5f),
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )

            // Progress arc
            drawArc(
                color = stat.color,
                startAngle = 180f,
                sweepAngle = 90f * stat.progress,
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
                handoutsProgress = 0.65f
            )
            SummaryHeroCard(
                quizzesCount = 0,
                flashcardsCount = 0,
                handoutsProgress = 0f
            )
        }
    }
}
