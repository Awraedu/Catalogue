package com.awra.catalogue.ui.term

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.model.Term
import com.awra.catalogue.ui.components.FloatingNavigationBar
import com.awra.catalogue.ui.components.NavDestination
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TermsScreen(
    subject: Subject?,
    viewModel: TermsViewModel,
    onBackClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onScheduleClick: () -> Unit
) {
    val terms by viewModel.terms.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showBookmarkedOnly by viewModel.showBookmarkedOnly.collectAsState()
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val selectedSubjectId by viewModel.selectedSubjectId.collectAsState()

    val textFieldState = rememberTextFieldState(searchQuery)
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    // Determine if we are in search mode to optimize rendering
    val isSearchExpanded by remember { 
        derivedStateOf { searchBarState.currentValue == SearchBarValue.Expanded } 
    }

    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChange(textFieldState.text.toString())
    }

    val inputField = remember(textFieldState, searchBarState, showBookmarkedOnly) {
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text("Search dictionary...") },
                leadingIcon = {
                    IconButton(onClick = {
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            scope.launch { searchBarState.animateToCollapsed() }
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = if (searchBarState.currentValue == SearchBarValue.Expanded)
                                Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Search,
                            contentDescription = if (searchBarState.currentValue == SearchBarValue.Expanded) "Back" else "Search"
                        )
                    }
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(onClick = { textFieldState.clearText() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                        IconButton(onClick = { viewModel.toggleBookmarkedOnly() }) {
                            Icon(
                                imageVector = if (showBookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = if (showBookmarkedOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
            // Main Content
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Bar and Chips Container
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(bottom = 8.dp)
                ) {
                    Spacer(modifier = Modifier.height(72.dp)) // Space for floating SearchBar

                    // Subject Filter Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSubjectId == null,
                                onClick = { viewModel.selectSubject(null) },
                                label = { Text("All", modifier = Modifier.padding(horizontal = 4.dp)) },
                                shape = CircleShape
                            )
                        }
                        items(subjects, key = { it.id }) { sub ->
                            val shortName = remember(sub.name) {
                                when {
                                    sub.name.contains("English") -> if (sub.name.contains("I")) "Eng I" else "Eng II"
                                    sub.name.contains("Psychology") -> "Psy"
                                    sub.name.contains("Anthropology") -> "Ant"
                                    sub.name.contains("Critical Thinking") -> "Log"
                                    sub.name.contains("Moral and Civic") -> "Civ"
                                    sub.name.contains("Physics") -> "Phy"
                                    sub.name.contains("Chemistry") -> "Chem"
                                    sub.name.contains("Biology") -> "Bio"
                                    sub.name.contains("Mathematics") -> "Math"
                                    sub.name.contains("Geography") -> "Geo"
                                    sub.name.contains("History") -> "Hist"
                                    sub.name.contains("Economics") -> "Econ"
                                    sub.name.contains("Sociology") -> "Soc"
                                    else -> sub.name.take(3)
                                }
                            }
                            FilterChip(
                                selected = selectedSubjectId == sub.id,
                                onClick = { viewModel.selectSubject(sub.id) },
                                label = { Text(shortName, modifier = Modifier.padding(horizontal = 4.dp)) },
                                shape = CircleShape
                            )
                        }
                    }
                }

                // Terms List (Visible when not searching or as background)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    itemsIndexed(terms, key = { _, term -> term.id }) { index, term ->
                        TermListItem(
                            term = term,
                            index = index,
                            count = terms.size,
                            onBookmarkClick = { viewModel.toggleBookmark(term) }
                        )

                        if (index < terms.size - 1) {
                            Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
                        }
                    }

                    if (terms.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(bottom = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty()) "No terms found." else "No matches found for \"$searchQuery\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Floating Search Bar
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

            // Full Screen Search Overlay
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField
            ) {
                // Only render results in overlay when searching to speed up expansion animation
                val displayTerms = if (textFieldState.text.isEmpty()) emptyList() else terms
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    itemsIndexed(displayTerms, key = { _, term -> term.id }) { index, term ->
                        TermListItem(
                            term = term,
                            index = index,
                            count = displayTerms.size,
                            onBookmarkClick = { viewModel.toggleBookmark(term) }
                        )

                        if (index < displayTerms.size - 1) {
                            Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
                        }
                    }

                    if (textFieldState.text.isNotEmpty() && displayTerms.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matches found for \"${textFieldState.text}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TermListItem(
    term: Term,
    index: Int,
    count: Int,
    onBookmarkClick: () -> Unit
) {
    val outerRadius = 32.dp
    val innerRadius = 8.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    // Deterministic random shape based on ID
    val expressiveShapes = remember { ExpressiveShape.entries }
    val randomShape = expressiveShapes[term.id.toInt() % expressiveShapes.size]

    SegmentedListItem(
        onClick = { /* Could show details */ },
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(randomShape.toShape())
                    .background(MaterialTheme.colorScheme.onSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = term.word.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        },
        trailingContent = {
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (term.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(22.dp),
                    tint = if (term.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = term.word,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = term.definition,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                lineHeight = 22.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
