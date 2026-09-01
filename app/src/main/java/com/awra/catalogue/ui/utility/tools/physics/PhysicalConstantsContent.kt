package com.awra.catalogue.ui.utility.tools.physics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun PhysicalConstantsContent(onClick: (UtilityItemData) -> Unit) {
    val items = listOf(
        UtilityItemData(
            "Speed of Light",
            "c = 299,792,458 m/s",
            "Speed of electromagnetic waves in a vacuum.",
            listOf("Calculating time for light to reach Earth from Sun"),
            "Modern physics and cosmology."
        ),
        UtilityItemData(
            "Gravitational Constant",
            "G = 6.674 × 10⁻¹¹ N·m²/kg²",
            "Constant in Newton's law of universal gravitation.",
            listOf("Calculating the mass of Earth"),
            "Astrophysics and satellite navigation."
        ),
        UtilityItemData(
            "Planck Constant",
            "h = 6.626 × 10⁻³⁴ J·s",
            "Relates energy of a photon to its frequency (E=hf).",
            listOf("Quantum computing", "LED efficiency"),
            "Quantum mechanics."
        ),
        UtilityItemData(
            "Elementary Charge",
            "e = 1.602 × 10⁻¹⁹ C",
            "Magnitude of charge on a single electron or proton.",
            listOf("Calculating charge in a circuit"),
            "Particle physics and electronics."
        ),
        UtilityItemData(
            "Avogadro Constant",
            "N_A = 6.022 × 10²³ mol⁻¹",
            "Number of particles in one mole of a substance.",
            listOf("Converting mass to number of atoms"),
            "Chemistry and molecular biology."
        )
    )
    UtilityList(items, onClick)
}
