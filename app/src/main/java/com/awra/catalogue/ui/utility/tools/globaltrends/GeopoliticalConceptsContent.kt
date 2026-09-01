package com.awra.catalogue.ui.utility.tools.globaltrends

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun GeopoliticalConceptsContent(onClick: (UtilityItemData) -> Unit) {
    val concepts = listOf(
        UtilityItemData(
            "Hard Power",
            "Coercive military and economic force",
            "The use of military and economic means to influence the behavior or interests of other political bodies.",
            listOf("Sanctions", "Military intervention", "Trade wars"),
            "Classical approach to international relations."
        ),
        UtilityItemData(
            "Soft Power",
            "Influence through attraction",
            "The ability to shape the preferences of others through appeal and attraction rather than coercion or payment.",
            listOf("Culture", "Political values", "Foreign policy"),
            "Increases a nation's global standing without force."
        ),
        UtilityItemData(
            "Unipolar System",
            "Single dominant superpower",
            "A distribution of power in which one state exercises most of the cultural, economic, and military influence.",
            listOf("Post-Cold War USA hegemony"),
            "Often leads to relative stability but also resentment."
        ),
        UtilityItemData(
            "Bipolar System",
            "Two dominant power centers",
            "A system of world order in which the majority of global economic, military and cultural influence is held between two states.",
            listOf("Cold War: USA vs USSR"),
            "Characterized by spheres of influence and proxy wars."
        ),
        UtilityItemData(
            "Multipolar System",
            "Multiple centers of power",
            "A distribution of power in which more than two nation-states have nearly equal amounts of military, cultural, and economic influence.",
            listOf("Pre-WWI Europe", "Emerging modern world order"),
            "Often considered more complex and potentially unstable."
        ),
        UtilityItemData(
            "Diplomatic Immunity",
            "Legal protection for diplomats",
            "A form of legal immunity that ensures diplomats are given safe passage and are considered not susceptible to lawsuit or prosecution under the host country's laws.",
            listOf("Vienna Convention on Diplomatic Relations"),
            "Crucial for maintaining open channels of communication."
        )
    )
    UtilityList(concepts, onClick)
}
