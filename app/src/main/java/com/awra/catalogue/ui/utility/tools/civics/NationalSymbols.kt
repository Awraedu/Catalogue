package com.awra.catalogue.ui.utility.tools.civics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun NationalSymbolsContent(onClick: (UtilityItemData) -> Unit) {
    val symbols = listOf(
        UtilityItemData(
            "The National Flag",
            "Green, Yellow and Red with an emblem.",
            "Green represents fertility/nature, Yellow represents hope/justice, and Red represents sacrifice for freedom.",
            listOf("Emblem in the center represents equality and unity."),
            "Adopted in its current form in 1996."
        ),
        UtilityItemData(
            "The National Emblem",
            "A blue circle with a star and rays.",
            "The blue represents peace. The star represents the bright future and equality of nations, nationalities and peoples.",
            listOf("Equality of all Ethiopians.")
        ),
        UtilityItemData(
            "The National Anthem",
            "Whedefit Gesgeshi Wouy Enat Ityopia.",
            "Adopted in 1992 following the fall of the Derg regime.",
            listOf("Symbol of national unity and pride.")
        )
    )
    UtilityList(symbols, onClick)
}
