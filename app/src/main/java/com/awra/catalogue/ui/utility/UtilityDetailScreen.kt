package com.awra.catalogue.ui.utility

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Utility
import com.awra.catalogue.ui.utility.tools.anthropology.ArcheologicalMethodsContent
import com.awra.catalogue.ui.utility.tools.anthropology.CultureConceptContent
import com.awra.catalogue.ui.utility.tools.anthropology.EvolutionStagesContent
import com.awra.catalogue.ui.utility.tools.anthropology.HumanVariationContent
import com.awra.catalogue.ui.utility.tools.civics.*
import com.awra.catalogue.ui.utility.tools.english.GrammarRulesContent
import com.awra.catalogue.ui.utility.tools.english.LiteraryDevicesContent
import com.awra.catalogue.ui.utility.tools.english.PhoneticChart
import com.awra.catalogue.ui.utility.tools.english.WritingStylesContent
import com.awra.catalogue.ui.utility.tools.logic.LogicOperatorsContent
import com.awra.catalogue.ui.utility.tools.logic.LogicalFallaciesContent
import com.awra.catalogue.ui.utility.tools.logic.ReasoningTypesContent
import com.awra.catalogue.ui.utility.tools.logic.TruthTablesContent
import com.awra.catalogue.ui.utility.tools.psychology.*
import com.awra.catalogue.ui.utility.tools.science.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityDetailScreen(
    utility: Utility,
    onBackClick: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<UtilityItemData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(utility.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val onItemClick: (UtilityItemData) -> Unit = {
                selectedItem = it
                showSheet = true
            }

            when (utility.type) {
                "PHONETIC" -> PhoneticChart()
                "GRAMMAR" -> GrammarRulesContent(onItemClick)
                "LITERARY" -> LiteraryDevicesContent(onItemClick)
                "WRITING" -> WritingStylesContent(onItemClick)
                "HOTLINE" -> HotlineContent(onItemClick)
                "PSYCH_PERSPECTIVE" -> PsychPerspectivesContent(onItemClick)
                "DEFENSE_MECH" -> DefenseMechanismsContent(onItemClick)
                "DEV_STAGES" -> DevelopmentStagesContent(onItemClick)
                "BRAIN_REGIONS" -> BrainRegionsContent(onItemClick)
                "EVOLUTION_STAGES" -> EvolutionStagesContent(onItemClick)
                "CULTURE_CONCEPT" -> CultureConceptContent(onItemClick)
                "ARCH_METHODS" -> ArcheologicalMethodsContent(onItemClick)
                "HUMAN_VARIATION" -> HumanVariationContent(onItemClick)
                "LOGICAL_FALLACIES" -> LogicalFallaciesContent(onItemClick)
                "LOGIC_OPERATORS" -> LogicOperatorsContent(onItemClick)
                "REASONING_TYPES" -> ReasoningTypesContent(onItemClick)
                "TRUTH_TABLE" -> TruthTablesContent()
                "CONST_EXPLORER" -> ConstitutionExplorerContent(onItemClick)
                "HR_CHARTER" -> HumanRightsCharterContent(onItemClick)
                "GOV_STRUCTURE" -> GovernmentStructureContent(onItemClick)
                "NAT_SYMBOL" -> NationalSymbolsContent(onItemClick)
                "CIVIC_DUTIES" -> CivicDutiesContent(onItemClick)
                "FORMULA_PHYS" -> PhysicsFormulasContent(onItemClick)
                "CONSTANTS" -> PhysicsConstantsContent(onItemClick)
                "ALGEBRA" -> MathAlgebraContent(onItemClick)
                "CALCULUS" -> MathCalculusContent(onItemClick)
                "ECON_FORMULA" -> EconFormulasContent(onItemClick)
                else -> Text("Content for ${utility.name} coming soon.", modifier = Modifier.align(Alignment.Center))
            }

            if (showSheet && selectedItem != null) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    UtilityDetailSheetContent(selectedItem!!)
                }
            }
        }
    }
}
