package com.awra.catalogue.ui.utility.tools.chemistry

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun GasLawsContent(onClick: (UtilityItemData) -> Unit) {
    val laws = listOf(
        UtilityItemData(
            "Boyle's Law",
            "P₁V₁ = P₂V₂",
            "Pressure is inversely proportional to volume at constant temperature.",
            listOf("Squeezing a balloon reduces volume and increases pressure.", "Breathing: Diaphragm movement changes lung volume and pressure."),
            "Scuba diving (preventing decompression sickness) and lung function."
        ),
        UtilityItemData(
            "Charles's Law",
            "V₁/T₁ = V₂/T₂",
            "Volume is directly proportional to temperature at constant pressure.",
            listOf("Hot air balloons rise because heated air expands.", "Car tires appearing flat in winter due to cold air contracting."),
            "Aerospace engineering and meteorology."
        ),
        UtilityItemData(
            "Gay-Lussac's Law",
            "P₁/T₁ = P₂/T₂",
            "Pressure is directly proportional to temperature at constant volume.",
            listOf("Pressure cookers increase pressure as temperature rises.", "Aerosol cans can explode if heated due to high internal pressure."),
            "Safety engineering and cooking technology."
        ),
        UtilityItemData(
            "Ideal Gas Law",
            "PV = nRT",
            "Relates pressure, volume, moles, and temperature of an ideal gas.",
            listOf("Calculating the amount of gas in a cylinder.", "Determining molar mass of a volatile liquid."),
            "Chemical engineering and thermodynamic modeling."
        ),
        UtilityItemData(
            "Dalton's Law",
            "P_total = ΣP_i",
            "The total pressure of a gas mixture is the sum of the partial pressures of its components.",
            listOf("Calculating air composition at different altitudes.", "Preparing deep-sea diving gas mixtures."),
            "Atmospheric science and medical ventilation."
        )
    )

    UtilityList(laws, onClick)
}
