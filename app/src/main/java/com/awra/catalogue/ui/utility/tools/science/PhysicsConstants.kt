package com.awra.catalogue.ui.utility.tools.science

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun PhysicsConstantsContent(onClick: (UtilityItemData) -> Unit) {
    val constants = listOf(
        UtilityItemData(
            "Gravity (g)", 
            "9.81 m/s²", 
            "Standard acceleration due to gravity on Earth's surface.",
            listOf("Calculating the weight of an object (W = mg)"),
            "Used in construction and aviation."
        ),
        UtilityItemData(
            "Speed of Light (c)", 
            "3.00 x 10⁸ m/s", 
            "Universal physical constant important in many areas of physics.",
            listOf("E = mc²"),
            "Fundamental limit of information speed."
        )
    )
    UtilityList(constants, onClick)
}
