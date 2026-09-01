package com.awra.catalogue.ui.utility.tools.biology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun KingdomsOfLifeContent(onClick: (UtilityItemData) -> Unit) {
    val kingdoms = listOf(
        UtilityItemData(
            "Archaebacteria",
            "Ancient, extreme bacteria",
            "Unicellular prokaryotes that live in extreme environments like hot springs or salt lakes.",
            listOf("Methanogens", "Halophiles"),
            "Study of early life on Earth."
        ),
        UtilityItemData(
            "Eubacteria",
            "Modern, true bacteria",
            "Unicellular prokaryotes that are found almost everywhere, some beneficial and some pathogenic.",
            listOf("E. coli", "Cyanobacteria"),
            "Medicine and environmental science."
        ),
        UtilityItemData(
            "Protista",
            "Diverse, mostly unicellular",
            "Eukaryotes that don't fit into other kingdoms. Includes plant-like, animal-like, and fungus-like members.",
            listOf("Amoeba", "Algae", "Slime molds"),
            "Microbiology and oceanography."
        ),
        UtilityItemData(
            "Fungi",
            "Absorptive heterotrophs",
            "Multicellular (mostly) eukaryotes with cell walls made of chitin. They decompose organic matter.",
            listOf("Mushrooms", "Yeast", "Molds"),
            "Pharmacy (antibiotics) and baking."
        ),
        UtilityItemData(
            "Plantae",
            "Autotrophic organisms",
            "Multicellular eukaryotes with cellulose cell walls that perform photosynthesis.",
            listOf("Mosses", "Ferns", "Flowering plants"),
            "Agriculture and ecology."
        ),
        UtilityItemData(
            "Animalia",
            "Ingestive heterotrophs",
            "Multicellular eukaryotes without cell walls. They are generally mobile at some stage of life.",
            listOf("Insects", "Birds", "Mammals"),
            "Zoology and veterinary medicine."
        )
    )
    UtilityList(kingdoms, onClick)
}
