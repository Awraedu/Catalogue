package com.awra.catalogue.ui.utility.tools.anthropology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun ArcheologicalMethodsContent(onClick: (UtilityItemData) -> Unit) {
    val methods = listOf(
        UtilityItemData(
            "Surveying", 
            "Locating archaeological sites.",
            "The systematic search of an area to identify potential areas of past human activity.",
            listOf("Aerial photography", "Ground-penetrating radar"),
            "Essential first step before excavation."
        ),
        UtilityItemData(
            "Excavation", 
            "Systematic digging and recording.",
            "Careful removal of earth layers to recover artifacts and features in context.",
            listOf("Stratigraphy analysis", "Mapping finds"),
            "Primary source of archaeological data."
        ),
        UtilityItemData(
            "Dating Techniques", 
            "Determining the age of remains.",
            "Relative dating (stratigraphy) vs. Absolute dating (Radiocarbon, Dendrochronology).",
            listOf("Carbon-14 dating for organic materials"),
            "Placing archaeological finds in a chronological sequence."
        )
    )
    UtilityList(methods, onClick)
}
