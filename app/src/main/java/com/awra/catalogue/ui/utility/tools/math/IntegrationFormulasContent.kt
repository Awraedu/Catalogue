package com.awra.catalogue.ui.utility.tools.math

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun IntegrationFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData(
            "Power Rule",
            "∫ x^n dx = [x^(n+1) / (n+1)] + C",
            "Inverse of the power rule for derivatives. Valid for n ≠ -1.",
            listOf("∫ x² dx = (x³/3) + C"),
            "Used to find areas under curves."
        ),
        UtilityItemData(
            "Natural Log Rule",
            "∫ (1/x) dx = ln|x| + C",
            "Integration of the reciprocal function.",
            listOf("∫ (2/x) dx = 2 ln|x| + C"),
            "Essential for growth models."
        ),
        UtilityItemData(
            "Exponential Rule",
            "∫ e^x dx = e^x + C",
            "Integration of the natural exponential function.",
            listOf("∫ e^(3x) dx = (1/3)e^(3x) + C"),
            "Common in probability distributions."
        ),
        UtilityItemData(
            "Integration by Parts",
            "∫ u dv = uv - ∫ v du",
            "A technique based on the product rule for differentiation.",
            listOf("∫ x e^x dx = x e^x - e^x + C"),
            "Solves complex integral products."
        ),
        UtilityItemData(
            "Trig Integrals",
            "∫ sin x dx = -cos x + C",
            "Basic trigonometric integration.",
            listOf("∫ cos x dx = sin x + C"),
            "Used in wave mechanics."
        )
    )
    UtilityList(formulas, onClick)
}
