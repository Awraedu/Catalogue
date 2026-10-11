package com.awra.catalogue.ui.flashcard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.awra.catalogue.data.model.ChapterWithTopics
import com.awra.catalogue.data.model.Difficulty
import com.awra.catalogue.data.model.Subject
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlashcardCustomizationPopup(
    subject: Subject,
    chapters: List<ChapterWithTopics>,
    onDismiss: () -> Unit,
    onStartSession: (FlashcardConfig) -> Unit
) {
    // Theming: Follows the "Flashcards" category card colors (Tertiary)
    val accentColor = MaterialTheme.colorScheme.onTertiaryContainer
    val containerColor = MaterialTheme.colorScheme.tertiaryContainer

    val counts = listOf(10, 20, 30, 40, 50, 100)
    var sliderPosition by remember { mutableFloatStateOf(1f) } // Default to 20
    val selectedCount = counts[sliderPosition.roundToInt()]

    var selectedDifficulty by remember { mutableStateOf(Difficulty.MIXED) }
    var selectedChapterIds by remember(chapters) { 
        mutableStateOf(chapters.map { it.chapter.id }.toSet()) 
    }
    var showUnitGrid by remember { mutableStateOf(false) } // Collapsed by default

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = ContinuousRoundedRectangle(32.dp),
            color = containerColor,
            contentColor = accentColor,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Flashcard Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )

                // Number of Cards Slider
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Cards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Surface(
                            shape = CircleShape,
                            color = accentColor,
                            contentColor = containerColor
                        ) {
                            Text(
                                text = selectedCount.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 0f..5f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = accentColor,
                            activeTrackColor = accentColor,
                            inactiveTrackColor = accentColor.copy(alpha = 0.15f),
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        counts.forEachIndexed { index, count ->
                            val isSelected = index == sliderPosition.roundToInt()
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) accentColor else accentColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Difficulty
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Difficulty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Difficulty.entries.forEachIndexed { index, difficulty ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = Difficulty.entries.size),
                                onClick = { selectedDifficulty = difficulty },
                                selected = selectedDifficulty == difficulty,
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = accentColor,
                                    activeContentColor = containerColor,
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = accentColor
                                )
                            ) {
                                Text(difficulty.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }

                // Unit Selector
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Select Chapters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )

                    SplitButtonLayout(
                        onToggleAll = {
                            selectedChapterIds = if (selectedChapterIds.size == chapters.size) {
                                emptySet()
                            } else {
                                chapters.map { it.chapter.id }.toSet()
                            }
                        },
                        onToggleGrid = { showUnitGrid = !showUnitGrid },
                        isAllSelected = selectedChapterIds.size == chapters.size,
                        selectedCount = selectedChapterIds.size,
                        showUnitGrid = showUnitGrid,
                        accentColor = accentColor
                    )

                    if (showUnitGrid) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.heightIn(max = 200.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chapters) { chapterWithTopics ->
                                val isSelected = selectedChapterIds.contains(chapterWithTopics.chapter.id)
                                FlashcardChapterChip(
                                    title = "Ch ${chapterWithTopics.chapter.number}",
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedChapterIds = if (isSelected) {
                                            selectedChapterIds - chapterWithTopics.chapter.id
                                        } else {
                                            selectedChapterIds + chapterWithTopics.chapter.id
                                        }
                                    },
                                    accentColor = accentColor,
                                    containerColor = containerColor
                                )
                            }
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = ContinuousRoundedRectangle(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = accentColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (selectedChapterIds.isNotEmpty()) {
                                onStartSession(
                                    FlashcardConfig(
                                        chapterIds = selectedChapterIds.toList(),
                                        difficulty = selectedDifficulty,
                                        limit = selectedCount
                                    )
                                )
                            }
                        },
                        enabled = selectedChapterIds.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = ContinuousRoundedRectangle(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = containerColor
                        )
                    ) {
                        Text("Start Flash", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SplitButtonLayout(
    onToggleAll: () -> Unit,
    onToggleGrid: () -> Unit,
    isAllSelected: Boolean,
    selectedCount: Int,
    showUnitGrid: Boolean,
    accentColor: Color
) {
    SplitButtonLayout(
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = onToggleAll,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = accentColor.copy(alpha = 0.1f),
                    contentColor = accentColor
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    if (isAllSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = if (isAllSelected) "All Units Selected" else "$selectedCount Units Selected",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                onClick = onToggleGrid,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = accentColor.copy(alpha = 0.2f),
                    contentColor = accentColor
                )
            ) {
                Icon(
                    imageVector = if (showUnitGrid) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle Grid",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FlashcardChapterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    containerColor: Color
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { 
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ) 
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = accentColor,
            selectedLabelColor = containerColor,
            selectedLeadingIconColor = containerColor,
            containerColor = accentColor.copy(alpha = 0.1f),
            labelColor = accentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = Color.Transparent,
            selectedBorderColor = Color.Transparent
        ),
        shape = ContinuousRoundedRectangle(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
