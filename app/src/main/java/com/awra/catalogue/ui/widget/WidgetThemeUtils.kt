package com.awra.catalogue.ui.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.unit.ColorProvider

data class GlanceSubjectColorProviders(
    val container: ColorProvider,
    val content: ColorProvider
)

@Composable
fun getGlanceSubjectColors(subjectId: Long): GlanceSubjectColorProviders {
    return when ((subjectId.toInt() and 0x7FFFFFFF) % 7) {
        0 -> GlanceSubjectColorProviders(GlanceTheme.colors.primaryContainer, GlanceTheme.colors.onPrimaryContainer)
        1 -> GlanceSubjectColorProviders(GlanceTheme.colors.secondaryContainer, GlanceTheme.colors.onSecondaryContainer)
        2 -> GlanceSubjectColorProviders(GlanceTheme.colors.tertiaryContainer, GlanceTheme.colors.onTertiaryContainer)
        3 -> GlanceSubjectColorProviders(GlanceTheme.colors.errorContainer, GlanceTheme.colors.onErrorContainer)
        4 -> GlanceSubjectColorProviders(GlanceTheme.colors.surfaceVariant, GlanceTheme.colors.onSurfaceVariant)
        5 -> GlanceSubjectColorProviders(GlanceTheme.colors.primaryContainer, GlanceTheme.colors.onPrimaryContainer)
        else -> GlanceSubjectColorProviders(GlanceTheme.colors.tertiaryContainer, GlanceTheme.colors.onTertiaryContainer)
    }
}
