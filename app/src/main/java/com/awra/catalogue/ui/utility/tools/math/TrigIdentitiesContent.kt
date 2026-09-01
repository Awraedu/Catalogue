package com.awra.catalogue.ui.utility.tools.math

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun TrigIdentitiesContent(onClick: (UtilityItemData) -> Unit) {
    val identities = listOf(
        UtilityItemData(
            "Pythagorean Identities",
            "sin²θ + cos²θ = 1",
            "Relates the basic trigonometric functions of an angle.",
            listOf("1 + tan²θ = sec²θ", "1 + cot²θ = csc²θ"),
            "Foundation of trigonometry."
        ),
        UtilityItemData(
            "Double Angle Formulas",
            "sin(2θ) = 2 sinθ cosθ",
            "Expresses trig functions of twice an angle in terms of the original angle.",
            listOf("cos(2θ) = cos²θ - sin²θ"),
            "Simplifies calculations in Fourier analysis."
        ),
        UtilityItemData(
            "Angle Sum Formulas",
            "sin(α ± β) = sinα cosβ ± cosα sinβ",
            "Formulas for the sum and difference of two angles.",
            listOf("cos(α ± β) = cosα cosβ ∓ sinα sinβ"),
            "Used in signal processing."
        ),
        UtilityItemData(
            "Half Angle Formulas",
            "sin²(θ/2) = (1 - cosθ) / 2",
            "Relates trig functions of half an angle to the whole angle.",
            listOf("cos²(θ/2) = (1 + cosθ) / 2"),
            "Used in integral calculus."
        )
    )
    UtilityList(identities, onClick)
}
