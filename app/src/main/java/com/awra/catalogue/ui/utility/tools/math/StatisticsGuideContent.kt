package com.awra.catalogue.ui.utility.tools.math

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun StatisticsGuideContent(onClick: (UtilityItemData) -> Unit) {
    val topics = listOf(
        UtilityItemData(
            "Mean",
            "x̄ = Σx_i / n",
            "The average of a data set.",
            listOf("Mean of {1, 2, 3, 4, 5} is 3"),
            "Measures central tendency."
        ),
        UtilityItemData(
            "Standard Deviation",
            "σ = √[Σ(x_i - μ)² / N]",
            "Measures the amount of variation or dispersion in a set of values.",
            listOf("A low σ means data is close to the mean."),
            "Used to quantify uncertainty."
        ),
        UtilityItemData(
            "Z-Score",
            "z = (x - μ) / σ",
            "The number of standard deviations a data point is from the mean.",
            listOf("Used in normal distribution analysis"),
            "Compares values from different scales."
        ),
        UtilityItemData(
            "Probability",
            "P(A) = n(A) / n(S)",
            "The likelihood of an event occurring.",
            listOf("Probability of rolling a 6 is 1/6"),
            "Used in risk assessment and insurance."
        )
    )
    UtilityList(topics, onClick)
}
