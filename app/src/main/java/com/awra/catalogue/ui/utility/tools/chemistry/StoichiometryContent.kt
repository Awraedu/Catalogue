package com.awra.catalogue.ui.utility.tools.chemistry

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun StoichiometryContent(onClick: (UtilityItemData) -> Unit) {
    val topics = listOf(
        UtilityItemData(
            "The Mole (n)",
            "n = m / M",
            "Molar mass conversion: moles = mass / molar mass. One mole contains Avogadro's number (6.022 x 10²³) of particles.",
            listOf("Finding moles in 18.02g of water (H₂O).", "Converting number of atoms to mass in grams."),
            "Fundamental basis for all quantitative chemical calculations."
        ),
        UtilityItemData(
            "Molarity (M)",
            "M = n / V",
            "Concentration of a solution: Molarity = moles of solute / liters of solution.",
            listOf("Preparing a 0.5M NaCl solution for an experiment.", "Calculating dilution: M₁V₁ = M₂V₂."),
            "Pharmacology, lab preparation, and industrial chemistry."
        ),
        UtilityItemData(
            "Limiting Reactant",
            "Mole Ratio Comparison",
            "The reactant that is completely consumed in a reaction, determining the maximum amount of product formed.",
            listOf("Identifying fuel vs oxygen in a combustion engine.", "Optimizing chemical synthesis in pharmaceutical manufacturing."),
            "Cost optimization and yield improvement in manufacturing."
        ),
        UtilityItemData(
            "Percent Yield",
            "% Yield = (Actual/Theoretical) x 100",
            "The efficiency of a chemical reaction, comparing experimental results to calculated expectations.",
            listOf("Evaluating laboratory performance.", "Assessing industrial waste and reaction efficiency."),
            "Quality control and process engineering."
        )
    )

    UtilityList(topics, onClick)
}
