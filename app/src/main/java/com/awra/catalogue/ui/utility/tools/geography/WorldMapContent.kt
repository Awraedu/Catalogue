package com.awra.catalogue.ui.utility.tools.geography

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import com.awra.catalogue.ui.utility.UtilityItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

@Composable
fun WorldMapContent(onCountryClick: (UtilityItemData) -> Unit) {
    val context = LocalContext.current
    var countries by remember { mutableStateOf<List<CountryData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCountryIso by remember { mutableStateOf<String?>(null) }

    // Transformation state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(Unit) {
        countries = withContext(Dispatchers.IO) {
            parseSvgMap(context.assets.open("world.svg"))
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Tap a country to explore",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(0.5f, 60f)
                            val actualZoom = newScale / scale
                            
                            // Zoom to pivot point (centroid)
                            // offset = centroid - (centroid - offset) * zoom + pan
                            offset = (centroid - (centroid - offset) * actualZoom) + pan
                            scale = newScale
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            // Invert the transformation to find the coordinate in SVG space
                            // Since transformOrigin is (0,0), screen = local * scale + offset
                            // local = (screen - offset) / scale
                            val actualTapX = (tapOffset.x - offset.x) / scale
                            val actualTapY = (tapOffset.y - offset.y) / scale
                            
                            val clicked = countries.find { country ->
                                country.paths.any { path ->
                                    val bounds = RectF()
                                    path.computeBounds(bounds, true)
                                    
                                    if (bounds.contains(actualTapX, actualTapY)) {
                                        val region = Region()
                                        region.setPath(path, Region(
                                            (bounds.left - 1).toInt(),
                                            (bounds.top - 1).toInt(),
                                            (bounds.right + 1).toInt(),
                                            (bounds.bottom + 1).toInt()
                                        ))
                                        region.contains(actualTapX.toInt(), actualTapY.toInt())
                                    } else false
                                }
                            }
                            
                            if (clicked != null) {
                                selectedCountryIso = clicked.isoCode
                                onCountryClick(
                                    UtilityItemData(
                                        title = clicked.name,
                                        shortDesc = "ISO: ${clicked.isoCode}",
                                        detailedExplanation = "A nation on the world map.",
                                        examples = emptyList(),
                                        realWorldUse = "Part of the global geography."
                                    )
                                )
                            }
                        }
                    }
            ) {
                val colorScheme = MaterialTheme.colorScheme
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y,
                            transformOrigin = TransformOrigin(0f, 0f)
                        )
                ) {
                    countries.forEach { country ->
                        val isSelected = country.isoCode == selectedCountryIso
                        val fillColor = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant
                        val strokeColor = if (isSelected) colorScheme.onPrimary else colorScheme.outline
                        val strokeWidth = if (isSelected) 0.8f else 0.2f

                        country.paths.forEach { path ->
                            drawPath(
                                path = path.asComposePath(),
                                color = fillColor
                            )
                            drawPath(
                                path = path.asComposePath(),
                                color = strokeColor,
                                style = Stroke(width = strokeWidth)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class CountryData(
    val isoCode: String,
    val name: String,
    val paths: List<Path>
)

private fun parseSvgMap(inputStream: java.io.InputStream): List<CountryData> {
    val countries = mutableListOf<CountryData>()
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(inputStream, "UTF-8")

    var eventType = parser.eventType
    val countryMap = mutableMapOf<String, MutableList<Path>>()
    val nameMap = mutableMapOf<String, String>()

    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
            val d = parser.getAttributeValue(null, "d")
            val id = parser.getAttributeValue(null, "id")
            val name = parser.getAttributeValue(null, "name")
            val className = parser.getAttributeValue(null, "class")

            val finalId = id ?: className ?: "Unknown"
            val finalName = name ?: className ?: "Unknown"

            if (d != null) {
                try {
                    val path = PathParser.createPathFromPathData(d)
                    if (path != null) {
                        countryMap.getOrPut(finalId) { mutableListOf() }.add(path)
                        nameMap[finalId] = finalName
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        eventType = parser.next()
    }

    countryMap.forEach { (id, paths) ->
        countries.add(CountryData(id, nameMap[id] ?: id, paths))
    }

    return countries
}
