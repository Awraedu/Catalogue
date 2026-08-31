package com.awra.catalogue.ui.term

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.model.Term
import com.awra.catalogue.ui.home.components.HomeFAB
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TermsScreen(
    subject: Subject?,
    viewModel: TermsViewModel,
    onBackClick: () -> Unit
) {
    val terms by viewModel.terms.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showBookmarkedOnly by viewModel.showBookmarkedOnly.collectAsState()
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val selectedSubjectId by viewModel.selectedSubjectId.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        // Pill-shaped search bar in top bar
                        TextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 4.dp),
                            placeholder = { Text("Search dictionary...", fontSize = 16.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(24.dp)) },
                            shape = CircleShape,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp))
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleBookmarkedOnly() },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (showBookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = if (showBookmarkedOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                
                // Subject Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
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
                    items(subjects) { sub ->
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
        },
        floatingActionButton = {
            HomeFAB(onTermsClick = { /* Already on Terms screen */ })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 32.dp)
        ) {
            itemsIndexed(terms, key = { _, term -> term.id }) { index, term ->
                TermListItem(
                    term = term,
                    index = index,
                    count = terms.size,
                    onBookmarkClick = { viewModel.toggleBookmark(term) }
                )
                
                if (index < terms.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp)) // Increased breathing space
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
