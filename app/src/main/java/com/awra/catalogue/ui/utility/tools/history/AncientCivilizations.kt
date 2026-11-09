package com.awra.catalogue.ui.utility.tools.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun AncientCivilizationsContent() {
    val civilizations = listOf(
        CivilizationInfo("Land of Punt", "Known as the land of gods, famous for trade in incense, gold, and ivory.", "c. 2500 BC"),
        CivilizationInfo("D'mt (Damot)", "Earliest known kingdom in northern Ethiopia, developed iron tools and agriculture.", "c. 800 - 400 BC"),
        CivilizationInfo("Aksumite Empire", "One of the four great powers of the ancient world. Famous for stelae and trade.", "c. 100 - 940 AD"),
        CivilizationInfo("Zagwe Kingdom", "Successor to Aksum, known for rock-hewn architecture and religious devotion.", "c. 1150 - 1270 AD")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        itemsIndexed(civilizations) { index, civil ->
            val outerRadius = 32.dp
            val innerRadius = 8.dp
            val itemShape = remember(index, civilizations.size) {
                when {
                    civilizations.size == 1 -> ContinuousRoundedRectangle(outerRadius)
                    index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
                    index == civilizations.size - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
                    else -> RoundedCornerShape(innerRadius)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = itemShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                ) {
                    Text(civil.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text(civil.period, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(civil.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private data class CivilizationInfo(val title: String, val description: String, val period: String)
