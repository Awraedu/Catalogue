package com.awra.catalogue.ui.utility.tools.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun HistoricalTimelineContent() {
    val events = listOf(
        TimelineEvent("Ancient Times", "Punt, D'mt, and the Rise of Aksumite Civilization.", "3000 BC - 100 AD"),
        TimelineEvent("Aksumite Golden Age", "Introduction of Christianity under King Ezana.", "330 AD"),
        TimelineEvent("Zagwe Dynasty", "Rise of the Zagwe and construction of Lalibela churches.", "1150 - 1270"),
        TimelineEvent("Solomonic Restoration", "Yekuno Amlak restores the Solomonic line.", "1270"),
        TimelineEvent("Adal-Ethiopian War", "Conflict involving Ahmad ibn Ibrahim al-Ghazi.", "1529 - 1543"),
        TimelineEvent("Gondar Period", "Era of Fasilides and architectural achievements.", "1632 - 1769"),
        TimelineEvent("Zemene Mesafint", "The Era of Princes; decentralized power.", "1769 - 1855"),
        TimelineEvent("Modernization Era", "Reign of Tewodros II, Yohannes IV, and Menelik II.", "1855 - 1913"),
        TimelineEvent("Battle of Adwa", "Decisive victory against Italian colonial forces.", "1896"),
        TimelineEvent("Imperial Period", "Reign of Emperor Haile Selassie I.", "1930 - 1974"),
        TimelineEvent("The Derg", "1974 Revolution and military government.", "1974 - 1991"),
        TimelineEvent("Modern Ethiopia", "Establishment of the FDRE and 1995 Constitution.", "1991 - Present")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(events) { event ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Timeline Connector
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(100.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                }
                
                // Content Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = ContinuousRoundedRectangle(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = event.year,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class TimelineEvent(val title: String, val description: String, val year: String)
