package com.awra.catalogue.ui.utility.tools.physics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun WavesOpticsContent(onClick: (UtilityItemData) -> Unit) {
    val items = listOf(
        UtilityItemData(
            "Wave Properties",
            "v = fλ, T = 1/f",
            "Fundamental wave speed equation relating velocity, frequency, and wavelength.",
            listOf("Calculating the frequency of a sound wave", "Tuning radio stations"),
            "Telecommunications and acoustics."
        ),
        UtilityItemData(
            "Refraction",
            "n = c/v, n₁sinθ₁ = n₂sinθ₂",
            "Snell's law and the index of refraction.",
            listOf("Why a straw looks bent in water", "Designing eye glasses"),
            "Optics and lens manufacturing."
        ),
        UtilityItemData(
            "Geometric Optics",
            "1/f = 1/v + 1/u, M = -v/u",
            "Mirror and lens equations for calculating image position and magnification.",
            listOf("Magnifying glass focal point", "Microscope design"),
            "Photography and medical imaging."
        ),
        UtilityItemData(
            "Wave Interference",
            "d sinθ = mλ",
            "Double-slit interference pattern and constructive/destructive interference.",
            listOf("Anti-reflective coatings", "Laser patterns"),
            "Spectroscopy and nano-technology."
        )
    )
    UtilityList(items, onClick)
}
