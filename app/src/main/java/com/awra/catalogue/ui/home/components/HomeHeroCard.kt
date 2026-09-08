package com.awra.catalogue.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeHeroCard(
    modifier: Modifier = Modifier
) {
    // Current StudyAnalyticsCard has a size of 178dp + 16dp vertical padding (approx 194dp total)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = ContinuousRoundedRectangle(32.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        // Blank for now as requested
        Box(modifier = Modifier.fillMaxSize())
    }
}
