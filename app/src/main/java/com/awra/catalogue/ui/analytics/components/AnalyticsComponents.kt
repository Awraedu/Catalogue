package com.awra.catalogue.ui.analytics.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.dao.TopicPerformance
import com.awra.catalogue.data.dao.SubjectMastery
import com.kyant.capsule.ContinuousRoundedRectangle

enum class MasteryTab { Strong, Weak }

private fun abbreviateSubject(name: String): String {
    return when (name.lowercase()) {
        "mathematics" -> "MATH"
        "physics" -> "PHYS"
        "chemistry" -> "CHEM"
        "biology" -> "BIO"
        "english" -> "ENG"
        "civics" -> "CIV"
        "geography" -> "GEOG"
        "history" -> "HIST"
        "economics" -> "ECON"
        "business" -> "BSNS"
        "information technology" -> "IT"
        else -> if (name.length > 4) name.take(4).uppercase() else name.uppercase()
    }
}

@Composable
fun PerformanceSummaryCard(
    label: String,
    value: String,
    trend: String,
    isUp: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
                Surface(
                    color = colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, modifier = Modifier.size(18.dp), tint = colorScheme.onPrimaryContainer)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val trendColor = if (isUp) colorScheme.primary else colorScheme.error
                Text(
                    text = if (isUp) "+$trend%" else "-$trend%",
                    color = trendColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = trendColor
                )
            }
        }
    }
}

@Composable
fun MainPerformanceChart(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(1) }
    val tabs = listOf("Monthly", "Weekly", "Today")
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Performance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Time spent in hours", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        val bgColor by animateColorAsState(if (isSelected) colorScheme.onSurface else Color.Transparent, label = "tabBg")
                        val textColor by animateColorAsState(if (isSelected) colorScheme.surface else colorScheme.onSurfaceVariant, label = "tabText")
                        
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable { selectedTab = index }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = textColor, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            Row(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.End) {
                    Text("20h", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                    Text("10h", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                    Text("0", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.width(16.dp))

                Row(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                    when (selectedTab) {
                        0 -> { // Monthly
                            PerformanceBarGroup(label = "July", heights = listOf(0.3f, 0.8f, 0.5f))
                            PerformanceBarGroup(label = "Aug", heights = listOf(0.4f, 0.9f, 0.6f))
                            PerformanceBarGroup(label = "Sept", heights = listOf(0.2f, 0.7f, 0.4f))
                        }
                        1 -> { // Weekly
                            PerformanceBarGroup(label = "W32", heights = listOf(0.35f, 0.75f, 0.45f))
                            PerformanceBarGroup(label = "W33", heights = listOf(0.45f, 0.95f, 0.55f))
                            PerformanceBarGroup(label = "W34", heights = listOf(0.25f, 0.65f, 0.35f))
                            PerformanceBarGroup(label = "W35", heights = listOf(0.3f, 0.85f, 0.5f))
                        }
                        2 -> { // Today
                            PerformanceBarGroup(label = "Today", heights = listOf(0.5f, 0.9f, 0.7f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem("Handout", colorScheme.onSurface)
                LegendItem("Quiz", colorScheme.surfaceVariant)
                LegendItem("Cards", Color(0xFFC5CAE9))
            }
        }
    }
}

@Composable
private fun PerformanceBarGroup(label: String, heights: List<Float> = listOf(0.4f, 0.8f, 0.6f)) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PerformanceBar(heights.getOrElse(0) { 0.4f }, MaterialTheme.colorScheme.onSurface)
            PerformanceBar(heights.getOrElse(1) { 0.8f }, MaterialTheme.colorScheme.surfaceVariant)
            PerformanceBar(heights.getOrElse(2) { 0.6f }, Color(0xFFC5CAE9))
        }
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PerformanceBar(heightFactor: Float, color: Color) {
    Box(modifier = Modifier.width(14.dp).fillMaxHeight(heightFactor).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).background(color))
}

@Composable
fun MasteryComparisonCard(
    bestSubject: SubjectMastery?,
    worstSubject: SubjectMastery?,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Subject Proficiency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            bestSubject?.let {
                MasteryItem(
                    title = "Strongest Subject",
                    name = it.subjectName,
                    accuracy = (it.correctAttempts.toFloat() / it.totalAttempts.coerceAtLeast(1) * 100).toInt(),
                    color = colorScheme.primary,
                    icon = Icons.Default.Verified
                )
            }

            worstSubject?.let {
                MasteryItem(
                    title = "Needs Focus",
                    name = it.subjectName,
                    accuracy = (it.correctAttempts.toFloat() / it.totalAttempts.coerceAtLeast(1) * 100).toInt(),
                    color = colorScheme.error,
                    icon = Icons.Default.Warning
                )
            }
        }
    }
}

