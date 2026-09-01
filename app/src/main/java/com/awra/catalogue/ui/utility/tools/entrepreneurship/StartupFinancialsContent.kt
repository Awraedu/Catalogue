package com.awra.catalogue.ui.utility.tools.entrepreneurship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun StartupFinancialsContent(onClick: (UtilityItemData) -> Unit) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        BreakEvenCalculator()
        ROICalculator()
        
        Text(
            "Note: These are foundational metrics for evaluating startup feasibility.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BreakEvenCalculator() {
    var fixedCosts by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var unitCost by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Break-Even Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = fixedCosts,
                onValueChange = { fixedCosts = it },
                label = { Text("Total Fixed Costs ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                    label = { Text("Price per Unit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = unitCost,
                    onValueChange = { unitCost = it },
                    label = { Text("Cost per Unit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            
            val fc = fixedCosts.toDoubleOrNull() ?: 0.0
            val up = unitPrice.toDoubleOrNull() ?: 0.0
            val uc = unitCost.toDoubleOrNull() ?: 0.0
            val margin = up - uc
            val result = if (margin > 0) (fc / margin).toInt() else 0

            Spacer(Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Break-Even Units:", style = MaterialTheme.typography.labelMedium)
                    Text("$result Units", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text("You need to sell $result units to cover all costs.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ROICalculator() {
    var gain by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Return on Investment (ROI)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = gain,
                onValueChange = { gain = it },
                label = { Text("Gain from Investment ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost of Investment ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            val g = gain.toDoubleOrNull() ?: 0.0
            val c = cost.toDoubleOrNull() ?: 0.0
            val roi = if (c > 0) ((g - c) / c) * 100 else 0.0

            Spacer(Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ROI Percentage:", style = MaterialTheme.typography.labelMedium)
                    Text("${"%.2f".format(roi)}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text("Efficiency of an investment relative to its cost.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
