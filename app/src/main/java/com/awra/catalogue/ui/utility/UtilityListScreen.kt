package com.awra.catalogue.ui.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.model.Utility
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UtilityListScreen(
    subject: Subject,
    viewModel: UtilityViewModel,
    onBackClick: () -> Unit,
    onUtilityClick: (Utility) -> Unit
) {
    val utilities by viewModel.utilities.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(subject.name, style = MaterialTheme.typography.titleMedium)
                        Text("Utilities", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(utilities, key = { _, u -> u.id }) { index, u ->
                UtilityItem(
                    utility = u,
                    index = index,
                    count = utilities.size,
                    onClick = { onUtilityClick(u) }
                )
                
                if (index < utilities.size - 1) {
                    Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UtilityItem(
    utility: Utility,
    index: Int,
    count: Int,
    onClick: () -> Unit
) {
    val outerRadius = 38.dp
    val innerRadius = 6.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    val icon = when (utility.icon) {
        "MenuBook" -> Icons.AutoMirrored.Filled.MenuBook
        "AutoStories" -> Icons.Default.AutoStories
        "HistoryEdu" -> Icons.Default.HistoryEdu
        "RecordVoiceOver" -> Icons.Default.RecordVoiceOver
        "AutoAwesome" -> Icons.Default.AutoAwesome
        "Shield" -> Icons.Default.Shield
        "Timeline" -> Icons.Default.Timeline
        "Psychology" -> Icons.Default.Psychology
        "SupportAgent" -> Icons.Default.SupportAgent
        "Gavel" -> Icons.Default.Gavel
        "Code" -> Icons.Default.Code
        "Functions" -> Icons.Default.Functions
        "Numbers" -> Icons.Default.Numbers
        "Calculate" -> Icons.Default.Calculate
        "TrendingUp" -> Icons.Default.TrendingUp
        "GridOn" -> Icons.Default.GridOn
        else -> Icons.Default.Build
    }

    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        supportingContent = {
            Text("Helper tool")
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Text(
            text = utility.name,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
