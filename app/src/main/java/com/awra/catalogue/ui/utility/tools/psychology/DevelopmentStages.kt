package com.awra.catalogue.ui.utility.tools.psychology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun DevelopmentStagesContent(onClick: (UtilityItemData) -> Unit) {
    val stages = listOf(
        UtilityItemData(
            "Piaget: Sensory-Motor", 
            "Birth to 2 years. Learning through senses and motor actions.",
            "Developing object permanence—the understanding that objects exist even when unseen.",
            listOf("Playing peek-a-boo", "Exploring objects by putting them in the mouth"),
            "Foundational stage for cognitive development."
        ),
        UtilityItemData(
            "Piaget: Pre-operational", 
            "2 to 7 years. Use of symbols and language.",
            "Characterized by egocentrism and a lack of logical reasoning.",
            listOf("Imaginary play", "Believing the moon follows them"),
            "Crucial for early childhood education and communication."
        ),
        UtilityItemData(
            "Erikson: Trust vs. Mistrust", 
            "Infancy (0-1 year). Developing a sense of security.",
            "Success leads to the virtue of hope. Failure leads to fear and anxiety.",
            listOf("Caregivers consistently meeting needs"),
            "Determines the child's future ability to form healthy relationships."
        )
    )
    UtilityList(stages, onClick)
}
