package com.awra.catalogue.ui.handout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.*
import com.awra.catalogue.ui.theme.YourAppTheme
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HandoutListScreen(
    subject: Subject,
    viewModel: HandoutViewModel,
    onBackClick: () -> Unit,
    onHandoutClick: (Handout) -> Unit
) {
    val chapters by viewModel.chapters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val downloadStates by viewModel.downloadStates.collectAsState()

    val textFieldState = rememberTextFieldState(searchQuery)
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    val isSearchActive by remember {
        derivedStateOf {
            searchBarState.currentValue == SearchBarValue.Expanded ||
                    searchBarState.targetValue == SearchBarValue.Expanded
        }
    }

    val neutralColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChange(textFieldState.text.toString())
    }

    val inputField = remember(textFieldState, searchBarState) {
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text("Search handouts or chapters...") },
                leadingIcon = {
                    IconButton(onClick = {
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            scope.launch { searchBarState.animateToCollapsed() }
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = { textFieldState.clearText() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    } else if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
                    }
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .semantics { isTraversalGroup = true }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item(key = "top_spacer") {
                    Spacer(modifier = Modifier.height(72.dp))
                }

                item(key = "subject_header") {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    )
                }

                chapters.forEachIndexed { index, chapterWithTopics ->
                    chapterSection(
                        chapterWithTopics = chapterWithTopics,
                        viewModel = viewModel,
                        downloadStates = downloadStates,
                        onHandoutClick = onHandoutClick,
                        accentColor = neutralColor
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

            SearchBar(
                state = searchBarState,
                inputField = inputField,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { traversalIndex = 0f },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = CircleShape
            )

            if (isSearchActive) {
                ExpandedFullScreenSearchBar(
                    state = searchBarState,
                    inputField = inputField
                ) {
                    val query = textFieldState.text.toString().trim()
                    if (query.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Type to search handouts or chapters",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else if (chapters.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matches found for \"$query\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            chapters.forEachIndexed { index, chapterWithTopics ->
                                chapterSection(
                                    chapterWithTopics = chapterWithTopics,
                                    viewModel = viewModel,
                                    downloadStates = downloadStates,
                                    onHandoutClick = {
                                        scope.launch { searchBarState.animateToCollapsed() }
                                        onHandoutClick(it)
                                    },
                                    accentColor = neutralColor
                                )

                                if (index < chapters.size - 1) {
                                    item(key = "s_sep_${chapterWithTopics.chapter.id}") {
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.chapterSection(
    chapterWithTopics: ChapterWithTopics,
    viewModel: HandoutViewModel? = null,
    downloadStates: Map<Long, HandoutDownloadState> = emptyMap(),
    onHandoutClick: (Handout) -> Unit,
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

    itemsIndexed(
        items = chapterWithTopics.topics,
        key = { _, topicWithHandout -> "top_${topicWithHandout.topic.id}" }
    ) { index, topicWithHandout ->
        val context = LocalContext.current
        val handout = topicWithHandout.handout
        val stateForHandout = handout?.let { downloadStates[it.id] }

        val isDownloaded = remember(handout?.id, stateForHandout) {
            when (stateForHandout) {
                is HandoutDownloadState.Downloaded -> true
                else -> handout?.let { viewModel?.isHandoutDownloaded(context, it) } ?: false
            }
        }

        TopicItem(
            topicWithHandout = topicWithHandout,
            index = index,
            count = chapterWithTopics.topics.size,
            isDownloaded = isDownloaded,
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
    isDownloaded: Boolean,
    onHandoutClick: (Handout) -> Unit,
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
            topicWithHandout.handout?.let { onHandoutClick(it) }
        },
        shapes = ListItemDefaults.shapes(shape = itemShape),
        leadingContent = {
            Icon(Icons.Default.Description, contentDescription = null, tint = accentColor)
        },
        supportingContent = {
            Text(if (isDownloaded) "Saved & Encrypted offline" else "Tap to download & study")
        },
        trailingContent = {
            if (isDownloaded) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Downloaded",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Available for download",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
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