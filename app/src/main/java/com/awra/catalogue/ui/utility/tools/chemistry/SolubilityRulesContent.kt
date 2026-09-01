package com.awra.catalogue.ui.utility.tools.chemistry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.RuleCard
import com.awra.catalogue.ui.utility.SectionTitle
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun SolubilityRulesContent(onClick: (UtilityItemData) -> Unit) {
    val solubleItems = listOf(
        UtilityItemData("Group 1 & Ammonium", "All Li+, Na+, K+, Rb+, Cs+, and NH4+ salts are soluble.", "No exceptions."),
        UtilityItemData("Nitrates & Acetates", "All NO3- and C2H3O2- salts are soluble.", "No exceptions."),
        UtilityItemData("Chlorides, Bromides, Iodides", "Most Cl-, Br-, and I- salts are soluble.", "Exceptions: Ag+, Pb2+, Hg2 2+"),
        UtilityItemData("Sulfates", "Most SO4 2- salts are soluble.", "Exceptions: Ba2+, Pb2+, Hg2 2+, Ca2+, Sr2+")
    )

    val insolubleItems = listOf(
        UtilityItemData("Carbonates & Phosphates", "Most CO3 2- and PO4 3- salts are insoluble.", "Exceptions: Group 1 and NH4+"),
        UtilityItemData("Hydroxides & Sulfides", "Most OH- and S 2- salts are insoluble.", "Exceptions: Group 1, NH4+, and heavy alkaline earth metals (Ca2+, Sr2+, Ba2+ for OH-)")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        item { SectionTitle("Soluble Compounds") }
        itemsIndexed(solubleItems) { index, item ->
            RuleCard(item, index, solubleItems.size, onClick = { onClick(item) })
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        item { SectionTitle("Insoluble Compounds") }
        itemsIndexed(insolubleItems) { index, item ->
            RuleCard(item, index, insolubleItems.size, onClick = { onClick(item) })
        }
    }
}
