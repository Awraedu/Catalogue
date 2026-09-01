package com.awra.catalogue.ui.utility.tools.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun BrailleChartContent(onClick: (UtilityItemData) -> Unit) {
    val brailleData = listOf(
        BrailleLetter("A", listOf(1)),
        BrailleLetter("B", listOf(1, 2)),
        BrailleLetter("C", listOf(1, 4)),
        BrailleLetter("D", listOf(1, 4, 5)),
        BrailleLetter("E", listOf(1, 5)),
        BrailleLetter("F", listOf(1, 2, 4)),
        BrailleLetter("G", listOf(1, 2, 4, 5)),
        BrailleLetter("H", listOf(1, 2, 5)),
        BrailleLetter("I", listOf(2, 4)),
        BrailleLetter("J", listOf(2, 4, 5)),
        BrailleLetter("K", listOf(1, 3)),
        BrailleLetter("L", listOf(1, 2, 3)),
        BrailleLetter("M", listOf(1, 3, 4)),
        BrailleLetter("N", listOf(1, 3, 4, 5)),
        BrailleLetter("O", listOf(1, 3, 5)),
        BrailleLetter("P", listOf(1, 2, 3, 4)),
        BrailleLetter("Q", listOf(1, 2, 3, 4, 5)),
        BrailleLetter("R", listOf(1, 2, 3, 5)),
        BrailleLetter("S", listOf(2, 3, 4)),
        BrailleLetter("T", listOf(2, 3, 4, 5)),
        BrailleLetter("U", listOf(1, 3, 6)),
        BrailleLetter("V", listOf(1, 2, 3, 6)),
        BrailleLetter("W", listOf(2, 4, 5, 6)),
        BrailleLetter("X", listOf(1, 3, 4, 6)),
        BrailleLetter("Y", listOf(1, 3, 4, 5, 6)),
        BrailleLetter("Z", listOf(1, 3, 5, 6))
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Braille Alphabet (Grade 1)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(brailleData) { item ->
                BrailleTile(item, onClick)
            }
        }
    }
}

@Composable
private fun BrailleTile(item: BrailleLetter, onClick: (UtilityItemData) -> Unit) {
    Surface(
        onClick = {
            onClick(
                UtilityItemData(
                    title = "Braille '${item.char}'",
                    shortDesc = "Grade 1 Braille cell for ${item.char}",
                    detailedExplanation = "Standard Braille cell consists of 6 dots arranged in a 3x2 grid. Dots for '${item.char}' are: ${item.dots.joinToString(", ")}.",
                    examples = emptyList(),
                    realWorldUse = "Used by visually impaired individuals to read and write."
                )
            )
        },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(item.char, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            BrailleCell(item.dots)
        }
    }
}

@Composable
private fun BrailleCell(activeDots: List<Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (row in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (col in 0..1) {
                    val dotNumber = if (col == 0) row + 1 else row + 4
                    val isActive = activeDots.contains(dotNumber)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

private data class BrailleLetter(val char: String, val dots: List<Int>)
