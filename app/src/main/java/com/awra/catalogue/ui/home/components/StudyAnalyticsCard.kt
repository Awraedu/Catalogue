package com.awra.catalogue.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StudyAnalyticsCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Progress Section
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val progressColor = MaterialTheme.colorScheme.primaryContainer
            val backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = backgroundColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 220f,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Study",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "62%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "of 100%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Stats Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatItem(icon = Icons.Default.Description, label = "Handouts", count = "12/20")
            StatItem(icon = Icons.Default.Quiz, label = "Quizzes", count = "5/8")
            StatItem(icon = Icons.Default.Style, label = "Flashcards", count = "45/100")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, count: String) {
    val shape = ContinuousRoundedRectangle(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 10.sp)
            Text(count, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
    }
}
