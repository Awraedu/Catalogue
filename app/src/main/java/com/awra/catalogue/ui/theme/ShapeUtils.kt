package com.awra.catalogue.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
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

fun getSubjectBackgroundImage(subjectName: String): Int {
    return when {
        subjectName.contains("Biology", ignoreCase = true) || 
        subjectName.contains("English II", ignoreCase = true) || 
        subjectName.contains("Entrepreneurship", ignoreCase = true) -> com.awra.catalogue.R.drawable.green
        
        subjectName.contains("Psychology", ignoreCase = true) || 
        subjectName.contains("Emerging", ignoreCase = true) || 
        subjectName.contains("Natural Math", ignoreCase = true) || 
        subjectName.contains("Social Math", ignoreCase = true) -> com.awra.catalogue.R.drawable.red
        
        subjectName.contains("Logic", ignoreCase = true) || 
        subjectName.contains("Global Trends", ignoreCase = true) || 
        subjectName.contains("History", ignoreCase = true) -> com.awra.catalogue.R.drawable.black
        
        subjectName.contains("Sociology", ignoreCase = true) || 
        subjectName.contains("Inclusiveness", ignoreCase = true) || 
        subjectName.contains("English I", ignoreCase = true) || 
        subjectName.contains("Chemistry", ignoreCase = true) -> com.awra.catalogue.R.drawable.purple
        
        subjectName.contains("Anthropology", ignoreCase = true) || 
        subjectName.contains("Fitness", ignoreCase = true) || 
        subjectName.contains("Geography", ignoreCase = true) -> com.awra.catalogue.R.drawable.white
        
        subjectName.contains("Physics", ignoreCase = true) -> com.awra.catalogue.R.drawable.blue
        subjectName.contains("Economics", ignoreCase = true) -> com.awra.catalogue.R.drawable.yellow
        subjectName.contains("Civics", ignoreCase = true) -> com.awra.catalogue.R.drawable.brown

        else -> com.awra.catalogue.R.drawable.blue
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

fun getSubjectIcon(name: String): ImageVector {
    return when {
        name.contains("English", ignoreCase = true) -> Icons.AutoMirrored.Filled.MenuBook
        name.contains("Physics", ignoreCase = true) -> Icons.Default.Science
        name.contains("Psychology", ignoreCase = true) -> Icons.Default.Psychology
        name.contains("Mathematics", ignoreCase = true) -> Icons.Default.Calculate
        name.contains("Logic", ignoreCase = true) -> Icons.Default.Lightbulb
        name.contains("Geography", ignoreCase = true) -> Icons.Default.Public
        name.contains("Anthropol", ignoreCase = true) -> Icons.Default.Groups
        name.contains("Emerging", ignoreCase = true) -> Icons.Default.Memory
        name.contains("Civics", ignoreCase = true) -> Icons.Default.Gavel
        name.contains("Fitness", ignoreCase = true) -> Icons.Default.FitnessCenter
        name.contains("Global", ignoreCase = true) -> Icons.Default.Language
        name.contains("Chemistry", ignoreCase = true) -> Icons.Default.Biotech
        name.contains("Biology", ignoreCase = true) -> Icons.Default.PestControl
        name.contains("Economics", ignoreCase = true) -> Icons.AutoMirrored.Filled.TrendingUp
        name.contains("Entreprene", ignoreCase = true) -> Icons.Default.BusinessCenter
        else -> Icons.Default.Book
    }
}
