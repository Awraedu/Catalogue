package com.awra.catalogue.ui.utility.tools.entrepreneurship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun LeanStartupContent(onClick: (UtilityItemData) -> Unit) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Build-Measure-Learn Loop",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        // The Loop Visualization
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LeanNode("BUILD", "Create an MVP (Minimum Viable Product)", MaterialTheme.colorScheme.primary)
            Icon(Icons.Default.ArrowDownward, null, modifier = Modifier.size(32.dp))
            LeanNode("MEASURE", "Collect data from users and experiments", MaterialTheme.colorScheme.secondary)
            Icon(Icons.Default.ArrowDownward, null, modifier = Modifier.size(32.dp))
            LeanNode("LEARN", "Analyze results to Pivot or Persevere", MaterialTheme.colorScheme.tertiary)
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(32.dp).rotate(45f))
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Core Lean Principles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                PrincipleRow("MVP", "Cheapest/fastest way to start learning.")
                PrincipleRow("Validated Learning", "Scientific approach to starting a business.")
                PrincipleRow("Pivot", "Change strategy without changing vision.")
            }
        }
    }
}

@Composable
private fun LeanNode(title: String, desc: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier.size(140.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary)
            Text(desc, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun PrincipleRow(title: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("• ", fontWeight = FontWeight.Bold)
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall)
        }
    }
}
