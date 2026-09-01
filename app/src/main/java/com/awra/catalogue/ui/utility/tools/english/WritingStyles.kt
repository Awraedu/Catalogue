package com.awra.catalogue.ui.utility.tools.english

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun WritingStylesContent(onClick: (UtilityItemData) -> Unit) {
    val styles = listOf(
        UtilityItemData(
            "Expository", 
            "Informs or explains a subject to the reader.",
            "Focuses on facts and logic rather than opinions.",
            listOf("Instruction manuals", "Encyclopedic entries"),
            "Used in academic textbooks and technical documentation."
        )
    )
    UtilityList(styles, onClick)
}
