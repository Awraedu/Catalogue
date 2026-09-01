package com.awra.catalogue.ui.utility.tools.science

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun MathCalculusContent(onClick: (UtilityItemData) -> Unit) {
    val rules = listOf(
        UtilityItemData(
            "Power Rule", 
            "d/dx [x^n] = n * x^(n-1)", 
            "Used to differentiate polynomial functions.",
            listOf("Derivative of x³ is 3x²")
        ),
        UtilityItemData(
            "Chain Rule", 
            "dy/dx = (dy/du) * (du/dx)", 
            "Used to differentiate composite functions.",
            listOf("Derivative of (x²+1)³")
        )
    )
    UtilityList(rules, onClick)
}
