package com.awra.catalogue.ui.utility.tools.logic

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun LogicOperatorsContent(onClick: (UtilityItemData) -> Unit) {
    val operators = listOf(
        UtilityItemData(
            "Conjunction (AND)", 
            "True only if both components are true.",
            "Represented by the symbol '∧'. P ∧ Q is true only if P is T and Q is T.",
            listOf("It is raining AND I have an umbrella."),
            "Used in computer programming and mathematical proofs."
        ),
        UtilityItemData(
            "Disjunction (OR)", 
            "True if at least one component is true.",
            "Represented by the symbol '∨'. P ∨ Q is false only if both P and Q are false.",
            listOf("I will study Logic OR I will sleep."),
            "The 'inclusive or' used in most formal logic systems."
        ),
        UtilityItemData(
            "Conditional (IF...THEN)", 
            "If the first part is true, the second must follow.",
            "Represented by '→'. P → Q is false only when the antecedent P is true and the consequent Q is false.",
            listOf("IF you study, THEN you will pass."),
            "The foundation of scientific hypotheses and logical arguments."
        )
    )
    UtilityList(operators, onClick)
}
