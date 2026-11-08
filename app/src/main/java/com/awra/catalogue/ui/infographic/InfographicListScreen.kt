package com.awra.catalogue.ui.infographic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.*
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InfographicListScreen(
    subject: Subject,
    viewModel: InfographicViewModel,
    onBackClick: () -> Unit,
    onInfographicClick: (Infographic) -> Unit
) {
    val chapters by viewModel.chapters.collectAsState(initial = emptyList())
    // Infographic category card color theme (error / red-pink container color)
    val accentColor = MaterialTheme.colorScheme.error

    Box(modifier = Modifier.fillMaxSize()) {
        // Infographic card theme background gradient
        AmbientMeshBackground(
            modifier = Modifier.fillMaxSize(),
            primaryColor = accentColor
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item(key = "top_spacer") {
                Spacer(modifier = Modifier.statusBarsPadding().height(72.dp))
            }

            item(key = "subject_header") {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Infographics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            chapters.forEachIndexed { index, chapterWithTopics ->
                chapterSection(
                    chapterWithTopics = chapterWithTopics,
                    onInfographicClick = onInfographicClick,
                    accentColor = Color.White
                )

                if (index < chapters.size - 1) {
                    item(key = "sep_${chapterWithTopics.chapter.id}") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Floating Circular Translucent Glass Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .size(48.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
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
private fun LazyListScope.chapterSection(
    chapterWithTopics: ChapterWithInfographicTopics,
    onInfographicClick: (Infographic) -> Unit,
    accentColor: Color
) {
    item(key = "ch_${chapterWithTopics.chapter.id}") {
        Text(
            text = "Chapter ${chapterWithTopics.chapter.number}: ${chapterWithTopics.chapter.title}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )
    }

    // Filter topics that have infographics
    val infographicTopics = chapterWithTopics.topics.filter { it.infographic != null }

    itemsIndexed(
        items = infographicTopics,
        key = { _, topicWithInfographic -> "top_${topicWithInfographic.topic.id}" }
    ) { index, topicWithInfographic ->
        TopicItem(
            topicWithInfographic = topicWithInfographic,
            index = index,
            count = infographicTopics.size,
            onInfographicClick = onInfographicClick,
            accentColor = accentColor
        )
        
        if (index < infographicTopics.size - 1) {
            Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopicItem(
    topicWithInfographic: TopicWithInfographic,
    index: Int,
    count: Int,
    onInfographicClick: (Infographic) -> Unit,
    accentColor: Color
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

    SegmentedListItem(
        onClick = { 
            topicWithInfographic.infographic?.let { onInfographicClick(it) }
        },
        shapes = ListItemDefaults.shapes(shape = itemShape),
        leadingContent = {
            Icon(Icons.Default.Image, contentDescription = null, tint = accentColor)
        },
        supportingContent = {
            Text("Visual topic summary")
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.90f)
        )
    ) {
        Text(
            text = topicWithInfographic.topic.title,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}