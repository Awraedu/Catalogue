package com.awra.catalogue.ui.utility.tools.psychology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun DefenseMechanismsContent(onClick: (UtilityItemData) -> Unit) {
    val mechs = listOf(
        UtilityItemData(
            "Repression", 
            "Pushing threatening thoughts into the unconscious.",
            "The most basic defense mechanism. Forgetting a traumatic event.",
            listOf("A child abused by a parent having no memory of it later."),
            "Can lead to psychological distress if not addressed in therapy."
        ),
        UtilityItemData(
            "Projection", 
            "Attributing one's own unacceptable traits to others.",
            "A person who is dishonest suspicious of others' honesty.",
            listOf("Accusing your partner of cheating because you feel guilty about flirting."),
            "Common in interpersonal conflicts and paranoia."
        ),
        UtilityItemData(
            "Sublimation", 
            "Channeling socially unacceptable impulses into productive activities.",
            "A mature defense mechanism. Turning anger into intense sports training.",
            listOf("A person with aggressive urges becoming a professional boxer."),
            "Considered the healthiest way to deal with internal conflict."
        ),
        UtilityItemData(
            "Rationalization", 
            "Creating self-justifying explanations for behavior.",
            "Explaining away failures to avoid feeling bad.",
            listOf("Saying you didn't want a job anyway after being rejected."),
            "Protects self-esteem in the short term."
        )
    )
    UtilityList(mechs, onClick)
}