@Composable
private fun MasteryItem(title: String, name: String, accuracy: Int, color: Color, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        Text("$accuracy%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasteryOverview(
    subjects: List<SubjectMastery>,
    allTopics: List<TopicPerformance>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MasteryTab.Strong) }
    
    val filteredSubjects = remember(subjects, selectedTab) {
        if (selectedTab == MasteryTab.Strong) {
            subjects.filter { (it.correctAttempts.toFloat() / it.totalAttempts.coerceAtLeast(1)) >= 0.7f }
                .sortedByDescending { it.correctAttempts.toFloat() / it.totalAttempts.coerceAtLeast(1) }
        } else {
            subjects.filter { (it.correctAttempts.toFloat() / it.totalAttempts.coerceAtLeast(1)) < 0.7f }
                .sortedBy { it.correctAttempts.toFloat() / it.totalAttempts.coerceAtLeast(1) }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Tab Switcher using Home Screen style (SegmentedButton)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            MasteryTab.entries.forEachIndexed { index, tab ->
                SegmentedButton(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = MasteryTab.entries.size),
                    label = { 
                        Text(
                            text = tab.name,
                            style = MaterialTheme.typography.labelLarge
                        ) 
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Subject List with Segmented Design
        Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
            filteredSubjects.forEachIndexed { index, subject ->
                ExpandableSubjectCard(
                    subject = subject,
                    topics = allTopics.filter { it.subjectName == subject.subjectName },
                    index = index,
                    count = filteredSubjects.size
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpandableSubjectCard(
    subject: SubjectMastery,
    topics: List<TopicPerformance>,
    index: Int,
    count: Int
) {
    var isExpanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val mastery = (subject.correctAttempts.toFloat() / subject.totalAttempts.coerceAtLeast(1) * 100).toInt()
    
    val outerRadius = 38.dp
    val innerRadius = 8.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
        shape = itemShape
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primary.copy(alpha = 0.1f)), 
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = colorScheme.primary)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = abbreviateSubject(subject.subjectName), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${subject.totalAttempts} total attempts", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    TrendTag(isUp = true, trend = "4.2")
                    Text(
                        text = "$mastery%", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Black, 
                        color = colorScheme.primary
                    )
                }
            }

            if (isExpanded) {
                Spacer(Modifier.height(24.dp))
                
                // Show max 4 topics at a time, scrollable if more
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (topics.size > 4) 280.dp else 600.dp) // Estimate height for 4 items
                        .clip(ContinuousRoundedRectangle(24.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(end = 4.dp), // Space for potential scrollbar indicator feel
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                    ) {
                        topics.forEachIndexed { tIndex, topic ->
                            TopicMasteryItem(
                                topic = topic,
                                index = tIndex,
                                count = topics.size
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendTag(isUp: Boolean, trend: String) {
    val colorScheme = MaterialTheme.colorScheme
    val trendColor = if (isUp) colorScheme.primary else colorScheme.error
    Surface(
        color = trendColor.copy(alpha = 0.1f),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isUp) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = trendColor
            )
            Text(
                text = "$trend%",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = trendColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopicMasteryItem(
    topic: TopicPerformance,
    index: Int,
    count: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    val progress = topic.correctAttempts.toFloat() / topic.totalAttempts.coerceAtLeast(1)
    
    val outerRadius = 24.dp
    val innerRadius = 4.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    SegmentedListItem(
        onClick = { },
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(containerColor = colorScheme.surface),
        trailingContent = {
            Text(
                text = "${(progress * 100).toInt()}%", 
                style = MaterialTheme.typography.labelMedium, 
                fontWeight = FontWeight.Black
            )
        },
        supportingContent = {
             LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (progress > 0.7f) colorScheme.primary else if (progress > 0.4f) colorScheme.secondary else colorScheme.error,
                trackColor = colorScheme.surfaceVariant
            )
        }
    ) {
        Text(
            text = topic.topicTitle,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MasteryOverviewPreview() {
    val mockSubjects = listOf(
        SubjectMastery(1, "Mathematics", 100, 85, 2.0),
        SubjectMastery(2, "Physics", 100, 45, 2.5),
        SubjectMastery(3, "Chemistry", 100, 90, 1.8),
        SubjectMastery(4, "Biology", 100, 30, 3.0)
    )
    val mockTopics = listOf(
        TopicPerformance(1, "Algebraic Equations", 1, "Mathematics", 50, 45),
        TopicPerformance(2, "Integrals", 2, "Mathematics", 50, 40),
        TopicPerformance(3, "Kinematics", 1, "Physics", 50, 20),
        TopicPerformance(4, "Thermodynamics", 2, "Physics", 50, 25)
    )
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MasteryOverview(subjects = mockSubjects, allTopics = mockTopics)
        }
    }
}
