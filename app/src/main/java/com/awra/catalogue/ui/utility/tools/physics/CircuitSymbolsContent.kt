package com.awra.catalogue.ui.utility.tools.physics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun CircuitSymbolsContent(onClick: (UtilityItemData) -> Unit) {
    val items = listOf(
        CircuitSymbolData(
            "Resistor",
            Icons.Default.HorizontalRule,
            UtilityItemData(
                "Resistor",
                "Zig-zag line or Rectangle",
                "Component that resists the flow of electric current.",
                listOf("Current limitation in LED circuits"),
                "All electronic devices."
            )
        ),
        CircuitSymbolData(
            "Capacitor",
            Icons.Default.Pause,
            UtilityItemData(
                "Capacitor",
                "Two parallel lines",
                "Stores energy in an electric field between two plates.",
                listOf("Filtering noise in power supplies", "Energy storage in flashes"),
                "Electronic filters and timers."
            )
        ),
        CircuitSymbolData(
            "Inductor",
            Icons.Default.Loop,
            UtilityItemData(
                "Inductor",
                "Series of loops (coil)",
                "Stores energy in a magnetic field when current flows through it.",
                listOf("Radio tuning circuits", "Transformers"),
                "Power conversion and signal processing."
            )
        ),
        CircuitSymbolData(
            "Diode",
            Icons.Default.PlayArrow,
            UtilityItemData(
                "Diode",
                "Triangle pointing to a vertical line",
                "Allows current to flow in one direction only.",
                listOf("Rectifiers converting AC to DC"),
                "Power supplies and protection circuits."
            )
        ),
        CircuitSymbolData(
            "Transistor",
            Icons.Default.SettingsInputComponent,
            UtilityItemData(
                "Transistor",
                "Three terminals (Base, Collector, Emitter)",
                "Used for switching and amplification of signals.",
                listOf("CPU logic gates", "Audio amplifiers"),
                "Modern digital computing."
            )
        ),
        CircuitSymbolData(
            "Battery",
            Icons.Default.BatteryFull,
            UtilityItemData(
                "Battery",
                "Long and short parallel lines",
                "Source of electrical energy (DC).",
                listOf("Powering portable electronics"),
                "Energy storage."
            )
        ),
        CircuitSymbolData(
            "Ground",
            Icons.Default.ExpandMore,
            UtilityItemData(
                "Ground",
                "Horizontal lines of decreasing length",
                "Zero potential reference point in a circuit.",
                listOf("Safety earthing", "Return path for current"),
                "Electrical safety."
            )
        ),
        CircuitSymbolData(
            "Switch",
            Icons.Default.ToggleOn,
            UtilityItemData(
                "Switch",
                "Gap in the line with a movable lever",
                "Allows or interrupts current flow.",
                listOf("Light switches", "Relays"),
                "Circuit control."
            )
        ),
        CircuitSymbolData(
            "LED",
            Icons.Default.Lightbulb,
            UtilityItemData(
                "LED",
                "Diode symbol with two arrows pointing away",
                "Light Emitting Diode.",
                listOf("Status indicators", "Display screens"),
                "Optoelectronics."
            )
        ),
        CircuitSymbolData(
            "Ammeter",
            Icons.Default.Circle,
            UtilityItemData(
                "Ammeter",
                "Circle with 'A' inside",
                "Instrument used to measure electric current.",
                listOf("Monitoring current in a branch"),
                "Measurement and testing."
            )
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            CircuitSymbolTile(item, onClick)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CircuitSymbolTile(item: CircuitSymbolData, onClick: (UtilityItemData) -> Unit) {
    Surface(
        onClick = { onClick(item.data) },
        shape = ContinuousRoundedRectangle(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class CircuitSymbolData(
    val name: String,
    val icon: ImageVector,
    val data: UtilityItemData
)
