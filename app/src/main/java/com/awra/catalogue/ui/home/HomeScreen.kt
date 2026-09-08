package com.awra.catalogue.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Science
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
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.home.components.HomeTopBar
import com.awra.catalogue.ui.home.components.SubjectCard
import com.awra.catalogue.ui.home.components.StreamSelector
import com.awra.catalogue.ui.home.components.HomeHeroCard
import com.awra.catalogue.ui.home.components.RecentlyOpenedSection
import com.awra.catalogue.ui.components.FloatingNavigationBar
import com.awra.catalogue.ui.components.NavDestination
import com.awra.catalogue.ui.components.shimmerEffect
import com.kyant.capsule.ContinuousRoundedRectangle
import java.util.Calendar
import androidx.compose.ui.tooling.preview.Preview
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.ui.theme.YourAppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userName: String,
    onSubjectClick: (Subject) -> Unit,
    onRecentItemClick: (com.awra.catalogue.data.model.RecentlyOpenedDisplayItem) -> Unit,
    onSettingsClick: () -> Unit,
    onGoPremiumClick: () -> Unit
) {
    val selectedStream by viewModel.selectedStream.collectAsState()
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val recentlyOpened by viewModel.recentlyOpened.collectAsState(initial = emptyList())

    HomeScreenContent(
        userName = userName,
        selectedStream = selectedStream,
        subjects = subjects,
        recentlyOpened = recentlyOpened,
        onStreamSelected = { viewModel.selectStream(it) },
        onSubjectClick = onSubjectClick,
        onRecentItemClick = onRecentItemClick,
        onSettingsClick = onSettingsClick,
        onGoPremiumClick = onGoPremiumClick
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenContent(
    userName: String,
    selectedStream: Stream,
    subjects: List<Subject>,
    recentlyOpened: List<com.awra.catalogue.data.model.RecentlyOpenedDisplayItem>,
    onStreamSelected: (Stream) -> Unit,
    onSubjectClick: (Subject) -> Unit,
    onRecentItemClick: (com.awra.catalogue.data.model.RecentlyOpenedDisplayItem) -> Unit,
    onSettingsClick: () -> Unit,
    onGoPremiumClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar(
                onSettingsClick = onSettingsClick,
                onPremiumClick = onGoPremiumClick
            )
        },
        bottomBar = { }
    ) { innerPadding ->
        val greeting = remember {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            when (hour) {
                in 0..11 -> "Good Morning"
                in 12..16 -> "Good Afternoon"
                else -> "Good Evening"
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Section
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$greeting, ",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HomeHeroCard()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Get Premium Banner
                    Surface(
                        onClick = onGoPremiumClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ContinuousRoundedRectangle(24.dp))
                            .shimmerEffect(
                                shimmerColor = Color.White.copy(alpha = 0.2f),
                                durationMillis = 2000
                            ),
                        shape = ContinuousRoundedRectangle(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Get Premium", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                                Text("Unlock full access to all features.", style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(Icons.Default.ChevronRight, null)
                        }
                    }
                }
            }

            // Recently Opened Section
            item(span = { GridItemSpan(2) }) {
                RecentlyOpenedSection(
                    items = recentlyOpened,
                    onItemClick = onRecentItemClick
                )
            }

            // Freshman Subjects Title
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
            itemsIndexed(subjects, key = { _, it -> it.id }) { index, subject ->
                Box(
                    modifier = Modifier.padding(
                        start = if (index % 2 == 0) 16.dp else 0.dp,
                        end = if (index % 2 != 0) 16.dp else 0.dp
                    )
                ) {
                    SubjectCard(
                        subject = subject,
                        onClick = { onSubjectClick(subject) }
                    )
                }
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    YourAppTheme {
        HomeScreenContent(
            userName = "Dagi",
            selectedStream = Stream.NATURAL,
            subjects = listOf(
                Subject(1, "English", "Desc", Stream.COMMON, ExpressiveShape.CIRCLE),
                Subject(2, "Physics", "Desc", Stream.NATURAL, ExpressiveShape.CLOVER_4)
            ),
            recentlyOpened = emptyList(),
            onStreamSelected = {},
            onSubjectClick = {},
            onRecentItemClick = {},
            onSettingsClick = {},
            onGoPremiumClick = {}
        )
    }
}
