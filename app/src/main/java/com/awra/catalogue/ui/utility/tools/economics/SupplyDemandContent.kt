package com.awra.catalogue.ui.utility.tools.economics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun SupplyDemandContent(onClick: (UtilityItemData) -> Unit) {
    val items = listOf(
        UtilityItemData(
            "Law of Demand",
            "Price up, Quantity down",
            "There is an inverse relationship between price and quantity demanded, ceteris paribus.",
            listOf("Substitute effect", "Income effect"),
            "Determines consumer behavior and pricing strategy."
        ),
        UtilityItemData(
            "Law of Supply",
            "Price up, Quantity up",
            "There is a direct relationship between price and quantity supplied, ceteris paribus.",
            listOf("Profit incentive", "New entrants"),
            "Influences production levels and inventory management."
        ),
        UtilityItemData(
            "Market Equilibrium",
            "Supply = Demand",
            "The point where the quantity demanded by consumers equals the quantity supplied by producers.",
            listOf("Shortage (Demand > Supply)", "Surplus (Supply > Demand)"),
            "Establishes the market price and quantity."
        ),
        UtilityItemData(
            "Price Floor",
            "Minimum legal price",
            "A government-imposed minimum price. Only effective if set above equilibrium.",
            listOf("Minimum Wage", "Agricultural subsidies"),
            "Usually results in a surplus."
        ),
        UtilityItemData(
            "Price Ceiling",
            "Maximum legal price",
            "A government-imposed maximum price. Only effective if set below equilibrium.",
            listOf("Rent control", "Gasoline caps"),
            "Usually results in a shortage."
        )
    )
    UtilityList(items, onClick)
}
