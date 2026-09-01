package com.awra.catalogue.ui.utility.tools.geography

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun EthiopiaRegionsContent(onClick: (UtilityItemData) -> Unit) {
    val regions = listOf(
        UtilityItemData(
            "Addis Ababa",
            "Capital City",
            "The capital and largest city of Ethiopia, and the seat of the African Union.",
            listOf("Self-governing chartered city"),
            "Diplomatic hub of Africa."
        ),
        UtilityItemData(
            "Afar",
            "Northeast Ethiopia",
            "Home to the Danakil Depression, one of the lowest and hottest places on Earth.",
            listOf("Semera (Capital)", "Erta Ale Volcano"),
            "Critical for geothermal and archaeological research."
        ),
        UtilityItemData(
            "Amhara",
            "Northwest Ethiopia",
            "Rich in history, containing the Semien Mountains and Lake Tana, the source of the Blue Nile.",
            listOf("Bahir Dar (Capital)", "Gondar", "Lalibela"),
            "Major center for tourism and agriculture."
        ),
        UtilityItemData(
            "Oromia",
            "Central & Southern Ethiopia",
            "The largest and most populous region, surrounding the capital city.",
            listOf("Adama (Capital)", "Bale Mountains", "Coffee production"),
            "The heart of Ethiopia's coffee export and industry."
        ),
        UtilityItemData(
            "Somali",
            "Eastern Ethiopia",
            "The second-largest region, characterized by arid and semi-arid lowlands.",
            listOf("Jijiga (Capital)"),
            "Important for livestock and pastoralist economy."
        ),
        UtilityItemData(
            "Tigray",
            "Northern Ethiopia",
            "The seat of the ancient Aksumite Empire, with deep cultural and historical roots.",
            listOf("Mekelle (Capital)", "Aksum Obelisks"),
            "Key for historical and archaeological studies."
        ),
        UtilityItemData(
            "Sidama",
            "Southern Ethiopia",
            "Known for high-quality Sidamo coffee and distinct cultural heritage.",
            listOf("Hawassa (Capital)"),
            "Major coffee producing region."
        ),
        UtilityItemData(
            "South Ethiopia",
            "Southern Region",
            "Highly diverse with many ethnic groups and stunning landscapes.",
            listOf("Arba Minch (Capital)"),
            "Center of cultural and linguistic diversity."
        )
    )
    UtilityList(regions, onClick)
}
