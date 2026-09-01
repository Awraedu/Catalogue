package com.awra.catalogue.ui.utility.tools.economics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun MarketStructuresContent(onClick: (UtilityItemData) -> Unit) {
    val structures = listOf(
        MarketStructure(
            "Perfect Competition",
            "Many small firms",
            "Identical products, no barriers to entry, price takers.",
            "Agriculture (wheat, corn)",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        MarketStructure(
            "Monopolistic Competition",
            "Many firms",
            "Differentiated products, low barriers, some price control.",
            "Restaurants, clothing stores",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        ),
        MarketStructure(
            "Oligopoly",
            "Few large firms",
            "High barriers, interdependent behavior, significant price control.",
            "Airlines, automobile manufacturers",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        ),
        MarketStructure(
            "Monopoly",
            "One firm",
            "Unique product, extremely high barriers, price maker.",
            "Utility companies (water, electricity)",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Comparing Market Structures",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(structures) { item ->
                Surface(
                    onClick = {
                        onClick(
                            UtilityItemData(
                                title = item.name,
                                shortDesc = item.firms,
                                detailedExplanation = item.characteristics,
                                examples = listOf("Typical Example: ${item.examples}"),
                                realWorldUse = "Used to analyze competition and antitrust policy."
                            )
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = item.color,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = item.onColor)
                        Spacer(Modifier.height(4.dp))
                        Text(item.firms, style = MaterialTheme.typography.labelMedium, color = item.onColor.copy(alpha = 0.8f))
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = item.onColor.copy(alpha = 0.2f))
                        Text(item.characteristics, style = MaterialTheme.typography.bodySmall, color = item.onColor)
                    }
                }
            }
        }
    }
}

private data class MarketStructure(
    val name: String,
    val firms: String,
    val characteristics: String,
    val examples: String,
    val color: androidx.compose.ui.graphics.Color,
    val onColor: androidx.compose.ui.graphics.Color
)
