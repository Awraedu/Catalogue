package com.awra.catalogue.ui.utility.tools.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun SignLanguageContent(onClick: (UtilityItemData) -> Unit) {
    val alphabet = ('A'..'Z').map { it.toString() }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "ASL Manual Alphabet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(alphabet) { letter ->
                Surface(
                    onClick = {
                        onClick(
                            UtilityItemData(
                                title = "Letter $letter",
                                shortDesc = "Manual alphabet for '$letter'",
                                detailedExplanation = "In American Sign Language, '$letter' is represented by a specific hand shape.",
                                examples = emptyList(),
                                realWorldUse = "Used for fingerspelling names and words without a dedicated sign."
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
