package com.awra.catalogue.ui.utility.tools.anthropology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun HumanVariationContent(onClick: (UtilityItemData) -> Unit) {
    val variation = listOf(
        UtilityItemData(
            "Biological Variation", 
            "Physical differences within the human species.",
            "Result of genetics and environmental adaptation.",
            listOf("Skin color (Melanin)", "Lactose tolerance", "Body proportions"),
            "Allows humans to survive in diverse climates (e.g., Bergmann's Rule)."
        ),
        UtilityItemData(
            "Skin Color Adaptation", 
            "Adaptation to UV radiation.",
            "Melanin protects against folate breakdown in high UV areas (equator). Vitamin D synthesis requires UV in low UV areas.",
            listOf("Dark skin in tropics", "Light skin in high latitudes"),
            "Classic example of natural selection in humans."
        ),
        UtilityItemData(
            "High-Altitude Adaptation", 
            "Physiological changes for low oxygen.",
            "Populations in the Andes and Himalayas have evolved different ways to process oxygen.",
            listOf("Increased chest size", "Higher hemoglobin levels"),
            "Survival in extreme environments."
        )
    )
    UtilityList(variation, onClick)
}
