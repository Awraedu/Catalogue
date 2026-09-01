package com.awra.catalogue.ui.utility.tools.emergingtech

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun AIEthicsContent(onClick: (UtilityItemData) -> Unit) {
    val principles = listOf(
        UtilityItemData(
            "Transparency",
            "Understandable AI systems",
            "AI systems should be designed so that their decisions can be explained and understood by humans.",
            listOf("Explainable AI (XAI)", "Open-source algorithms"),
            "Prevents 'black box' decision making in critical areas like healthcare or law."
        ),
        UtilityItemData(
            "Justice & Fairness",
            "Eliminating algorithmic bias",
            "AI should be developed and used in ways that are equitable and do not discriminate against groups or individuals.",
            listOf("Bias detection tools", "Diverse training datasets"),
            "Ensures AI doesn't reinforce existing societal prejudices."
        ),
        UtilityItemData(
            "Safety & Security",
            "Robust and reliable AI",
            "AI systems must be secure from hacking and robust enough to handle unexpected inputs without causing harm.",
            listOf("Adversarial testing", "Fail-safe mechanisms"),
            "Crucial for autonomous vehicles and critical infrastructure."
        ),
        UtilityItemData(
            "Privacy",
            "Data protection by design",
            "Respecting user privacy and ensuring data used to train AI is handled securely and ethically.",
            listOf("Differential privacy", "Data anonymization"),
            "Protecting individual identity in the age of big data."
        ),
        UtilityItemData(
            "Human Oversight",
            "Humans in the loop",
            "AI should support human autonomy, not replace it. Critical decisions should always have a human fallback.",
            listOf("Human-in-the-loop (HITL)", "Stop switches"),
            "Maintains ethical accountability for AI-driven actions."
        )
    )
    UtilityList(principles, onClick)
}
