package com.awra.catalogue.ui.term

import android.view.HapticFeedbackConstants
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalView
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
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import com.awra.catalogue.ui.components.NavDestination
import com.awra.catalogue.ui.theme.toShape
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect
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
    val isSearchActive by remember { 
        derivedStateOf { 
            searchBarState.currentValue == SearchBarValue.Expanded || 
            searchBarState.targetValue == SearchBarValue.Expanded 
        }
    }

    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChange(textFieldState.text.toString())
    }

    val view = LocalView.current
    val inputField = remember(textFieldState, searchBarState, showBookmarkedOnly) {
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(
                        "Search dictionary...",
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                    )
                },
                leadingIcon = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            scope.launch { searchBarState.animateToCollapsed() }
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                textFieldState.clearText()
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            viewModel.toggleBookmarkedOnly()
                        }) {
                            Icon(
                                imageVector = if (showBookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = if (showBookmarkedOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.surface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.surface,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            AmbientMeshBackground(modifier = Modifier.fillMaxSize())
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .semantics { isTraversalGroup = true }
                ) {
                    // 1. Terms Scrolling List underneath
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 135.dp, bottom = 140.dp, start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                    ) {
                        itemsIndexed(terms, key = { _, term -> term.id }) { index, term ->
                            TermListItem(
                                term = term,
                                index = index,
                                count = terms.size,
                                onBookmarkClick = { viewModel.toggleBookmark(term) }
                            )
                        }

                        if (terms.isEmpty()) {
                            item(key = "empty_state") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp),
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
                    }
                }
            }
        }

        // Top Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(230.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(48f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = 1.0 - smoothstep(size.y * 0.2, size.y, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        // Bottom Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(16f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = smoothstep(0.0, size.y * 0.9, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }
        ) {
            // 2. Floating Search Bar & Filter Chips at Top
            Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .semantics { traversalIndex = 0f },
                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = CircleShape
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            val isSelected = selectedSubjectId == null
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.selectSubject(null)
                                },
                                label = {
                                    Text(
                                        "All",
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.onSurface,
                                    labelColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                    selectedContainerColor = MaterialTheme.colorScheme.surface,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = Color.Transparent,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                        }
                        items(subjects, key = { it.id }) { sub ->
                            val isSelected = selectedSubjectId == sub.id
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
                                selected = isSelected,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.selectSubject(sub.id)
                                },
                                label = {
                                    Text(
                                        shortName,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.onSurface,
                                    labelColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                    selectedContainerColor = MaterialTheme.colorScheme.surface,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = Color.Transparent,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }

                // Full Screen Search Overlay (only rendered when search is active)
                if (isSearchActive) {
                    ExpandedFullScreenSearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        val displayTerms = if (textFieldState.text.isEmpty()) emptyList() else terms
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                        ) {
                            itemsIndexed(displayTerms, key = { _, term -> term.id }) { index, term ->
                                TermListItem(
                                    term = term,
                                    index = index,
                                    count = displayTerms.size,
                                    onBookmarkClick = { viewModel.toggleBookmark(term) }
                                )
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
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                        )
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
    val randomShape = remember(term.id) {
        val expressiveShapes = ExpressiveShape.entries
        expressiveShapes[(term.id % expressiveShapes.size).toInt()]
    }

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
