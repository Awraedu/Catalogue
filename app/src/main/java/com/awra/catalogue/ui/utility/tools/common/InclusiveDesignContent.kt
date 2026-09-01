package com.awra.catalogue.ui.utility.tools.common

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun InclusiveDesignContent(onClick: (UtilityItemData) -> Unit) {
    val principles = listOf(
        UtilityItemData(
            "Equitable Use",
            "The design is useful to people with diverse abilities.",
            "Provides the same means of use for all users: identical whenever possible; equivalent when not.",
            listOf("Automatic doors", "Adjustable height desks"),
            "Fundamental for ensuring everyone can participate in society equally."
        ),
        UtilityItemData(
            "Flexibility in Use",
            "Accommodates a wide range of individual preferences.",
            "Provides choice in methods of use. For example, accommodating right- or left-handed access.",
            listOf("Adjustable keyboard height", "Ambidextrous tools"),
            "Allows for different ways of completing a task based on physical needs."
        ),
        UtilityItemData(
            "Simple and Intuitive Use",
            "Easy to understand, regardless of experience.",
            "Eliminates unnecessary complexity. Arranges information consistent with its importance.",
            listOf("Standardized symbols", "Clear instructions"),
            "Reduces cognitive load and helps people with varying literacy or language skills."
        ),
        UtilityItemData(
            "Perceptible Information",
            "Communicates effectively, regardless of sensory abilities.",
            "Uses different modes (pictorial, verbal, tactile) for redundant presentation of essential information.",
            listOf("Captioned videos", "Tactile markings on appliances"),
            "Ensures information is received even if one sense is impaired."
        ),
        UtilityItemData(
            "Tolerance for Error",
            "Minimizes hazards and the consequences of accidental actions.",
            "Arranges elements to minimize hazards. Provides warnings of hazards and errors.",
            listOf("Undo function in software", "Failsafe switches"),
            "Protects users from mistakes and makes products safer and more reliable."
        ),
        UtilityItemData(
            "Low Physical Effort",
            "Can be used efficiently and comfortably with minimum fatigue.",
            "Allows user to maintain a neutral body position. Minimizes repetitive actions and sustained physical effort.",
            listOf("Lever handles on doors", "Light-touch switches"),
            "Makes products usable for people with limited strength or stamina."
        ),
        UtilityItemData(
            "Size and Space for Approach and Use",
            "Appropriate size/space regardless of body size/posture.",
            "Provides a clear line of sight to important elements. Accommodates variations in grip and hand size.",
            listOf("Wide doorways", "Spacious restrooms"),
            "Ensures users of all physical builds and those using mobility aids can use the space."
        )
    )
    UtilityList(principles, onClick)
}
