package com.awra.catalogue.ui.utility.tools.geography

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.awra.catalogue.ui.utility.UtilityItemData
import kotlin.math.sqrt

@Composable
fun EarthLayersContent(onClick: (UtilityItemData) -> Unit) {
    val density = LocalDensity.current

    // Define detailed layers data matching the visual style
    val layers = remember {
        listOf(
            EarthLayerData(
                "Atmosphere",
                "10,000 km",
                "The layer of gases surrounding Earth, extending far into space.",
                0.95f,
                Color(0xFF1B2631),
                Color.White.copy(alpha = 0.6f)
            ),
            EarthLayerData(
                "Crust",
                "5 - 70 km",
                "The outermost solid shell of a rocky planet.",
                0.80f,
                Color(0xFF4A0E0E),
                Color.White.copy(alpha = 0.8f)
            ),
            EarthLayerData(
                "Lithosphere",
                "100 km",
                "The rigid outer part of the earth, consisting of the crust and upper mantle.",
                0.70f,
                Color(0xFF7B241C),
                Color.White.copy(alpha = 0.9f)
            ),
            EarthLayerData(
                "Upper Mantle",
                "660 km",
                "Below the lithosphere, it is more fluid and allows for plate tectonics.",
                0.60f,
                Color(0xFFC0392B),
                Color.White
            ),
            EarthLayerData(
                "Lower Mantle",
                "2,230 km",
                "A solid but slowly flowing layer of hot rock.",
                0.50f,
                Color(0xFFE74C3C),
                Color.White
            ),
            EarthLayerData(
                "Outer Core",
                "2,200 km",
                "A liquid layer composed of iron and nickel, creating Earth's magnetic field.",
                0.40f,
                Color(0xFF7F8C8D),
                Color.White
            ),
            EarthLayerData(
                "Inner Core",
                "1,220 km",
                "A solid sphere of iron and nickel at the center of Earth.",
                0.25f,
                Color.White,
                Color(0xFF2C3E50)
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1218))
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val verticalOffset = with(density) { 60.dp.toPx() }
        val center = Offset(width / 2f, height + verticalOffset) // Shifted down

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y
                        val distance = sqrt(dx * dx + dy * dy)
                        
                        // Hit test from smallest to largest
                        val clickedLayer = layers.reversed().find { layer ->
                            val radiusPx = height * layer.radiusRatio
                            distance <= radiusPx
                        }

                        clickedLayer?.let {
                            onClick(
                                UtilityItemData(
                                    title = it.name,
                                    shortDesc = "Thickness: ${it.depth}",
                                    detailedExplanation = it.description,
                                    examples = emptyList(),
                                    realWorldUse = "Fundamental to Earth's geology and life."
                                )
                            )
                        }
                    }
                }
        ) {
            // Draw from largest to smallest to nest correctly
            layers.forEach { layer ->
                drawCircle(
                    color = layer.color,
                    radius = height * layer.radiusRatio,
                    center = center
                )
            }
        }

        // Labels positioned relative to the arcs
        layers.forEach { layer ->
            val nextRadiusRatio = layers.getOrNull(layers.indexOf(layer) + 1)?.radiusRatio ?: 0f
            val midRadiusRatio = (layer.radiusRatio + nextRadiusRatio) / 2f
            val labelRadius = height * midRadiusRatio
            val topOffset = with(density) { (height + verticalOffset - labelRadius).toDp() }
            
            Text(
                text = layer.name.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = layer.textColor,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = topOffset - 10.dp)
            )
        }
        
        Text(
            "Tap a layer to learn more",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp),
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EarthLayersPreview() {
    MaterialTheme {
        EarthLayersContent({})
    }
}

private data class EarthLayerData(
    val name: String,
    val depth: String,
    val description: String,
    val radiusRatio: Float,
    val color: Color,
    val textColor: Color
)
