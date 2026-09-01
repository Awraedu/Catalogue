package com.awra.catalogue.ui.utility.tools.psychology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun BrainRegionsContent(onClick: (UtilityItemData) -> Unit) {
    val regions = listOf(
        UtilityItemData(
            "Frontal Lobe", 
            "Executive functions and voluntary movement.",
            "Contains the motor cortex and Broca's area. Crucial for personality and social behavior.",
            listOf("Planning a schedule", "Controlling impulses", "Speaking fluently"),
            "Damage often leads to radical changes in personality (e.g., Phineas Gage)."
        ),
        UtilityItemData(
            "Parietal Lobe", 
            "Processing somatosensory information.",
            "Responsible for integrating sensory input from various parts of the body.",
            listOf("Feeling the texture of a cloth", "Judging distance and spatial relationships"),
            "Helps in navigation and spatial orientation."
        ),
        UtilityItemData(
            "Temporal Lobe", 
            "Auditory processing and memory.",
            "Contains the primary auditory cortex and Wernicke's area. Vital for language comprehension.",
            listOf("Recognizing a song", "Understanding a conversation", "Storing new memories"),
            "Key region for language and long-term memory."
        ),
        UtilityItemData(
            "Occipital Lobe", 
            "Visual processing center.",
            "Located at the back of the brain. Processes color, form, and motion.",
            listOf("Seeing a bird in flight", "Reading text on a screen"),
            "Primary area for receiving and interpreting visual information."
        )
    )
    UtilityList(regions, onClick)
}
