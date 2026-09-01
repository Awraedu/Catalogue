package com.awra.catalogue.ui.utility.tools.math

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun GeometryFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData(
            "Circle",
            "Area = πr², Circumference = 2πr",
            "Properties of a circle where r is the radius.",
            listOf("For r = 7, Area ≈ 153.94"),
            "Used in circular motions and structural design."
        ),
        UtilityItemData(
            "Rectangle",
            "Area = L * W, Perimeter = 2(L + W)",
            "L is length and W is width.",
            listOf("L=10, W=5, Area=50"),
            "Fundamental for land measurement."
        ),
        UtilityItemData(
            "Sphere",
            "Volume = (4/3)πr³, Surface Area = 4πr²",
            "Properties of a perfectly round ball.",
            listOf("Calculating volume of a basketball"),
            "Used in fluid dynamics."
        ),
        UtilityItemData(
            "Triangle",
            "Area = ½ * base * height",
            "Standard area formula for any triangle.",
            listOf("Base=4, Height=3, Area=6"),
            "Used in triangulation and architecture."
        )
    )
    UtilityList(formulas, onClick)
}
