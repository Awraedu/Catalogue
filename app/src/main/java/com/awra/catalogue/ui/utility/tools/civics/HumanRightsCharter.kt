package com.awra.catalogue.ui.utility.tools.civics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun HumanRightsCharterContent(onClick: (UtilityItemData) -> Unit) {
    val articles = listOf(
        UtilityItemData(
            "Right to Life",
            "Every person has the inviolable right to life.",
            "No person shall be deprived of his life except as a punishment for a serious criminal offence determined by law.",
            listOf("Article 15"),
            "Foundation of all human rights."
        ),
        UtilityItemData(
            "Right to Liberty",
            "Right to protection from arbitrary arrest.",
            "No person shall be deprived of his or her liberty except on such grounds and in accordance with such procedure as are established by law.",
            listOf("Article 17"),
            "Protects citizens from unlawful detention."
        ),
        UtilityItemData(
            "Prohibition of Inhuman Treatment",
            "Protection from torture and degrading treatment.",
            "Everyone has the right to protection against cruel, inhuman or degrading treatment or punishment.",
            listOf("Article 18"),
            "Prohibits slavery and forced labor."
        )
    )
    UtilityList(articles, onClick)
}
