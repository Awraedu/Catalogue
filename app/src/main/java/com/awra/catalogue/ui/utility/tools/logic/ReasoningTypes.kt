package com.awra.catalogue.ui.utility.tools.logic

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun ReasoningTypesContent(onClick: (UtilityItemData) -> Unit) {
    val types = listOf(
        UtilityItemData(
            "Deductive Reasoning", 
            "Top-down logic. From general premises to specific conclusions.",
            "If the premises are true and the form is valid, the conclusion is NECESSARILY true (certainty).",
            listOf("All humans are mortal. Socrates is human. Therefore, Socrates is mortal."),
            "Used in mathematics and formal logic systems."
        ),
        UtilityItemData(
            "Inductive Reasoning", 
            "Bottom-up logic. From specific observations to general conclusions.",
            "The conclusion is PROBABLY true based on evidence, but not guaranteed.",
            listOf("Every swan I've seen is white. Therefore, all swans are probably white."),
            "The basis of scientific research and everyday decision-making."
        )
    )
    UtilityList(types, onClick)
}
