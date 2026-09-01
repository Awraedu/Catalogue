package com.awra.catalogue.ui.utility.tools.anthropology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun EvolutionStagesContent(onClick: (UtilityItemData) -> Unit) {
    val stages = listOf(
        UtilityItemData(
            "Australopithecus", 
            "Early hominins from Africa (4-2 million years ago).",
            "Key species like A. afarensis (Lucy). Primarily bipedal but still spent time in trees.",
            listOf("Bipedalism", "Small brain size (~400cc)"),
            "Foundation of human bipedal evolution."
        ),
        UtilityItemData(
            "Homo Habilis", 
            "The 'Handy Man' (2.4-1.4 million years ago).",
            "First species associated with stone tool use (Oldowan tools). Larger brain than Australopithecus.",
            listOf("Tool making", "Brain size increase (~600-700cc)"),
            "Emergence of technological intelligence."
        ),
        UtilityItemData(
            "Homo Erectus", 
            "The first to leave Africa (1.9 million - 140,000 years ago).",
            "Discovered fire and used Acheulean tools (hand axes). Tall, lean proportions similar to modern humans.",
            listOf("Use of fire", "Long-distance migration", "Complex tool making"),
            "Adaptation to diverse global environments."
        ),
        UtilityItemData(
            "Homo Sapiens", 
            "Modern humans (300,000 years ago to present).",
            "Characterized by complex language, symbolic art, and large brain size.",
            listOf("Symbolic thought", "Agriculture", "Complex social structures"),
            "Global dominance and cultural diversification."
        )
    )
    UtilityList(stages, onClick)
}
