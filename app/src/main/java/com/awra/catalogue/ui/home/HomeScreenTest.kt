package com.awra.catalogue.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.model.RecentlyOpenedDisplayItem
import com.awra.catalogue.ui.home.components.SubjectCard
import com.awra.catalogue.ui.home.components.PinnedItemsSection
import com.awra.catalogue.ui.home.components.DailyQuizCard
import com.awra.catalogue.ui.home.components.DailyTermCard
import com.awra.catalogue.ui.home.components.NextClassCard
import com.awra.catalogue.ui.home.components.DailyInsightCard
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.model.FeaturedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTest(
    viewModel: HomeViewModel,
    userName: String,
    onSubjectClick: (Subject) -> Unit,
    onRecentItemClick: (RecentlyOpenedDisplayItem) -> Unit,
    onSettingsClick: () -> Unit,
    onGoPremiumClick: () -> Unit,
    onStartDailyQuiz: (Long) -> Unit
) {
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val pinnedItems by viewModel.pinnedItems.collectAsState()
    val featuredItems by viewModel.featuredItems.collectAsState()
    val selectedStream by viewModel.selectedStream.collectAsState()
    
    HomeScreenTestContent(
        subjects = subjects,
        pinnedItems = pinnedItems,
        featuredItems = featuredItems,
        userName = userName,
        selectedStream = selectedStream,
        onStreamSelect = viewModel::selectStream,
        onSubjectClick = onSubjectClick,
        onRecentItemClick = onRecentItemClick,
        onStartDailyQuiz = onStartDailyQuiz,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTestContent(
    subjects: List<Subject>,
    pinnedItems: List<RecentlyOpenedDisplayItem>,
    featuredItems: List<FeaturedItem>,
    userName: String,
    selectedStream: Stream,
    onStreamSelect: (Stream) -> Unit,
    onSubjectClick: (Subject) -> Unit,
    onRecentItemClick: (RecentlyOpenedDisplayItem) -> Unit,
    onStartDailyQuiz: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showStreamFilter by remember { mutableStateOf(false) }

    val sortedFeaturedItems = remember(featuredItems) {
        featuredItems.sortedWith(compareByDescending<FeaturedItem> {
            when (it) {
                is FeaturedItem.NextClass -> 100
                is FeaturedItem.DailyQuiz -> if (!it.isCompleted) 90 else 10
                is FeaturedItem.DailyTerm -> if (!it.isTried) 80 else 5
                is FeaturedItem.DailyInsight -> 70
                is FeaturedItem.EmptyNextClass -> 60
                is FeaturedItem.EmptyDailyQuiz -> 50
                is FeaturedItem.EmptyDailyTerm -> 40
                else -> 0
            }
        })
    }

    val filteredSubjects = remember(subjects, searchQuery) {
        if (searchQuery.isBlank()) {
            subjects
        } else {
            subjects.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.description.contains(searchQuery, ignoreCase = true) 
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow // Use a lower surface for the page background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Content (Header + Featured + Pinned) with theme-aware background
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    // Header Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { onSettingsClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings, 
                                contentDescription = "Settings", 
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Featured (Edge to Edge Slider)
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Daily Quest",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            items(sortedFeaturedItems) { item ->
                                when (item) {
                                    is FeaturedItem.DailyQuiz, is FeaturedItem.EmptyDailyQuiz -> {
                                        DailyQuizCard(
                                            item = item,
                                            onClick = { 
                                                if (item is FeaturedItem.DailyQuiz) {
                                                    onStartDailyQuiz(item.topicId)
                                                }
                                            },
                                            modifier = Modifier.width(220.dp)
                                        )
                                    }
                                    is FeaturedItem.DailyTerm, is FeaturedItem.EmptyDailyTerm -> {
                                        DailyTermCard(
                                            item = item,
                                            modifier = Modifier.width(220.dp)
                                        )
                                    }
                                    is FeaturedItem.NextClass, is FeaturedItem.EmptyNextClass -> {
                                        NextClassCard(
                                            item = item,
                                            modifier = Modifier.width(220.dp)
                                        )
                                    }
                                    is FeaturedItem.DailyInsight -> {
                                        DailyInsightCard(
                                            item = item,
                                            modifier = Modifier.width(220.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pinned Tools Section
                    if (pinnedItems.isNotEmpty()) {
                        Box(modifier = Modifier.padding(bottom = 16.dp)) {
                            PinnedItemsSection(
                                items = pinnedItems,
                                onItemClick = onRecentItemClick
                    )
                }
            }
        }

                // The Bottom Card (Search + All Subjects Grid)
                // Extends infinitely downwards by using a column with theme-aware background
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .padding(top = 24.dp, bottom = 120.dp)
                        .navigationBarsPadding()
                ) {
                    // Search Bar inside the container
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search subjects...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            trailingIcon = {
                                IconButton(onClick = { showStreamFilter = !showStreamFilter }) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Filter",
                                        tint = if (showStreamFilter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        )
                    }

                    if (showStreamFilter) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Stream.entries.forEach { stream ->
                                FilterChip(
                                    selected = selectedStream == stream,
                                    onClick = { onStreamSelect(stream) },
                                    label = { 
                                        Text(
                                            text = stream.name.lowercase().replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelLarge
                                        ) 
                                    },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    border = null
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(if (showStreamFilter) 12.dp else 24.dp))

                    // Grid of subjects inside white card container manually chunked by 2 columns
                    val chunks = filteredSubjects.chunked(2)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        chunks.forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SubjectCard(
                                        subject = pair[0],
                                        onClick = { onSubjectClick(pair[0]) }
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    if (pair.size > 1) {
                                        SubjectCard(
                                            subject = pair[1],
                                            onClick = { onSubjectClick(pair[1]) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.fillMaxWidth())
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

@Preview(showBackground = true)
@Composable
fun HomeScreenTestPreview() {
    val dummySubjects = listOf(
        Subject(id = 1, name = "English I", description = "Foundational language skills.", stream = Stream.COMMON, shape = ExpressiveShape.CIRCLE),
        Subject(id = 2, name = "Psychology", description = "Introduction to human behavior.", stream = Stream.COMMON, shape = ExpressiveShape.PILL),
        Subject(id = 3, name = "Physics", description = "Mechanics and thermodynamics.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_9),
        Subject(id = 4, name = "Chemistry", description = "Atomic structure and bonding.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_12)
    )
    val dummyFeatured = listOf(
        FeaturedItem.DailyQuiz(
            topicId = 1,
            topicTitle = "English I",
            subjectName = "English I",
            isCompleted = true,
            lastScore = 4
        ),
        FeaturedItem.EmptyDailyTerm,
        FeaturedItem.EmptyNextClass,
        FeaturedItem.DailyInsight(
            word = "Photosynthesis",
            definition = "The process by which green plants and some other organisms use sunlight to synthesize foods from carbon dioxide and water.",
            subjectName = "Biology"
        )
    )
    HomeScreenTestContent(
        subjects = dummySubjects,
        pinnedItems = emptyList(),
        featuredItems = dummyFeatured,
        userName = "Dagi",
        selectedStream = Stream.NATURAL,
        onStreamSelect = {},
        onSubjectClick = {},
        onRecentItemClick = {},
        onStartDailyQuiz = {},
        onSettingsClick = {}
    )
}
