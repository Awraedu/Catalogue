package com.awra.catalogue.ui.utility.tools.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun HistoricalLeadersContent() {
    val leaders = listOf(
        LeaderInfo("King Ezana", "Aksum", "Adopted Christianity as state religion."),
        LeaderInfo("Lalibela", "Zagwe", "Constructed the famous rock-hewn churches."),
        LeaderInfo("Yekuno Amlak", "Solomonic", "Restored the Solomonic Dynasty."),
        LeaderInfo("Tewodros II", "Modern", "Initiated the unification of Ethiopia."),
        LeaderInfo("Menelik II", "Modern", "Victory at Adwa and expansion of the state."),
        LeaderInfo("Haile Selassie I", "Imperial", "Introduced the first constitution.")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(leaders) { leader ->
            ListItem(
                headlineContent = { Text(leader.name, fontWeight = FontWeight.Bold) },
                overlineContent = { Text(leader.era) },
                supportingContent = { Text(leader.achievement) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.clip(ContinuousRoundedRectangle(20.dp))
            )
        }
    }
}

private data class LeaderInfo(val name: String, val era: String, val achievement: String)
