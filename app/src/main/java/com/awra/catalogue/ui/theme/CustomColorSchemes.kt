package com.awra.catalogue.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object CustomColorSchemes {
    // Shared Colors
    private val VibrantRed = Color(0xFFFF2D2D)
    private val AppleBlue = Color(0xFF0A84FF)
    private val NothingRed = Color(0xFFFF0000)

    val MidnightMono = darkColorScheme(
        primary = Color(0xFFF9F9F9),
        onPrimary = Color(0xFF5F5F5F),
        primaryContainer = Color(0xFFA0A0A0),
        onPrimaryContainer = Color(0xFF232323),
        secondary = Color(0xFFC6C6C6),
        onSecondary = Color(0xFF404040),
        secondaryContainer = Color(0xFF3B3B3B),
        onSecondaryContainer = Color(0xFFBFBFBF),
        surface = Color(0xFF0E0E0E),
        surfaceContainerLow = Color(0xFF131313),
        surfaceContainer = Color(0xFF191919),
        surfaceContainerHigh = Color(0xFF1F1F1F),
        surfaceContainerHighest = Color(0xFF262626),
        background = Color(0xFF0E0E0E),
        onSurface = Color(0xFFE5E5E5),
        onSurfaceVariant = Color(0xFFABABAB),
        outline = Color(0xFF757575),
        error = VibrantRed
    )

    val AppleIOS = darkColorScheme(
        primary = AppleBlue,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF003266),
        onPrimaryContainer = Color(0xFFD1E4FF),
        secondary = Color(0xFF30D158), // iOS Green
        onSecondary = Color.Black,
        tertiary = Color(0xFFFF375F), // iOS Pink
        surface = Color(0xFF000000), // Pure Black for OLED
        surfaceContainerLow = Color(0xFF1C1C1E),
        surfaceContainer = Color(0xFF2C2C2E),
        surfaceContainerHigh = Color(0xFF3A3A3C),
        surfaceContainerHighest = Color(0xFF48484A),
        background = Color(0xFF000000),
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFF8E8E93), // iOS Gray
        outline = Color(0xFF3A3A3C),
        error = Color(0xFFFF453A) // iOS Red
    )

    val NothingOS = darkColorScheme(
        primary = Color.White,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF333333),
        onPrimaryContainer = Color.White,
        secondary = NothingRed,
        onSecondary = Color.White,
        tertiary = Color(0xFF757575),
        surface = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF121212),
        surfaceContainer = Color(0xFF1E1E1E),
        surfaceContainerHigh = Color(0xFF2A2A2A),
        surfaceContainerHighest = Color(0xFF363636),
        background = Color(0xFF000000),
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFFB3B3B3),
        outline = Color(0xFF4D4D4D),
        error = NothingRed
    )

    val Schemes = mapOf(
        "DEFAULT" to null,
        "MIDNIGHT_MONO" to MidnightMono,
        "APPLE_IOS" to AppleIOS,
        "NOTHING_OS" to NothingOS
    )
}
