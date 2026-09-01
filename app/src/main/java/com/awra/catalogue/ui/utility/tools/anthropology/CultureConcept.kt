package com.awra.catalogue.ui.utility.tools.anthropology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun CultureConceptContent(onClick: (UtilityItemData) -> Unit) {
    val concepts = listOf(
        UtilityItemData(
            "Holism", 
            "Studying the whole of the human condition.",
            "Anthropologists study past, present, and future; biology, society, language, and culture.",
            listOf("Combining biological and cultural data"),
            "Ensures a comprehensive understanding of human diversity."
        ),
        UtilityItemData(
            "Cultural Relativism", 
            "Understanding cultures on their own terms.",
            "Opposite of ethnocentrism. Behavior in one culture should not be judged by the standards of another.",
            listOf("Refraining from judging traditional practices as 'wrong' without context"),
            "Core ethical and methodological principle in anthropology."
        ),
        UtilityItemData(
            "Ethnocentrism", 
            "Judging other cultures by your own standards.",
            "The tendency to view one's own culture as superior.",
            listOf("Labeling unfamiliar food as 'gross'"),
            "A barrier to cross-cultural understanding."
        ),
        UtilityItemData(
            "Enculturation", 
            "The process of learning culture.",
            "How people learn their society's values, norms, and behaviors since childhood.",
            listOf("Learning language from parents", "Observing social cues"),
            "Ensures the continuity of culture through generations."
        )
    )
    UtilityList(concepts, onClick)
}
