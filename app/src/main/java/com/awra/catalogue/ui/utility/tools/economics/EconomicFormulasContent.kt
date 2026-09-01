package com.awra.catalogue.ui.utility.tools.economics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun EconomicFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData(
            "GDP (Expenditure Approach)",
            "Y = C + I + G + (X - M)",
            "The total value of goods and services produced. C = Consumption, I = Investment, G = Govt Spending, X-M = Net Exports.",
            listOf("C: Household spending", "I: Business investment", "G: Infrastructure"),
            "Primary measure of a country's economic health."
        ),
        UtilityItemData(
            "Price Elasticity of Demand (PED)",
            "PED = % Change in Qty / % Change in Price",
            "Measures how responsive the quantity demanded of a good is to a change in its price.",
            listOf("> 1: Elastic", "< 1: Inelastic", "= 1: Unitary"),
            "Used by businesses to set prices and governments for tax policy."
        ),
        UtilityItemData(
            "Inflation Rate",
            "[(CPI This Year - CPI Last Year) / CPI Last Year] x 100",
            "The percentage increase in the general price level of goods and services over a period.",
            listOf("CPI: Consumer Price Index"),
            "Crucial for monetary policy and cost-of-living adjustments."
        ),
        UtilityItemData(
            "Unemployment Rate",
            "(Unemployed / Labor Force) x 100",
            "The percentage of the labor force that is jobless and actively seeking work.",
            listOf("Frictional", "Structural", "Cyclical"),
            "Key indicator of economic performance and social well-being."
        ),
        UtilityItemData(
            "Marginal Cost (MC)",
            "MC = Change in Total Cost / Change in Quantity",
            "The cost added by producing one additional unit of a product or service.",
            listOf("Profit Maximization: MR = MC"),
            "Determines the optimal production level for a firm."
        )
    )
    UtilityList(formulas, onClick)
}
