package com.awra.catalogue.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object CustomColorSchemes {
    private val VibrantRed = Color(0xFFFF2D2D) // Apple-style vibrant red
    private val VibrantRedContainer = Color(0xFF93000A)
    private val OnVibrantRedContainer = Color(0xFFFFDAD6)

    val MidnightMono = darkColorScheme(
        primary = Color(0xFFF9F9F9),
        onPrimary = Color(0xFF5F5F5F),
        primaryContainer = Color(0xFFA0A0A0),
        onPrimaryContainer = Color(0xFF232323),
        inversePrimary = Color(0xFF5F5F5F),
        secondary = Color(0xFFC6C6C6),
        onSecondary = Color(0xFF404040),
        secondaryContainer = Color(0xFF3B3B3B),
        onSecondaryContainer = Color(0xFFBFBFBF),
        tertiary = Color(0xFFF9F9F9),
        onTertiary = Color(0xFF5F5F5F),
        tertiaryContainer = Color(0xFFEBEBEB),
        onTertiaryContainer = Color(0xFF575757),
        surface = Color(0xFF0E0E0E),
        surfaceDim = Color(0xFF0E0E0E),
        surfaceBright = Color(0xFF2C2C2C),
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF131313),
        surfaceContainer = Color(0xFF191919),
        surfaceContainerHigh = Color(0xFF1F1F1F),
        surfaceContainerHighest = Color(0xFF262626),
        surfaceVariant = Color(0xFF262626),
        onSurface = Color(0xFFE5E5E5),
        onSurfaceVariant = Color(0xFFABABAB),
        surfaceTint = Color(0xFFF9F9F9),
        background = Color(0xFF0E0E0E),
        onBackground = Color(0xFFE5E5E5),
        error = VibrantRed,
        onError = Color(0xFFFFFFFF),
        errorContainer = VibrantRedContainer,
        onErrorContainer = OnVibrantRedContainer,
        outline = Color(0xFF757575),
        outlineVariant = Color(0xFF484848),
        inverseSurface = Color(0xFFF9F9F9),
        inverseOnSurface = Color(0xFF323232),
        scrim = Color(0xFF000000)
    )

    val AppleIOS = darkColorScheme(
        primary = Color(0xFF0A84FF), // iOS System Blue
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF003266),
        onPrimaryContainer = Color(0xFFD1E4FF),
        secondary = Color(0xFF30D158), // iOS System Green
        onSecondary = Color(0xFF000000),
        tertiary = Color(0xFFFF375F), // iOS System Pink
        onTertiary = Color(0xFFFFFFFF),
        surface = Color(0xFF1C1C1E),
        surfaceVariant = Color(0xFF3A3A3C),
        background = Color(0xFF000000),
        onSurface = Color(0xFFFFFFFF),
        onSurfaceVariant = Color(0xFFEBEBF5),
        onBackground = Color(0xFFFFFFFF),
        error = Color(0xFFFF453A), // iOS System Red
        outline = Color(0xFF8E8E93)
    )

    val NothingOS = darkColorScheme(
        primary = Color(0xFFFFFFFF),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF333333),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFFFF0000), // Nothing Red
        onSecondary = Color(0xFFFFFFFF),
        surface = Color(0xFF121212),
        surfaceVariant = Color(0xFF1E1E1E),
        background = Color(0xFF000000),
        onSurface = Color(0xFFFFFFFF),
        onSurfaceVariant = Color(0xFFB3B3B3),
        onBackground = Color(0xFFFFFFFF),
        error = Color(0xFFFF0000),
        outline = Color(0xFF4D4D4D)
    )

    val Schemes = mapOf(
        "DEFAULT" to null,
        "MIDNIGHT_MONO" to MidnightMono,
        "APPLE_IOS" to AppleIOS,
        "NOTHING_OS" to NothingOS
    )
}
