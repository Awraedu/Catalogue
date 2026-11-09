package com.awra.catalogue.ui.utility

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Utility
import com.awra.catalogue.ui.utility.tools.anthropology.ArcheologicalMethodsContent
import com.awra.catalogue.ui.utility.tools.anthropology.CultureConceptContent
import com.awra.catalogue.ui.utility.tools.anthropology.EvolutionStagesContent
import com.awra.catalogue.ui.utility.tools.anthropology.HumanVariationContent
import com.awra.catalogue.ui.utility.tools.biology.*
import com.awra.catalogue.ui.utility.tools.chemistry.*
import com.awra.catalogue.ui.utility.tools.civics.*
import com.awra.catalogue.ui.utility.tools.common.*
import com.awra.catalogue.ui.utility.tools.economics.*
import com.awra.catalogue.ui.utility.tools.emergingtech.*
import com.awra.catalogue.ui.utility.tools.entrepreneurship.*
import com.awra.catalogue.ui.utility.tools.fitness.*
import com.awra.catalogue.ui.utility.tools.geography.*
import com.awra.catalogue.ui.utility.tools.globaltrends.*
import com.awra.catalogue.ui.utility.tools.history.*
import com.awra.catalogue.ui.utility.tools.english.GrammarRulesContent
import com.awra.catalogue.ui.utility.tools.english.LiteraryDevicesContent
import com.awra.catalogue.ui.utility.tools.english.PhoneticChart
import com.awra.catalogue.ui.utility.tools.english.WritingStylesContent
import com.awra.catalogue.ui.utility.tools.logic.LogicOperatorsContent
import com.awra.catalogue.ui.utility.tools.logic.LogicalFallaciesContent
import com.awra.catalogue.ui.utility.tools.logic.ReasoningTypesContent
import com.awra.catalogue.ui.utility.tools.logic.TruthTablesContent
import com.awra.catalogue.ui.utility.tools.math.*
import com.awra.catalogue.ui.utility.tools.physics.*
import com.awra.catalogue.ui.utility.tools.psychology.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityDetailScreen(
    utility: Utility,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<UtilityItemData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val isEarthLayers = utility.type == "EARTH_LAYERS"
            val appBarColor = if (isEarthLayers) Color(0xFF0B1218) else MaterialTheme.colorScheme.surface
            val contentColor = if (isEarthLayers) Color.White else MaterialTheme.colorScheme.onSurface
            
            TopAppBar(
                title = { Text(utility.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) "Unpin" else "Pin",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor
                )
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
                "DISABILITY_TYPES" -> DisabilityTypesContent(onItemClick)
                "SIGN_LANGUAGE" -> SignLanguageContent(onItemClick)
                "BRAILLE" -> BrailleChartContent(onItemClick)
                "INCLUSIVE_DESIGN" -> InclusiveDesignContent(onItemClick)
                "BMC" -> BusinessModelCanvasContent(onItemClick)
                "STARTUP_FINANCE" -> StartupFinancialsContent(onItemClick)
                "LEAN_STARTUP" -> LeanStartupContent(onItemClick)
                "GROWTH_METRICS" -> GrowthMetricsContent(onItemClick)
                "PITCH_DECK" -> PitchDeckStructureContent(onItemClick)
                "AI_ETHICS" -> AIEthicsContent(onItemClick)
                "IOT_STACK" -> IoTStackContent(onItemClick)
                "BLOCKCHAIN_VIS" -> BlockchainContent(onItemClick)
                "CLOUD_MODELS" -> CloudModelsContent(onItemClick)
                "TECH_TIMELINE" -> TechRoadmapContent(onItemClick)
                "BMI_CALC" -> BMICalculatorContent(onItemClick)
                "NUTRITION" -> NutritionBasicsContent(onItemClick)
                "TRAINING_PRO" -> TrainingPrinciplesContent(onItemClick)
                "HR_ZONES" -> HeartRateZonesContent(onItemClick)
                "INT_ORGS" -> InternationalOrgsContent(onItemClick)
                "AU_HUB" -> InternationalOrgsContent(onItemClick) // Reusing since it contains AU
                "SDG_GRID" -> SDGContent(onItemClick)
                "GEO_CONCEPTS" -> GeopoliticalConceptsContent(onItemClick)
                "PERIODIC_TABLE" -> PeriodicTableContent(onItemClick)
                "SOLUBILITY_RULES" -> SolubilityRulesContent(onItemClick)
                "GAS_LAWS" -> GasLawsContent(onItemClick)
                "STOICHIOMETRY" -> StoichiometryContent(onItemClick)
                "ORGANELLE_LIB" -> OrganelleLibraryContent(onItemClick)
                "BODY_SYSTEMS" -> BodySystemsContent(onItemClick)
                "KINGDOMS_OF_LIFE" -> KingdomsOfLifeContent(onItemClick)
                "WORLD_MAP" -> WorldMapContent(onItemClick)
                "CLIMATE_ZONES" -> ClimateZonesContent(onItemClick)
                "EARTH_LAYERS" -> EarthLayersContent(onItemClick)
                "ETHIOPIA_REGIONS" -> EthiopiaRegionsContent(onItemClick)
                "FORMULA_BANK" -> FormulaBankContent(onItemClick)
                "WAVES_OPTICS" -> WavesOpticsContent(onItemClick)
                "ELECTROMAGNETISM" -> ElectromagnetismContent(onItemClick)
                "PHYS_CONSTANTS" -> PhysicalConstantsContent(onItemClick)
                "CIRCUIT_SYMBOLS" -> CircuitSymbolsContent(onItemClick)
                "DYNAMICS_ENERGY" -> DynamicsEnergyContent(onItemClick)
                "DERIVATIVES" -> DerivativeTableContent(onItemClick)
                "INTEGRALS" -> IntegrationFormulasContent(onItemClick)
                "TRIG_IDENTITY" -> TrigIdentitiesContent(onItemClick)
                "GEOMETRY" -> GeometryFormulasContent(onItemClick)
                "LOG_LAWS" -> LogarithmLawsContent(onItemClick)
                "STATS_GUIDE" -> StatisticsGuideContent(onItemClick)
                "ECON_FORMULA" -> EconomicFormulasContent(onItemClick)
                "MARKET_STRUCTURES" -> MarketStructuresContent(onItemClick)
                "SUPPLY_DEMAND" -> SupplyDemandContent(onItemClick)
                "HIST_TIMELINE" -> HistoricalTimelineContent()
                "ANCIENT_CIV" -> AncientCivilizationsContent()
                "HIST_LEADERS" -> HistoricalLeadersContent()
                else -> Text("Content for ${utility.name} coming soon.", modifier = Modifier.align(Alignment.Center))
            }

            if (showSheet && selectedItem != null) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    if (utility.type == "PERIODIC_TABLE") {
                        ElementDetailSheet(selectedItem!!)
                    } else if (utility.type == "WORLD_MAP") {
                        CountryDetailSheet(selectedItem!!)
                    } else {
                        UtilityDetailSheetContent(selectedItem!!)
                    }
                }
            }
        }
    }
}
