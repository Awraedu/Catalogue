package com.awra.catalogue.ui.utility.tools.math

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun DerivativeTableContent(onClick: (UtilityItemData) -> Unit) {
    val rules = listOf(
        UtilityItemData(
            "Power Rule",
            "d/dx [x^n] = nx^(n-1)",
            "The derivative of a power of x is the exponent times x raised to the exponent minus one.",
            listOf("d/dx (x³) = 3x²", "d/dx (x) = 1"),
            "Fundamental rule for differentiating polynomials."
        ),
        UtilityItemData(
            "Product Rule",
            "d/dx [f(x)g(x)] = f'(x)g(x) + f(x)g'(x)",
            "Used when differentiating two functions multiplied together.",
            listOf("d/dx (x² sin x) = 2x sin x + x² cos x"),
            "Essential for complex function differentiation."
        ),
        UtilityItemData(
            "Quotient Rule",
            "d/dx [f(x)/g(x)] = [f'(x)g(x) - f(x)g'(x)] / [g(x)]²",
            "Used for differentiating the ratio of two functions.",
            listOf("d/dx (tan x) = sec² x"),
            "Used in physics for rate of change in ratios."
        ),
        UtilityItemData(
            "Chain Rule",
            "d/dx [f(g(x))] = f'(g(x)) * g'(x)",
            "Used to differentiate composite functions.",
            listOf("d/dx (sin(x²)) = cos(x²) * 2x"),
            "Crucial for nested physical models."
        ),
        UtilityItemData(
            "Exponential Function",
            "d/dx [e^x] = e^x",
            "The natural exponential function is its own derivative.",
            listOf("d/dx (e^(2x)) = 2e^(2x)"),
            "Used in modeling population growth and radioactive decay."
        )
    )
    UtilityList(rules, onClick)
}
