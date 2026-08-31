package com.awra.catalogue.ui.handout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.*
import com.awra.catalogue.ui.theme.YourAppTheme
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HandoutListScreen(
    subject: Subject,
    viewModel: HandoutViewModel,
    onBackClick: () -> Unit,
    onHandoutClick: (Handout) -> Unit
) {
    val chapters by viewModel.chapters.collectAsState(initial = emptyList())
    val subjectColors = getSubjectAdaptiveColors(subject.id)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(subject.name, style = MaterialTheme.typography.titleMedium)
                        Text("Handouts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            chapters.forEachIndexed { index, chapterWithTopics ->
                chapterSection(
                    chapterWithTopics = chapterWithTopics,
                    onHandoutClick = onHandoutClick,
                    accentColor = subjectColors.content
                )
                
                if (index < chapters.size - 1) {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.chapterSection(
    chapterWithTopics: ChapterWithTopics,
    onHandoutClick: (Handout) -> Unit,
    accentColor: Color
) {
    item(key = "ch_${chapterWithTopics.chapter.id}") {
        Text(
            text = "Chapter ${chapterWithTopics.chapter.number}: ${chapterWithTopics.chapter.title}",
            style = MaterialTheme.typography.titleSmall,
            color = accentColor,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )
    }

    itemsIndexed(
        items = chapterWithTopics.topics,
        key = { _, topicWithHandout -> "top_${topicWithHandout.topic.id}" }
    ) { index, topicWithHandout ->
        TopicItem(
            topicWithHandout = topicWithHandout,
            index = index,
            count = chapterWithTopics.topics.size,
            onHandoutClick = onHandoutClick,
            accentColor = accentColor
        )
        
        if (index < chapterWithTopics.topics.size - 1) {
            Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopicItem(
    topicWithHandout: TopicWithHandout,
    index: Int,
    count: Int,
    onHandoutClick: (Handout) -> Unit,
    accentColor: Color
) {
    // High-expressive smooth shapes for segmented items with further increased radius
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
            topicWithHandout.handout?.let { onHandoutClick(it) }
        },
        // Overriding the default shapes with our custom high-radius expressive shapes
        shapes = ListItemDefaults.shapes(shape = itemShape),
        leadingContent = {
            Icon(Icons.Default.Description, contentDescription = null, tint = accentColor)
        },
        supportingContent = {
            Text("Topic study materials")
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Text(
            text = topicWithHandout.topic.title,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HandoutListPreview() {
    val mockChapter = Chapter(id = 1, subjectId = 1, number = 1, title = "Quantum Mechanics")
    val mockTopic = Topic(id = 1, chapterId = 1, title = "Wave-Particle Duality")
    val mockChapterWithTopics = ChapterWithTopics(
        chapter = mockChapter,
        topics = listOf(
            TopicWithHandout(mockTopic, Handout(id = 1, topicId = 1, content = "Some content"))
        )
    )
    
    YourAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val primaryColor = MaterialTheme.colorScheme.primary
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                chapterSection(
                    chapterWithTopics = mockChapterWithTopics,
                    onHandoutClick = {},
                    accentColor = primaryColor
                )
            }
        }
    }
}
