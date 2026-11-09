package com.awra.catalogue.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

/**
 * Adaptive Ambient Mesh Gradient Background for Home Screen, Paywall & Subject Detail.
 *
 * Uses the strongest, richest full-opacity shade of [primaryColor] at the top & upper-middle
 * before transitioning smoothly down to clean white at the bottom.
 */
@Composable
fun AmbientMeshBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color? = null,
    animationProgress: Float = 1f
) {
    // Strongest, full-opacity vibrant color shade for top & mid rows
    val skyBlueTop = primaryColor ?: Color(0xFF4792FB)
    val oceanBlueMid = primaryColor ?: Color(0xFF3B82F6)
    val paleBlueLower = primaryColor?.copy(alpha = 0.45f) ?: Color(0xFFC7DFFC)
    val cleanWhiteBottom = Color(0xFFFFFFFF)

    val gradientPainter: Painter = remember(primaryColor) {
        try {
            // Instantiate Jetpack Compose MeshGradientPainter (3 rows x 3 columns)
            MeshGradientPainter(rows = 3, columns = 3, hasBicubicColor = true) {
                // Row 0 - Top Device Edge & Status Bar (Strongest Shade)
                setVertex(0, 0, Offset(0.0f, 0.0f), skyBlueTop)
                setVertex(0, 1, Offset(0.35f, 0.0f), skyBlueTop)
                setVertex(0, 2, Offset(0.70f, 0.0f), skyBlueTop)
                setVertex(0, 3, Offset(1.0f, 0.0f), skyBlueTop)

                // Row 1 - Upper-Mid Area (Strongest Shade)
                setVertex(1, 0, Offset(0.0f, 0.35f), oceanBlueMid)
                setVertex(1, 1, Offset(0.35f, 0.35f), oceanBlueMid)
                setVertex(1, 2, Offset(0.70f, 0.35f), oceanBlueMid)
                setVertex(1, 3, Offset(1.0f, 0.35f), oceanBlueMid)

                // Row 2 - Lower-Mid Area (Soft Fading Transition)
                setVertex(2, 0, Offset(0.0f, 0.65f), paleBlueLower)
                setVertex(2, 1, Offset(0.35f, 0.65f), paleBlueLower)
                setVertex(2, 2, Offset(0.70f, 0.65f), paleBlueLower)
                setVertex(2, 3, Offset(1.0f, 0.65f), paleBlueLower)

                // Row 3 - Bottom Section (Clean White)
                setVertex(3, 0, Offset(0.0f, 1.0f), cleanWhiteBottom)
                setVertex(3, 1, Offset(0.35f, 1.0f), cleanWhiteBottom)
                setVertex(3, 2, Offset(0.70f, 1.0f), cleanWhiteBottom)
                setVertex(3, 3, Offset(1.0f, 1.0f), cleanWhiteBottom)
            }
        } catch (_: Throwable) {
            MeshGradientFallbackPainter(
                skyBlueTop = skyBlueTop,
                oceanBlueMid = oceanBlueMid,
                paleBlueLower = paleBlueLower,
                cleanWhiteBottom = cleanWhiteBottom
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val progress = animationProgress.coerceIn(0f, 1f)
        val currentHeight = h * progress

        // Fill below with bottom color
        if (currentHeight < h) {
            drawRect(
                color = cleanWhiteBottom,
                topLeft = Offset(0f, currentHeight),
                size = Size(w, h - currentHeight)
            )
        }

        if (currentHeight > 0f) {
            drawContext.canvas.save()
            drawContext.canvas.clipRect(Rect(0f, 0f, w, currentHeight))
            with(gradientPainter) {
                draw(size)
            }
            drawContext.canvas.restore()
        }
    }
}

/**
 * Fallback Mesh Gradient Painter providing smooth multi-stop color blending.
 */
private class MeshGradientFallbackPainter(
    private val skyBlueTop: Color,
    private val oceanBlueMid: Color,
    private val paleBlueLower: Color,
    private val cleanWhiteBottom: Color
) : Painter() {

    override val intrinsicSize: Size
        get() = Size.Unspecified

    override fun DrawScope.onDraw() {
        val w = size.width
        val h = size.height

        if (w <= 0f || h <= 0f) return

        // Smooth vertical mesh gradient transition
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to skyBlueTop,
                    0.35f to oceanBlueMid,
                    0.65f to paleBlueLower,
                    1.00f to cleanWhiteBottom
                ),
                startY = 0f,
                endY = h
            )
        )

        // Soft radial bloom in center area
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    oceanBlueMid.copy(alpha = 0.35f),
                    Color.Transparent
                ),
                center = Offset(w * 0.50f, h * 0.40f),
                radius = w * 0.80f
            ),
            center = Offset(w * 0.50f, h * 0.40f),
            radius = w * 0.80f
        )
    }
}
