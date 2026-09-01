package com.awra.catalogue.ui.utility.tools.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun BMICalculatorContent(onClick: (UtilityItemData) -> Unit) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val h = height.toDoubleOrNull() ?: 0.0
    val w = weight.toDoubleOrNull() ?: 0.0
    val bmi = if (h > 0) w / ((h / 100) * (h / 100)) else 0.0

    val (category, color) = when {
        bmi < 18.5 -> "Underweight" to Color(0xFF03A9F4)
        bmi < 25 -> "Normal" to Color(0xFF4CAF50)
        bmi < 30 -> "Overweight" to Color(0xFFFFC107)
        else -> "Obese" to Color(0xFFF44336)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Body Mass Index (BMI)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (bmi > 0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = color.copy(alpha = 0.1f),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = color)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your BMI", style = MaterialTheme.typography.labelLarge, color = color)
                    Text(
                        "${"%.1f".format(bmi)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
                    Surface(
                        color = color,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // BMI Info Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BMI Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                BMIRow("Underweight", "< 18.5")
                BMIRow("Normal", "18.5 – 24.9")
                BMIRow("Overweight", "25 – 29.9")
                BMIRow("Obese", "≥ 30")
            }
        }
    }
}

@Composable
private fun BMIRow(label: String, range: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(range, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
