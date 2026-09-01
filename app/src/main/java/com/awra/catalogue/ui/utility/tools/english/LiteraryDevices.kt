package com.awra.catalogue.ui.utility.tools.english

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun LiteraryDevicesContent(onClick: (UtilityItemData) -> Unit) {
    val devices = listOf(
        UtilityItemData(
            "Simile", 
            "A comparison using 'like' or 'as'.",
            "Makes descriptions more vivid by linking two different concepts.",
            listOf("He was as brave as a lion.", "The water was clear like crystal."),
            "Used in creative writing and speeches to evoke emotion."
        ),
        UtilityItemData(
            "Metaphor", 
            "A direct comparison without 'like' or 'as'.",
            "States that one thing IS another to imply shared characteristics.",
            listOf("Time is a thief.", "The world is a stage."),
            "Used in poetry and persuasive literature."
        )
    )
    UtilityList(devices, onClick)
}
