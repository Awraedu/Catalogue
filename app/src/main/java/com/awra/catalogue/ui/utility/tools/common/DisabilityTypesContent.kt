package com.awra.catalogue.ui.utility.tools.common

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun DisabilityTypesContent(onClick: (UtilityItemData) -> Unit) {
    val types = listOf(
        UtilityItemData(
            "Visual Impairment",
            "Partial or total loss of sight",
            "Includes low vision and total blindness. Can be congenital or acquired due to disease or injury.",
            listOf("Low vision", "Total blindness", "Color blindness"),
            "Essential for understanding the need for Braille and screen readers."
        ),
        UtilityItemData(
            "Hearing Impairment",
            "Partial or total loss of hearing",
            "Range from mild hearing loss to profound deafness. Communication often involves sign language or hearing aids.",
            listOf("Hard of hearing", "Deafness", "Tinnitus"),
            "Highlights the importance of captioning and sign language interpreters."
        ),
        UtilityItemData(
            "Physical Disability",
            "Limitations on physical functioning",
            "Affects mobility, manual dexterity, or stamina. May require the use of wheelchairs or other assistive devices.",
            listOf("Paralysis", "Amputation", "Cerebral palsy", "Muscular dystrophy"),
            "Drives the need for ramps, elevators, and accessible transportation."
        ),
        UtilityItemData(
            "Intellectual Disability",
            "Significant limitations in cognitive functioning",
            "Characterized by limitations in intellectual functioning and adaptive behavior, such as social and practical skills.",
            listOf("Down syndrome", "Fragile X syndrome"),
            "Requires specialized educational strategies and social support."
        ),
        UtilityItemData(
            "Learning Disability",
            "Neurological disorders affecting learning",
            "Interferes with the ability to store, process, or produce information. Not related to overall intelligence.",
            listOf("Dyslexia", "Dyscalculia", "ADHD"),
            "Crucial for implementing inclusive classroom adjustments."
        )
    )
    UtilityList(types, onClick)
}
