package com.awra.catalogue.ui.utility.tools.psychology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun PsychPerspectivesContent(onClick: (UtilityItemData) -> Unit) {
    val perspectives = listOf(
        UtilityItemData(
            "Psychodynamic", 
            "Focuses on unconscious drives and childhood experiences.",
            "Originated with Freud. Emphasizes how internal conflicts shape behavior.",
            listOf("Analyzing dreams", "Exploring early relationships"),
            "Used in psychoanalysis to uncover hidden motivations."
        ),
        UtilityItemData(
            "Behavioral", 
            "Focuses on observable behaviors and environmental learning.",
            "Based on the work of Watson and Skinner. Behavior is learned through reinforcement.",
            listOf("Reward systems for children", "Training pets"),
            "Foundation for behavioral therapy and classroom management."
        ),
        UtilityItemData(
            "Cognitive", 
            "Focuses on mental processes like memory and thinking.",
            "Behavior is determined by how we process and store information.",
            listOf("Problem-solving strategies", "Memory improvement techniques"),
            "Helps in developing computer models of artificial intelligence."
        ),
        UtilityItemData(
            "Humanistic", 
            "Focuses on self-growth and human potential.",
            "Emphasizes free will and individual choice (Maslow, Rogers).",
            listOf("Client-centered therapy", "Self-actualization pursuits"),
            "Promotes empathy and unconditional positive regard in counseling."
        )
    )
    UtilityList(perspectives, onClick)
}
