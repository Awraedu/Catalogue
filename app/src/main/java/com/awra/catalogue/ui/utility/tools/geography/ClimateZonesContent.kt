package com.awra.catalogue.ui.utility.tools.geography

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun ClimateZonesContent(onClick: (UtilityItemData) -> Unit) {
    val zones = listOf(
        UtilityItemData(
            "Tropical Zone",
            "Hot and humid all year",
            "Located between the Tropic of Cancer and the Tropic of Capricorn. Characterized by high temperatures and significant rainfall.",
            listOf("Amazon Rainforest", "Congo Basin", "Southeast Asia"),
            "Critical for global biodiversity and carbon sequestration."
        ),
        UtilityItemData(
            "Dry (Arid) Zone",
            "Low precipitation, extreme temps",
            "Characterized by a deficiency of water and high evaporation rates. Includes deserts and steppes.",
            listOf("Sahara Desert", "Arabian Desert", "Australian Outback"),
            "Study of desertification and extreme survival adaptations."
        ),
        UtilityItemData(
            "Temperate Zone",
            "Moderate rainfall and temperatures",
            "Located between the subtropical and polar regions. Has four distinct seasons: spring, summer, autumn, and winter.",
            listOf("Western Europe", "Northern USA", "Eastern China"),
            "Home to a large percentage of the world's population."
        ),
        UtilityItemData(
            "Continental Zone",
            "Large temperature variations",
            "Found in the interior of large landmasses. Cold winters and hot summers with moderate precipitation.",
            listOf("Central Russia", "Great Plains of North America"),
            "Important for large-scale grain production."
        ),
        UtilityItemData(
            "Polar Zone",
            "Cold all year, ice and snow",
            "Characterized by a lack of warm summers. Every month has an average temperature of less than 10°C.",
            listOf("Antarctica", "Greenland", "Arctic Circle"),
            "Key area for studying global climate change and ice melt."
        )
    )
    UtilityList(zones, onClick)
}
