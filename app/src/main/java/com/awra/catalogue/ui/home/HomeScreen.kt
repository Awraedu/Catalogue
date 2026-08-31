package com.awra.catalogue.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.home.components.HomeFAB
import com.awra.catalogue.ui.home.components.HomeTopBar
import com.awra.catalogue.ui.home.components.SubjectCard
import com.awra.catalogue.ui.home.components.StreamSelector
import com.awra.catalogue.ui.home.components.StudyAnalyticsCard
import com.kyant.capsule.ContinuousRoundedRectangle

import androidx.compose.ui.tooling.preview.Preview
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.ui.theme.YourAppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSubjectClick: (Subject) -> Unit,
    onTermsClick: () -> Unit
) {
    val selectedStream by viewModel.selectedStream.collectAsState()
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())

    HomeScreenContent(
        selectedStream = selectedStream,
        subjects = subjects,
        onStreamSelected = { viewModel.selectStream(it) },
        onSubjectClick = onSubjectClick,
        onTermsClick = onTermsClick
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenContent(
    selectedStream: Stream,
    subjects: List<Subject>,
    onStreamSelected: (Stream) -> Unit,
    onSubjectClick: (Subject) -> Unit,
    onTermsClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar()
        },
        floatingActionButton = {
            HomeFAB(onTermsClick = onTermsClick)
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Section
            item(span = { GridItemSpan(2) }) {
                StudyAnalyticsCard()
            }

            // Recently Opened Section
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Recently Opened",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RecentlyOpenedItem(
                            name = "English", 
                            type = "Handout", 
                            color = Color(0xFF795548),
                            icon = Icons.AutoMirrored.Filled.MenuBook
                        )
                        RecentlyOpenedItem(
                            name = "Physics", 
                            type = "Handout", 
                            color = Color(0xFF3E2723),
                            icon = Icons.Default.Science
                        )
                    }
                }
            }

            // Freshman Subjects Title
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Freshman Subjects",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    StreamSelector(
                        selectedStream = selectedStream,
                        onStreamSelected = onStreamSelected
                    )
                }
            }

            // The Subjects Grid
            items(subjects, key = { it.id }) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = { onSubjectClick(subject) }
                )
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    YourAppTheme {
        HomeScreenContent(
            selectedStream = Stream.NATURAL,
            subjects = listOf(
                Subject(1, "English", "Desc", Stream.COMMON, ExpressiveShape.CIRCLE),
                Subject(2, "Physics", "Desc", Stream.NATURAL, ExpressiveShape.CLOVER_4)
            ),
            onStreamSelected = {},
            onSubjectClick = {},
            onTermsClick={}
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RecentlyOpenedItem(
    name: String, 
    type: String, 
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val shape = ContinuousRoundedRectangle(20.dp)
    Row(
        modifier = Modifier
            .width(160.dp)
            .height(64.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(ContinuousRoundedRectangle(12.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
