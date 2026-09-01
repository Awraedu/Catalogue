package com.awra.catalogue.ui.utility.tools.logic

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun LogicalFallaciesContent(onClick: (UtilityItemData) -> Unit) {
    val fallacies = listOf(
        UtilityItemData(
            "Ad Hominem", 
            "Attacking the person instead of the argument.",
            "Character assassination used to distract from the actual reasoning.",
            listOf("You can't trust his climate policy because he didn't finish college."),
            "Common in political debates and toxic social media discussions."
        ),
        UtilityItemData(
            "Straw Man", 
            "Misrepresenting an argument to make it easier to attack.",
            "Creating a weak version of an opponent's position and then 'demolishing' it.",
            listOf("A: We should spend more on education. B: Why do you hate our military so much that you want to cut their budget?"),
            "Used to win arguments without actually engaging with the core issue."
        ),
        UtilityItemData(
            "Slippery Slope", 
            "Claiming one small step will lead to a chain of disastrous events.",
            "Assumes a chain reaction without providing evidence for the inevitability of each step.",
            listOf("If we let students use calculators, they will soon forget how to add, then they will lose all basic logic skills, and eventually, civilization will collapse."),
            "Often used to discourage policy changes through fear."
        )
    )
    UtilityList(fallacies, onClick)
}
