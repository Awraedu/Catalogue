package com.awra.catalogue.ui.utility.tools.emergingtech

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun TechRoadmapContent(onClick: (UtilityItemData) -> Unit) {
    val eras = listOf(
        EraData("1st Industrial Revolution", "Steam power and mechanization (1780s)."),
        EraData("2nd Industrial Revolution", "Electric power and mass production (1870s)."),
        EraData("3rd Industrial Revolution", "Computers and automation (1960s)."),
        EraData("4th Industrial Revolution", "AI, IoT, and Cyber-Physical Systems (Present)."),
        EraData("Society 5.0", "Human-centric society balancing economic and social problems via tech.")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "The Evolution of Technology",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(eras) { index, era ->
                Surface(
                    onClick = {
                        onClick(
                            UtilityItemData(
                                title = era.title,
                                shortDesc = "Technological Era",
                                detailedExplanation = era.desc,
                                examples = emptyList(),
                                realWorldUse = "Understanding historical context for current emerging trends."
                            )
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 1.dp
                ) {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "#${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(40.dp)
                        )
                        Column {
                            Text(era.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(era.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private data class EraData(val title: String, val desc: String)
