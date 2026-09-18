package com.awra.catalogue.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object CustomColorSchemes {

    // --- BRAND EXTRAS (Used for subjects in Vibrant Orange theme) ---
    val BrandBlueLight = Color(0xFF007AFF)
    val BrandBlueDark = Color(0xFF0A84FF)
    val BrandYellowLight = Color(0xFFFFD600)
    val BrandYellowDark = Color(0xFFFFEA00)

    // --- MIDNIGHT MONO ---
    val MidnightMonoDark = darkColorScheme(
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
        error = Color(0xFFFFB4AB), // Starker Red-Peach
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF757575),
        outlineVariant = Color(0xFF484848),
        inverseSurface = Color(0xFFF9F9F9),
        inverseOnSurface = Color(0xFF323232),
        scrim = Color(0xFF000000)
    )

    val MidnightMonoLight = lightColorScheme(
        primary = Color(0xFF000000),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE0E0E0),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFF424242),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFEEEEEE),
        onSecondaryContainer = Color(0xFF212121),
        tertiary = Color(0xFF616161),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFD1D1D1),
        onTertiaryContainer = Color(0xFF000000),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1A1A1A),
        onSurfaceVariant = Color(0xFF474747),
        background = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1A1A1A),
        outline = Color(0xFF797979),
        outlineVariant = Color(0xFFC4C4C4),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F7F7),
        surfaceContainer = Color(0xFFF2F2F2),
        surfaceContainerHigh = Color(0xFFECECEC),
        surfaceContainerHighest = Color(0xFFE6E6E6),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )

    // --- FOREST SAGE ---
    val ForestSageDark = darkColorScheme(
        primary = Color(0xFFE8F5E9),
        onPrimary = Color(0xFF2E3B2E),
        primaryContainer = Color(0xFF7A8F7A),
        onPrimaryContainer = Color(0xFF0E130E),
        secondary = Color(0xFFC8D6C8),
        onSecondary = Color(0xFF3A453A),
        secondaryContainer = Color(0xFF333D33),
        onSecondaryContainer = Color(0xFFBDCDBD),
        tertiary = Color(0xFFA5D6A7),
        onTertiary = Color(0xFF00390A),
        tertiaryContainer = Color(0xFF1B5E20),
        onTertiaryContainer = Color(0xFFC8E6C9),
        surface = Color(0xFF0A0D0A),
        background = Color(0xFF0A0D0A),
        onSurface = Color(0xFFE1E8E1),
        onSurfaceVariant = Color(0xFFAAB2AA),
        outline = Color(0xFF6F776F),
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF111611),
        surfaceContainer = Color(0xFF181D18),
        surfaceContainerHigh = Color(0xFF1F241F),
        surfaceContainerHighest = Color(0xFF262B26),
        inverseSurface = Color(0xFFE1E8E1),
        inverseOnSurface = Color(0xFF2A312A),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )

    val ForestSageLight = lightColorScheme(
        primary = Color(0xFF386B41),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFB9F0BC),
        onPrimaryContainer = Color(0xFF002106),
        secondary = Color(0xFF52634F),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD5E8CF),
        onSecondaryContainer = Color(0xFF111F0F),
        tertiary = Color(0xFF386567),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFBCEBEB),
        onTertiaryContainer = Color(0xFF002021),
        surface = Color(0xFFF8FAF7),
        onSurface = Color(0xFF191C19),
        onSurfaceVariant = Color(0xFF424940),
        background = Color(0xFFF8FAF7),
        outline = Color(0xFF72796F),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF2F5F0),
        surfaceContainer = Color(0xFFEDF1EA),
        surfaceContainerHigh = Color(0xFFE7EBE4),
        surfaceContainerHighest = Color(0xFFE1E6DF),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )

    // --- EARTH BROWN ---
    val EarthBrownDark = darkColorScheme(
        primary = Color(0xFFF5F0EB),
        onPrimary = Color(0xFF3D352E),
        primaryContainer = Color(0xFF8A7B6D),
        onPrimaryContainer = Color(0xFF1A1613),
        secondary = Color(0xFFD6CEC8),
        onSecondary = Color(0xFF453D37),
        secondaryContainer = Color(0xFF3D3732),
        onSecondaryContainer = Color(0xFFCDC6BF),
        tertiary = Color(0xFFD2C4B4),
        onTertiary = Color(0xFF372F26),
        tertiaryContainer = Color(0xFF4F4539),
        onTertiaryContainer = Color(0xFFEEE0CF),
        surface = Color(0xFF0D0C0B),
        background = Color(0xFF0D0C0B),
        onSurface = Color(0xFFE8E2DD),
        onSurfaceVariant = Color(0xFFB2ABA5),
        outline = Color(0xFF777069),
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF161412),
        surfaceContainer = Color(0xFF1C1A18),
        surfaceContainerHigh = Color(0xFF22201E),
        surfaceContainerHighest = Color(0xFF282624),
        inverseSurface = Color(0xFFE8E2DD),
        inverseOnSurface = Color(0xFF312D2A),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )

    val EarthBrownLight = lightColorScheme(
        primary = Color(0xFF7A5900),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDF9E),
        onPrimaryContainer = Color(0xFF261A00),
        secondary = Color(0xFF6B5D3F),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF5E0BB),
        onSecondaryContainer = Color(0xFF241A06),
        tertiary = Color(0xFF4A605E),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFCDE5E3),
        onTertiaryContainer = Color(0xFF051F1C),
        surface = Color(0xFFFFFBFF),
        onSurface = Color(0xFF1E1B16),
        onSurfaceVariant = Color(0xFF4D4639),
        background = Color(0xFFFFFBFF),
        outline = Color(0xFF7E7667),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F3ED),
        surfaceContainer = Color(0xFFF1EDE6),
        surfaceContainerHigh = Color(0xFFEBE7E0),
        surfaceContainerHighest = Color(0xFFE5E1DA),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )

    // --- TWILIGHT LAVENDER ---
    val TwilightLavenderDark = darkColorScheme(
        primary = Color(0xFFF3E5F5),
        onPrimary = Color(0xFF3B2E3D),
        primaryContainer = Color(0xFF7B6D8A),
        onPrimaryContainer = Color(0xFF16131A),
        secondary = Color(0xFFD2C8D6),
        onSecondary = Color(0xFF403745),
        secondaryContainer = Color(0xFF38323D),
        onSecondaryContainer = Color(0xFFC9BFCD),
        tertiary = Color(0xFFD7C1D7),
        onTertiary = Color(0xFF3C2C3C),
        tertiaryContainer = Color(0xFF534353),
        onTertiaryContainer = Color(0xFFF4DDF4),
        surface = Color(0xFF0D0A0D),
        background = Color(0xFF0D0A0D),
        onSurface = Color(0xFFE7E1E8),
        onSurfaceVariant = Color(0xFFB2AAB2),
        outline = Color(0xFF776F77),
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF161216),
        surfaceContainer = Color(0xFF1C181C),
        surfaceContainerHigh = Color(0xFF221E22),
        surfaceContainerHighest = Color(0xFF282428),
        inverseSurface = Color(0xFFE7E1E8),
        inverseOnSurface = Color(0xFF312A31),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )

    val TwilightLavenderLight = lightColorScheme(
        primary = Color(0xFF6F4DA0),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFECDCFF),
        onPrimaryContainer = Color(0xFF280056),
        secondary = Color(0xFF645A70),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFEBDDF7),
        onSecondaryContainer = Color(0xFF20182A),
        tertiary = Color(0xFF7E525D),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD9E1),
        onTertiaryContainer = Color(0xFF31101A),
        surface = Color(0xFFFFF7FF),
        onSurface = Color(0xFF1D1A20),
        onSurfaceVariant = Color(0xFF4A454E),
        background = Color(0xFFFFF7FF),
        outline = Color(0xFF7B757F),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF8F2FA),
        surfaceContainer = Color(0xFFF2ECF4),
        surfaceContainerHigh = Color(0xFFECE6EE),
        surfaceContainerHighest = Color(0xFFE6E0E8),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )

    // --- VIBRANT ORANGE (Brand Theme) ---
    // A detailed, high-contrast theme using Orange as the core brand color,
    // with Indigo and Emerald as functional secondary/tertiary colors for tools like Periodic Tables.
    val VibrantOrangeDark = darkColorScheme(
        primary = Color(0xFFFF6D00), // Vibrant Orange
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF7A2E00),
        onPrimaryContainer = Color(0xFFFFDBCB),
        
        secondary = Color(0xFF7C3AED), // Vivid Violet/Indigo
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF4C1D95),
        onSecondaryContainer = Color(0xFFEDE9FE),
        
        tertiary = Color(0xFF059669), // Vivid Emerald
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF064E3B),
        onTertiaryContainer = Color(0xFFD1FAE5),
        
        surface = Color(0xFF000000), // Pure Black background
        background = Color(0xFF000000),
        onSurface = Color(0xFFF9FAFB),
        onSurfaceVariant = Color(0xFFD1D5DB),
        
        outline = Color(0xFF6B7280),
        outlineVariant = Color(0xFF374151),
        
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF09090B),
        surfaceContainer = Color(0xFF111113),
        surfaceContainerHigh = Color(0xFF18181B),
        surfaceContainerHighest = Color(0xFF27272A),
        
        inverseSurface = Color(0xFFF9FAFB),
        inverseOnSurface = Color(0xFF09090B),
        
        error = Color(0xFFEF4444),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF991B1B),
        onErrorContainer = Color(0xFFFEE2E2)
    )

    val VibrantOrangeLight = lightColorScheme(
        primary = Color(0xFFFF6D00),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFF7ED),
        onPrimaryContainer = Color(0xFF7C2D12),
        
        secondary = Color(0xFF7C3AED), // Vivid Violet/Indigo
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFEDE9FE),
        onSecondaryContainer = Color(0xFF4C1D95),
        
        tertiary = Color(0xFF059669), // Vivid Emerald
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFD1FAE5),
        onTertiaryContainer = Color(0xFF064E3B),
        
        surface = Color(0xFFFFFFFF), // Pure White background
        background = Color(0xFFFFFFFF),
        onSurface = Color(0xFF09090B),
        onSurfaceVariant = Color(0xFF4B5563),
        
        outline = Color(0xFF9CA3AF),
        outlineVariant = Color(0xFFF1F1F3),
        
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFAFAFB),
        surfaceContainer = Color(0xFFF7F7F9),
        surfaceContainerHigh = Color(0xFFF1F1F3),
        surfaceContainerHighest = Color(0xFFE5E7EB),
        
        error = Color(0xFFDC2626),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFEE2E2),
        onErrorContainer = Color(0xFF7F1D1D)
    )

    val Schemes = mapOf(
        "DEFAULT" to null,
        "MIDNIGHT_MONO" to (MidnightMonoLight to MidnightMonoDark),
        "FOREST_SAGE" to (ForestSageLight to ForestSageDark),
        "EARTH_BROWN" to (EarthBrownLight to EarthBrownDark),
        "TWILIGHT_LAVENDER" to (TwilightLavenderLight to TwilightLavenderDark),
        "VIBRANT_ORANGE" to (VibrantOrangeLight to VibrantOrangeDark)
    )

    fun getScheme(id: String, isDark: Boolean): ColorScheme? {
        val pair = Schemes[id] ?: return null
        return if (isDark) pair.second else pair.first
    }
}
