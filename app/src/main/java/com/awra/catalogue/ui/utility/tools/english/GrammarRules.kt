package com.awra.catalogue.ui.utility.tools.english

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun GrammarRulesContent(onClick: (UtilityItemData) -> Unit) {
    val rules = listOf(
        UtilityItemData(
            "Present Simple", 
            "Used for habits, facts, and permanent states.",
            "Formed with the base verb. Add 's' for 3rd person singular.",
            listOf("I play football every Saturday.", "Water boils at 100 degrees Celsius."),
            "Used in scientific papers to describe facts."
        ),
        UtilityItemData(
            "Zero Conditional", 
            "Used for facts and general truths.",
            "If + present simple, ... present simple.",
            listOf("If you heat ice, it melts.", "If it rains, the grass gets wet."),
            "Essential for giving technical instructions."
        ),
        UtilityItemData(
            "Passive Voice", 
            "Used when the action is more important than the subject.",
            "Form: am/is/are + past participle.",
            listOf("The bridge was built in 1990.", "Mistakes were made."),
            "Used extensively in formal reports and news writing."
        )
    )
    UtilityList(rules, onClick)
}
