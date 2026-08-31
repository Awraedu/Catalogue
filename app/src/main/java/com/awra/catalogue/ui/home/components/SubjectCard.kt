package com.awra.catalogue.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubjectCard(
    subject: Subject,
    modifier: Modifier = Modifier
) {
    val cardShape = ContinuousRoundedRectangle(32.dp)
    val colorRoles = getSubjectAdaptiveColors(subject.id)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(cardShape)
            .background(colorRoles.container)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon Container with adaptive background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(subject.shape.toShape())
                .background(colorRoles.content.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconForSubject(subject.name),
                contentDescription = null,
                tint = colorRoles.content,
                modifier = Modifier.size(24.dp)
            )
        }

        Column {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorRoles.content
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subject.description,
                style = MaterialTheme.typography.bodySmall,
                color = colorRoles.content.copy(alpha = 0.8f),
                lineHeight = 16.sp,
                maxLines = 2
            )
        }
    }
}

private fun getIconForSubject(name: String): ImageVector {
    return when {
        name.contains("English", ignoreCase = true) -> Icons.AutoMirrored.Filled.MenuBook
        name.contains("Physics", ignoreCase = true) -> Icons.Default.Science
        name.contains("Psychology", ignoreCase = true) -> Icons.Default.Psychology
        name.contains("Mathematics", ignoreCase = true) -> Icons.Default.Calculate
        name.contains("Logic", ignoreCase = true) -> Icons.Default.Lightbulb
        name.contains("Geography", ignoreCase = true) -> Icons.Default.Public
        name.contains("Anthropol", ignoreCase = true) -> Icons.Default.Groups
        name.contains("Emerging", ignoreCase = true) -> Icons.Default.Memory
        name.contains("Civics", ignoreCase = true) -> Icons.Default.Gavel
        name.contains("Fitness", ignoreCase = true) -> Icons.Default.FitnessCenter
        name.contains("Global", ignoreCase = true) -> Icons.Default.Language
        name.contains("Chemistry", ignoreCase = true) -> Icons.Default.Biotech
        name.contains("Biology", ignoreCase = true) -> Icons.Default.PestControl
        name.contains("Economics", ignoreCase = true) -> Icons.AutoMirrored.Filled.TrendingUp
        name.contains("Entreprene", ignoreCase = true) -> Icons.Default.BusinessCenter
        else -> Icons.Default.Book
    }
}
