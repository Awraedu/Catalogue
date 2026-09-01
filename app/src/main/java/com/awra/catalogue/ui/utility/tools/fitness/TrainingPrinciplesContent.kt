package com.awra.catalogue.ui.utility.tools.fitness

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun TrainingPrinciplesContent(onClick: (UtilityItemData) -> Unit) {
    val principles = listOf(
        UtilityItemData(
            "Progressive Overload",
            "Gradually increasing stress",
            "In order for a muscle to grow and improve, it must be forced to work at a higher tension than it is accustomed to.",
            listOf("Increasing weight", "Increasing reps", "Reducing rest time"),
            "Fundamental to strength and hypertrophy training."
        ),
        UtilityItemData(
            "Specificity",
            "Training for a specific goal",
            "The body will adapt specifically to the type of demand placed on it.",
            listOf("Running for endurance", "Lifting for strength", "Yoga for flexibility"),
            "Ensures training matches desired outcomes."
        ),
        UtilityItemData(
            "F.I.T.T. Principle",
            "Frequency, Intensity, Time, Type",
            "A formula used to design effective workout programs.",
            listOf("Frequency: How often", "Intensity: How hard", "Time: How long", "Type: What activity"),
            "Standard framework for all fitness levels."
        ),
        UtilityItemData(
            "Reversibility",
            "Use it or lose it",
            "Fitness gains will be lost if training is stopped for a significant period.",
            listOf("Loss of strength", "Reduction in cardiovascular capacity"),
            "Encourages consistency in exercise habits."
        ),
        UtilityItemData(
            "Recovery",
            "Allowing the body to repair",
            "Physical improvements occur during rest, not during the actual training.",
            listOf("Sleep", "Active rest days", "Proper nutrition"),
            "Prevents overtraining and injuries."
        )
    )
    UtilityList(principles, onClick)
}
