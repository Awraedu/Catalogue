package com.awra.catalogue.ui.utility.tools.psychology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun HotlineContent(onClick: (UtilityItemData) -> Unit) {
    val resources = listOf(
        UtilityItemData("Emergency", "Call 991 for immediate assistance.", "Global emergency number in Ethiopia.")
    )
    UtilityList(resources, onClick)
}
