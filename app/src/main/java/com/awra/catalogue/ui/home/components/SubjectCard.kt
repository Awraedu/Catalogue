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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.ui.theme.getSubjectIcon
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = ContinuousRoundedRectangle(32.dp)
    val colorRoles = getSubjectAdaptiveColors(subject.id)
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colorRoles.container)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    imageVector = getSubjectIcon(subject.name),
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
                    color = colorRoles.content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
}

