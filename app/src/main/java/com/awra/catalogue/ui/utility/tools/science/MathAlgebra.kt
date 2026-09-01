package com.awra.catalogue.ui.utility.tools.science

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun MathAlgebraContent(onClick: (UtilityItemData) -> Unit) {
    val identities = listOf(
        UtilityItemData(
            "Difference of Squares", 
            "a² - b² = (a - b)(a + b)", 
            "A pattern used to factor expressions.",
            listOf("x² - 16 = (x - 4)(x + 4)")
        ),
        UtilityItemData(
            "Quadratic Formula", 
            "x = [-b ± sqrt(b² - 4ac)] / 2a", 
            "Used to find the roots of a quadratic equation ax² + bx + c = 0.",
            listOf("Solving x² - 5x + 6 = 0")
        )
    )
    UtilityList(identities, onClick)
}
