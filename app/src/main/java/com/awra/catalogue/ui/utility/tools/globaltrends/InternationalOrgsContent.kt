package com.awra.catalogue.ui.utility.tools.globaltrends

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun InternationalOrgsContent(onClick: (UtilityItemData) -> Unit) {
    val orgs = listOf(
        UtilityItemData(
            "United Nations (UN)",
            "Intergovernmental organization",
            "Maintains international peace and security, develops friendly relations among nations, and achieves international cooperation.",
            listOf("Security Council", "General Assembly", "ICJ"),
            "Founded in 1945 to prevent another world-scale conflict."
        ),
        UtilityItemData(
            "African Union (AU)",
            "Continental union of 55 countries",
            "Promotes integrated, prosperous and peaceful Africa, driven by its own citizens and representing a dynamic force in the global arena.",
            listOf("Assembly", "Executive Council", "PAP"),
            "Headquartered in Addis Ababa, Ethiopia."
        ),
        UtilityItemData(
            "European Union (EU)",
            "Political and economic union",
            "A unique economic and political union between 27 European countries that together cover much of the continent.",
            listOf("European Commission", "Eurozone", "Schengen Area"),
            "Focused on single market and common currency."
        ),
        UtilityItemData(
            "NATO",
            "Intergovernmental military alliance",
            "The North Atlantic Treaty Organization is a system of collective defense whereby its member states agree to mutual defense.",
            listOf("Article 5", "Collective Security"),
            "Crucial for transatlantic security dynamics."
        ),
        UtilityItemData(
            "OPEC",
            "Intergovernmental Organization",
            "Coordinates and unifies the petroleum policies of its Member Countries and ensures the stabilization of oil markets.",
            listOf("Saudi Arabia", "United Arab Emirates", "Nigeria"),
            "Significant influence on global energy prices."
        )
    )
    UtilityList(orgs, onClick)
}
