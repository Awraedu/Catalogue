package com.awra.catalogue.ui.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.shimmerEffect(
    shimmerColor: Color = Color.White.copy(alpha = 0.3f),
    durationMillis: Int = 2000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    drawWithContent {
        drawContent()
        val sweepWidth = size.width + size.height
        val offset = translateAnim * sweepWidth * 2 - sweepWidth

        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                shimmerColor,
                Color.Transparent,
            ),
            start = Offset(offset, offset),
            end = Offset(offset + 300f, offset + 300f)
        )
        drawRect(brush = brush)
    }
}
