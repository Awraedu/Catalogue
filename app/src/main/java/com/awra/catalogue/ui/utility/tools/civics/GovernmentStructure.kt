package com.awra.catalogue.ui.utility.tools.civics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun GovernmentStructureContent(onClick: (UtilityItemData) -> Unit) {
    val structure = listOf(
        UtilityItemData(
            "The Federal Houses",
            "House of Peoples' Representatives and House of Federation.",
            "HPR is the highest authority of the federal government. HoF represents the nations, nationalities and peoples.",
            listOf("Legislative branch"),
            "Bicameral parliamentary system."
        ),
        UtilityItemData(
            "The Executive",
            "The Prime Minister and the Council of Ministers.",
            "The Prime Minister is the Chief Executive and Chairman of the Council of Ministers.",
            listOf("Executive branch"),
            "PM is elected from members of the HPR."
        ),
        UtilityItemData(
            "The Judiciary",
            "Independent court system.",
            "Judicial power, both at Federal and State levels, is vested in the courts.",
            listOf("Judicial branch"),
            "Supreme, High and First Instance Courts."
        )
    )
    UtilityList(structure, onClick)
}
