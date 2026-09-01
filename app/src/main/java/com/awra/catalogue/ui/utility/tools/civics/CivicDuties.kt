package com.awra.catalogue.ui.utility.tools.civics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun CivicDutiesContent(onClick: (UtilityItemData) -> Unit) {
    val duties = listOf(
        UtilityItemData(
            "Respecting the Law",
            "Obligation to abide by the Constitution and laws.",
            "Ensures social order and the protection of rights of others.",
            listOf("Following traffic rules", "Paying taxes")
        ),
        UtilityItemData(
            "Defending the Country",
            "Participating in national security.",
            "Citizens have a duty to defend the sovereignty and territorial integrity of the country.",
            listOf("Military service when required")
        ),
        UtilityItemData(
            "Community Service",
            "Contributing to social development.",
            "Engaging in activities that improve the welfare of the community.",
            listOf("Volunteering", "Environmental protection")
        )
    )
    UtilityList(duties, onClick)
}
