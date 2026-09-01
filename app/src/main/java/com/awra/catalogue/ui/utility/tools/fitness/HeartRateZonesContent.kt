package com.awra.catalogue.ui.utility.tools.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun HeartRateZonesContent(onClick: (UtilityItemData) -> Unit) {
    var age by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val userAge = age.toIntOrNull() ?: 20
    val maxHR = 220 - userAge

    val zones = listOf(
        HRZone("Zone 1 (Very Light)", 0.5f..0.6f, "Warm-up, recovery.", Color(0xFF9E9E9E)),
        HRZone("Zone 2 (Light)", 0.6f..0.7f, "Fat burn, basic endurance.", Color(0xFF2196F3)),
        HRZone("Zone 3 (Moderate)", 0.7f..0.8f, "Aerobic fitness, heart health.", Color(0xFF4CAF50)),
        HRZone("Zone 4 (Hard)", 0.8f..0.9f, "Anaerobic capacity, speed.", Color(0xFFFF9800)),
        HRZone("Zone 5 (Maximum)", 0.9f..1.0f, "Sprinting, peak effort.", Color(0xFFF44336))
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Target Heart Rate Zones", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Enter Your Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Estimated Max HR: $maxHR bpm", style = MaterialTheme.typography.labelMedium)

        zones.forEach { zone ->
            val start = (maxHR * zone.range.start).toInt()
            val end = (maxHR * zone.range.endInclusive).toInt()

            Surface(
                onClick = {
                    onClick(UtilityItemData(
                        title = zone.name,
                        shortDesc = "${(zone.range.start * 100).toInt()}% - ${(zone.range.endInclusive * 100).toInt()}% of Max HR",
                        detailedExplanation = "This zone is ideal for ${zone.desc.lowercase()}. Target rate: $start - $end bpm.",
                        examples = emptyList(),
                        realWorldUse = "Used to monitor training intensity."
                    ))
                },
                shape = RoundedCornerShape(16.dp),
                color = zone.color.copy(alpha = 0.15f),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = zone.color)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(zone.color, CircleShape))
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(zone.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("$start - $end bpm", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(zone.desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private data class HRZone(val name: String, val range: ClosedFloatingPointRange<Float>, val desc: String, val color: Color)
