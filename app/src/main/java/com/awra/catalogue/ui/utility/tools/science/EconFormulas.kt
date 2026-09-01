package com.awra.catalogue.ui.utility.tools.science

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun EconFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData(
            "GDP (Expenditure)", 
            "GDP = C + I + G + (X - M)", 
            "Calculation of GDP using Consumption, Investment, Government spending, and Net Exports.",
            listOf("C=500, I=200, G=150, X=50, M=100 -> GDP = 800")
        ),
        UtilityItemData(
            "Elasticity of Demand", 
            "Ed = % change in Q / % change in P", 
            "Measures how responsive quantity demanded is to price changes.",
            listOf("Price rises 10%, Quantity falls 20% -> Ed = 2 (Elastic)")
        )
    )
    UtilityList(formulas, onClick)
}
