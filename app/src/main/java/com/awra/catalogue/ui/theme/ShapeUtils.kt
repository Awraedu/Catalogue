package com.awra.catalogue.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.awra.catalogue.data.model.ExpressiveShape

data class SubjectColors(val container: Color, val content: Color)

@Composable
fun getSubjectAdaptiveColors(subjectId: Long): SubjectColors {
    return when (subjectId.toInt() % 6) {
        0 -> SubjectColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        1 -> SubjectColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        2 -> SubjectColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        3 -> SubjectColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        4 -> SubjectColors(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurfaceVariant)
        else -> SubjectColors(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveShape.toShape(): Shape {
    return when (this) {
        ExpressiveShape.CIRCLE -> MaterialShapes.Circle
        ExpressiveShape.OVAL -> MaterialShapes.Oval
        ExpressiveShape.PILL -> MaterialShapes.Pill
        ExpressiveShape.SEMICIRCLE -> MaterialShapes.SemiCircle
        ExpressiveShape.ARCH -> MaterialShapes.Arch
        ExpressiveShape.BUN -> MaterialShapes.Bun
        ExpressiveShape.FAN -> MaterialShapes.Fan
        ExpressiveShape.HEART -> MaterialShapes.Heart
        ExpressiveShape.COOKIE_4 -> MaterialShapes.Cookie4Sided
        ExpressiveShape.COOKIE_6 -> MaterialShapes.Cookie6Sided
        ExpressiveShape.COOKIE_7 -> MaterialShapes.Cookie7Sided
        ExpressiveShape.COOKIE_9 -> MaterialShapes.Cookie9Sided
        ExpressiveShape.COOKIE_12 -> MaterialShapes.Cookie12Sided
        ExpressiveShape.CLOVER_4 -> MaterialShapes.Clover4Leaf
        ExpressiveShape.CLOVER_8 -> MaterialShapes.Clover8Leaf
        ExpressiveShape.PUFFY -> MaterialShapes.Puffy
        ExpressiveShape.SOFT_BOOM -> MaterialShapes.SoftBoom
        ExpressiveShape.SOFT_BURST -> MaterialShapes.SoftBurst
        ExpressiveShape.GHOST -> MaterialShapes.Ghostish
    }.toShape()
}
