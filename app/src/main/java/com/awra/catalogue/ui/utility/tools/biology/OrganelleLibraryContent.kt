package com.awra.catalogue.ui.utility.tools.biology

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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.awra.catalogue.R
import com.awra.catalogue.ui.utility.UtilityItemData
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun OrganelleLibraryContent(onClick: (UtilityItemData) -> Unit) {
    val organelles = listOf(
        OrganelleData(
            "Nucleus",
            Icons.Default.Adjust,
            UtilityItemData(
                "Nucleus",
                "The Control Center",
                "Houses the cell's genetic material (DNA) and coordinates cell activities like growth and reproduction.",
                listOf("Contains the nucleolus", "Surrounded by nuclear envelope"),
                "Analogous to the 'brain' of the cell."
            )
        ),
        OrganelleData(
            "Mitochondria",
            Icons.Default.Bolt,
            UtilityItemData(
                "Mitochondria",
                "The Powerhouse",
                "Responsible for cellular respiration and ATP (energy) production.",
                listOf("Has its own DNA", "Double-membrane structure"),
                "Crucial for muscles and energy-intensive organs."
            )
        ),
        OrganelleData(
            "Ribosomes",
            Icons.Default.Grain,
            UtilityItemData(
                "Ribosomes",
                "Protein Factories",
                "Synthesize proteins by translating messenger RNA.",
                listOf("Found free or attached to ER", "Made of RNA and protein"),
                "Essential for all cellular structures and enzymes."
            )
        ),
        OrganelleData(
            "ER",
            Icons.Default.Timeline,
            UtilityItemData(
                "Endoplasmic Reticulum",
                "Transport Network",
                "Rough ER (with ribosomes) handles protein folding; Smooth ER handles lipid synthesis.",
                listOf("Continuous with nuclear envelope", "Detoxifies chemicals"),
                "Liver cells have abundant Smooth ER for detox."
            )
        ),
        OrganelleData(
            "Golgi App.",
            R.drawable.ic_golgi_apparatus,
            UtilityItemData(
                "Golgi Apparatus",
                "Shipping Center",
                "Modifies, sorts, and packages proteins for secretion or delivery to organelles.",
                listOf("Stacked flattened sacs", "Forms lysosomes"),
                "Glandular cells have highly active Golgi."
            )
        ),
        OrganelleData(
            "Lysosomes",
            R.drawable.ic_lysosome,
            UtilityItemData(
                "Lysosomes",
                "Waste Disposal",
                "Contain digestive enzymes to break down waste materials and cellular debris.",
                listOf("Highly acidic interior", "Break down captured bacteria"),
                "Crucial in white blood cells for immune response."
            )
        ),
        OrganelleData(
            "Vacuole",
            Icons.Default.RadioButtonUnchecked,
            UtilityItemData(
                "Vacuole",
                "Storage Tank",
                "Stores nutrients, water, and waste. Large central vacuole in plants provides structural support.",
                listOf("Stores cell sap", "Maintains turgor pressure in plants"),
                "Prevents plants from wilting."
            )
        ),
        OrganelleData(
            "Chloroplast",
            Icons.Default.Eco,
            UtilityItemData(
                "Chloroplast",
                "Sugar Factory",
                "Site of photosynthesis in plant cells, converting sunlight into chemical energy.",
                listOf("Contains chlorophyll", "Found in green plant tissues"),
                "Source of all food and oxygen on Earth."
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
        items(organelles) { organelle ->
            OrganelleTile(organelle, onClick)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OrganelleTile(organelle: OrganelleData, onClick: (UtilityItemData) -> Unit) {
    Surface(
        onClick = { onClick(organelle.data) },
        shape = ContinuousRoundedRectangle(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val iconPainter = when (val icon = organelle.icon) {
                is ImageVector -> rememberVectorPainter(icon)
                is Int -> painterResource(icon)
                else -> rememberVectorPainter(Icons.Default.Help)
            }
            Icon(
                painter = iconPainter,
                contentDescription = organelle.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = organelle.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class OrganelleData(
    val name: String,
    val icon: Any,
    val data: UtilityItemData
)
