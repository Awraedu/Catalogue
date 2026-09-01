package com.awra.catalogue.ui.utility.tools.math

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun LogarithmLawsContent(onClick: (UtilityItemData) -> Unit) {
    val laws = listOf(
        UtilityItemData(
            "Product Law",
            "log_b(xy) = log_b(x) + log_b(y)",
            "The log of a product is the sum of the logs.",
            listOf("log(20) = log(2) + log(10)"),
            "Simplifies complex multiplication."
        ),
        UtilityItemData(
            "Quotient Law",
            "log_b(x/y) = log_b(x) - log_b(y)",
            "The log of a quotient is the difference of the logs.",
            listOf("log(5) = log(10) - log(2)"),
            "Simplifies complex division."
        ),
        UtilityItemData(
            "Power Law",
            "log_b(x^k) = k * log_b(x)",
            "The log of a power is the exponent times the log of the base.",
            listOf("log(x²) = 2 log(x)"),
            "Used to solve exponential equations."
        ),
        UtilityItemData(
            "Change of Base",
            "log_b(x) = log_a(x) / log_a(b)",
            "Converts a logarithm to a different base.",
            listOf("log₂x = ln x / ln 2"),
            "Necessary for calculators that only have log₁₀ or ln."
        )
    )
    UtilityList(laws, onClick)
}
