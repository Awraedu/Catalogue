package com.awra.catalogue.ui.utility

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.data.model.Utility
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle

data class UtilityItemData(
    val title: String,
    val shortDesc: String,
    val detailedExplanation: String = "",
    val examples: List<String> = emptyList(),
    val realWorldUse: String = ""
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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

@Composable
private fun UtilityDetailSheetContent(item: UtilityItemData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Explanation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (item.detailedExplanation.isNotEmpty()) item.detailedExplanation else item.shortDesc,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }

        if (item.examples.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Examples",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                item.examples.forEach { example ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        if (item.realWorldUse.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Real World Application",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.realWorldUse,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun PhoneticChart() {
    val vowels = listOf("i:", "ɪ", "ʊ", "u:", "e", "ə", "ɜ:", "ɔ:", "æ", "ʌ", "ɑ:", "ɒ")
    val diphthongs = listOf("ɪə", "eɪ", "ʊə", "ɔɪ", "əʊ", "eə", "aɪ", "aʊ")
    val consonants = listOf("p", "b", "t", "d", "tʃ", "dʒ", "k", "g", "f", "v", "θ", "ð", "s", "z", "ʃ", "ʒ", "m", "n", "ŋ", "h", "l", "r", "w", "j")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item { SectionTitle("Monophthongs (Vowels)") }
        item { PhoneticGrid(vowels) }

        item { SectionTitle("Diphthongs") }
        item { PhoneticGrid(diphthongs) }

        item { SectionTitle("Consonants") }
        item { PhoneticGrid(consonants) }
        
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhoneticGrid(items: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4
    ) {
        items.forEach { symbol ->
            PhoneticTile(symbol, modifier = Modifier.weight(1f))
        }
        // Fill empty slots in last row to maintain alignment if needed
        val remain = items.size % 4
        if (remain != 0) {
            repeat(4 - remain) {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PhoneticTile(symbol: String, modifier: Modifier = Modifier) {
    Surface(
        onClick = { /* Could show details or play sound */ },
        shape = ContinuousRoundedRectangle(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.aspectRatio(1f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 4.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RuleCard(item: UtilityItemData, index: Int, count: Int, onClick: () -> Unit) {
    val outerRadius = 32.dp
    val innerRadius = 8.dp
    val itemShape = when {
        count == 1 -> ContinuousRoundedRectangle(outerRadius)
        index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
        index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
        else -> RoundedCornerShape(innerRadius)
    }

    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text(item.shortDesc, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GrammarRulesContent(onClick: (UtilityItemData) -> Unit) {
    val rules = listOf(
        UtilityItemData(
            "Present Simple", 
            "Used for habits, facts, and permanent states.",
            "Formed with the base verb. Add 's' for 3rd person singular.",
            listOf("I play football every Saturday.", "Water boils at 100 degrees Celsius."),
            "Used in scientific papers to describe facts."
        ),
        UtilityItemData(
            "Zero Conditional", 
            "Used for facts and general truths.",
            "If + present simple, ... present simple.",
            listOf("If you heat ice, it melts.", "If it rains, the grass gets wet."),
            "Essential for giving technical instructions."
        ),
        UtilityItemData(
            "Passive Voice", 
            "Used when the action is more important than the subject.",
            "Form: am/is/are + past participle.",
            listOf("The bridge was built in 1990.", "Mistakes were made."),
            "Used extensively in formal reports and news writing."
        )
    )
    UtilityList(rules, onClick)
}

@Composable
private fun LiteraryDevicesContent(onClick: (UtilityItemData) -> Unit) {
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

@Composable
private fun WritingStylesContent(onClick: (UtilityItemData) -> Unit) {
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

@Composable
private fun HotlineContent(onClick: (UtilityItemData) -> Unit) {
    val resources = listOf(
        UtilityItemData("Emergency", "Call 991 for immediate assistance.", "Global emergency number in Ethiopia.")
    )
    UtilityList(resources, onClick)
}

@Composable
private fun BrainRegionsContent(onClick: (UtilityItemData) -> Unit) {
    val regions = listOf(
        UtilityItemData(
            "Frontal Lobe", 
            "Executive functions and voluntary movement.",
            "Contains the motor cortex and Broca's area. Crucial for personality and social behavior.",
            listOf("Planning a schedule", "Controlling impulses", "Speaking fluently"),
            "Damage often leads to radical changes in personality (e.g., Phineas Gage)."
        ),
        UtilityItemData(
            "Parietal Lobe", 
            "Processing somatosensory information.",
            "Responsible for integrating sensory input from various parts of the body.",
            listOf("Feeling the texture of a cloth", "Judging distance and spatial relationships"),
            "Helps in navigation and spatial orientation."
        ),
        UtilityItemData(
            "Temporal Lobe", 
            "Auditory processing and memory.",
            "Contains the primary auditory cortex and Wernicke's area. Vital for language comprehension.",
            listOf("Recognizing a song", "Understanding a conversation", "Storing new memories"),
            "Key region for language and long-term memory."
        ),
        UtilityItemData(
            "Occipital Lobe", 
            "Visual processing center.",
            "Located at the back of the brain. Processes color, form, and motion.",
            listOf("Seeing a bird in flight", "Reading text on a screen"),
            "Primary area for receiving and interpreting visual information."
        )
    )
    UtilityList(regions, onClick)
}

@Composable
private fun PsychPerspectivesContent(onClick: (UtilityItemData) -> Unit) {
    val perspectives = listOf(
        UtilityItemData(
            "Psychodynamic", 
            "Focuses on unconscious drives and childhood experiences.",
            "Originated with Freud. Emphasizes how internal conflicts shape behavior.",
            listOf("Analyzing dreams", "Exploring early relationships"),
            "Used in psychoanalysis to uncover hidden motivations."
        ),
        UtilityItemData(
            "Behavioral", 
            "Focuses on observable behaviors and environmental learning.",
            "Based on the work of Watson and Skinner. Behavior is learned through reinforcement.",
            listOf("Reward systems for children", "Training pets"),
            "Foundation for behavioral therapy and classroom management."
        ),
        UtilityItemData(
            "Cognitive", 
            "Focuses on mental processes like memory and thinking.",
            "Behavior is determined by how we process and store information.",
            listOf("Problem-solving strategies", "Memory improvement techniques"),
            "Helps in developing computer models of artificial intelligence."
        ),
        UtilityItemData(
            "Humanistic", 
            "Focuses on self-growth and human potential.",
            "Emphasizes free will and individual choice (Maslow, Rogers).",
            listOf("Client-centered therapy", "Self-actualization pursuits"),
            "Promotes empathy and unconditional positive regard in counseling."
        )
    )
    UtilityList(perspectives, onClick)
}

@Composable
private fun DefenseMechanismsContent(onClick: (UtilityItemData) -> Unit) {
    val mechs = listOf(
        UtilityItemData(
            "Repression", 
            "Pushing threatening thoughts into the unconscious.",
            "The most basic defense mechanism. Forgetting a traumatic event.",
            listOf("A child abused by a parent having no memory of it later."),
            "Can lead to psychological distress if not addressed in therapy."
        ),
        UtilityItemData(
            "Projection", 
            "Attributing one's own unacceptable traits to others.",
            "A person who is dishonest suspicious of others' honesty.",
            listOf("Accusing your partner of cheating because you feel guilty about flirting."),
            "Common in interpersonal conflicts and paranoia."
        ),
        UtilityItemData(
            "Sublimation", 
            "Channeling socially unacceptable impulses into productive activities.",
            "A mature defense mechanism. Turning anger into intense sports training.",
            listOf("A person with aggressive urges becoming a professional boxer."),
            "Considered the healthiest way to deal with internal conflict."
        ),
        UtilityItemData(
            "Rationalization", 
            "Creating self-justifying explanations for behavior.",
            "Explaining away failures to avoid feeling bad.",
            listOf("Saying you didn't want a job anyway after being rejected."),
            "Protects self-esteem in the short term."
        )
    )
    UtilityList(mechs, onClick)
}

@Composable
private fun DevelopmentStagesContent(onClick: (UtilityItemData) -> Unit) {
    val stages = listOf(
        UtilityItemData(
            "Piaget: Sensory-Motor", 
            "Birth to 2 years. Learning through senses and motor actions.",
            "Developing object permanence—the understanding that objects exist even when unseen.",
            listOf("Playing peek-a-boo", "Exploring objects by putting them in the mouth"),
            "Foundational stage for cognitive development."
        ),
        UtilityItemData(
            "Piaget: Pre-operational", 
            "2 to 7 years. Use of symbols and language.",
            "Characterized by egocentrism and a lack of logical reasoning.",
            listOf("Imaginary play", "Believing the moon follows them"),
            "Crucial for early childhood education and communication."
        ),
        UtilityItemData(
            "Erikson: Trust vs. Mistrust", 
            "Infancy (0-1 year). Developing a sense of security.",
            "Success leads to the virtue of hope. Failure leads to fear and anxiety.",
            listOf("Caregivers consistently meeting needs"),
            "Determines the child's future ability to form healthy relationships."
        )
    )
    UtilityList(stages, onClick)
}

@Composable
private fun EvolutionStagesContent(onClick: (UtilityItemData) -> Unit) {
    val stages = listOf(
        UtilityItemData(
            "Australopithecus", 
            "Early hominins from Africa (4-2 million years ago).",
            "Key species like A. afarensis (Lucy). Primarily bipedal but still spent time in trees.",
            listOf("Bipedalism", "Small brain size (~400cc)"),
            "Foundation of human bipedal evolution."
        ),
        UtilityItemData(
            "Homo Habilis", 
            "The 'Handy Man' (2.4-1.4 million years ago).",
            "First species associated with stone tool use (Oldowan tools). Larger brain than Australopithecus.",
            listOf("Tool making", "Brain size increase (~600-700cc)"),
            "Emergence of technological intelligence."
        ),
        UtilityItemData(
            "Homo Erectus", 
            "The first to leave Africa (1.9 million - 140,000 years ago).",
            "Discovered fire and used Acheulean tools (hand axes). Tall, lean proportions similar to modern humans.",
            listOf("Use of fire", "Long-distance migration", "Complex tool making"),
            "Adaptation to diverse global environments."
        ),
        UtilityItemData(
            "Homo Sapiens", 
            "Modern humans (300,000 years ago to present).",
            "Characterized by complex language, symbolic art, and large brain size.",
            listOf("Symbolic thought", "Agriculture", "Complex social structures"),
            "Global dominance and cultural diversification."
        )
    )
    UtilityList(stages, onClick)
}

@Composable
private fun CultureConceptContent(onClick: (UtilityItemData) -> Unit) {
    val concepts = listOf(
        UtilityItemData(
            "Holism", 
            "Studying the whole of the human condition.",
            "Anthropologists study past, present, and future; biology, society, language, and culture.",
            listOf("Combining biological and cultural data"),
            "Ensures a comprehensive understanding of human diversity."
        ),
        UtilityItemData(
            "Cultural Relativism", 
            "Understanding cultures on their own terms.",
            "Opposite of ethnocentrism. Behavior in one culture should not be judged by the standards of another.",
            listOf("Refraining from judging traditional practices as 'wrong' without context"),
            "Core ethical and methodological principle in anthropology."
        ),
        UtilityItemData(
            "Ethnocentrism", 
            "Judging other cultures by your own standards.",
            "The tendency to view one's own culture as superior.",
            listOf("Labeling unfamiliar food as 'gross'"),
            "A barrier to cross-cultural understanding."
        ),
        UtilityItemData(
            "Enculturation", 
            "The process of learning culture.",
            "How people learn their society's values, norms, and behaviors since childhood.",
            listOf("Learning language from parents", "Observing social cues"),
            "Ensures the continuity of culture through generations."
        )
    )
    UtilityList(concepts, onClick)
}

@Composable
private fun ArcheologicalMethodsContent(onClick: (UtilityItemData) -> Unit) {
    val methods = listOf(
        UtilityItemData(
            "Surveying", 
            "Locating archaeological sites.",
            "The systematic search of an area to identify potential areas of past human activity.",
            listOf("Aerial photography", "Ground-penetrating radar"),
            "Essential first step before excavation."
        ),
        UtilityItemData(
            "Excavation", 
            "Systematic digging and recording.",
            "Careful removal of earth layers to recover artifacts and features in context.",
            listOf("Stratigraphy analysis", "Mapping finds"),
            "Primary source of archaeological data."
        ),
        UtilityItemData(
            "Dating Techniques", 
            "Determining the age of remains.",
            "Relative dating (stratigraphy) vs. Absolute dating (Radiocarbon, Dendrochronology).",
            listOf("Carbon-14 dating for organic materials"),
            "Placing archaeological finds in a chronological sequence."
        )
    )
    UtilityList(methods, onClick)
}

@Composable
private fun HumanVariationContent(onClick: (UtilityItemData) -> Unit) {
    val variation = listOf(
        UtilityItemData(
            "Biological Variation", 
            "Physical differences within the human species.",
            "Result of genetics and environmental adaptation.",
            listOf("Skin color (Melanin)", "Lactose tolerance", "Body proportions"),
            "Allows humans to survive in diverse climates (e.g., Bergmann's Rule)."
        ),
        UtilityItemData(
            "Skin Color Adaptation", 
            "Adaptation to UV radiation.",
            "Melanin protects against folate breakdown in high UV areas (equator). Vitamin D synthesis requires UV in low UV areas.",
            listOf("Dark skin in tropics", "Light skin in high latitudes"),
            "Classic example of natural selection in humans."
        ),
        UtilityItemData(
            "High-Altitude Adaptation", 
            "Physiological changes for low oxygen.",
            "Populations in the Andes and Himalayas have evolved different ways to process oxygen.",
            listOf("Increased chest size", "Higher hemoglobin levels"),
            "Survival in extreme environments."
        )
    )
    UtilityList(variation, onClick)
}

@Composable
private fun LogicalFallaciesContent(onClick: (UtilityItemData) -> Unit) {
    val fallacies = listOf(
        UtilityItemData(
            "Ad Hominem", 
            "Attacking the person instead of the argument.",
            "Character assassination used to distract from the actual reasoning.",
            listOf("You can't trust his climate policy because he didn't finish college."),
            "Common in political debates and toxic social media discussions."
        ),
        UtilityItemData(
            "Straw Man", 
            "Misrepresenting an argument to make it easier to attack.",
            "Creating a weak version of an opponent's position and then 'demolishing' it.",
            listOf("A: We should spend more on education. B: Why do you hate our military so much that you want to cut their budget?"),
            "Used to win arguments without actually engaging with the core issue."
        ),
        UtilityItemData(
            "Slippery Slope", 
            "Claiming one small step will lead to a chain of disastrous events.",
            "Assumes a chain reaction without providing evidence for the inevitability of each step.",
            listOf("If we let students use calculators, they will soon forget how to add, then they will lose all basic logic skills, and eventually, civilization will collapse."),
            "Often used to discourage policy changes through fear."
        )
    )
    UtilityList(fallacies, onClick)
}

@Composable
private fun LogicOperatorsContent(onClick: (UtilityItemData) -> Unit) {
    val operators = listOf(
        UtilityItemData(
            "Conjunction (AND)", 
            "True only if both components are true.",
            "Represented by the symbol '∧'. P ∧ Q is true only if P is T and Q is T.",
            listOf("It is raining AND I have an umbrella."),
            "Used in computer programming and mathematical proofs."
        ),
        UtilityItemData(
            "Disjunction (OR)", 
            "True if at least one component is true.",
            "Represented by the symbol '∨'. P ∨ Q is false only if both P and Q are false.",
            listOf("I will study Logic OR I will sleep."),
            "The 'inclusive or' used in most formal logic systems."
        ),
        UtilityItemData(
            "Conditional (IF...THEN)", 
            "If the first part is true, the second must follow.",
            "Represented by '→'. P → Q is false only when the antecedent P is true and the consequent Q is false.",
            listOf("IF you study, THEN you will pass."),
            "The foundation of scientific hypotheses and logical arguments."
        )
    )
    UtilityList(operators, onClick)
}

@Composable
private fun ReasoningTypesContent(onClick: (UtilityItemData) -> Unit) {
    val types = listOf(
        UtilityItemData(
            "Deductive Reasoning", 
            "Top-down logic. From general premises to specific conclusions.",
            "If the premises are true and the form is valid, the conclusion is NECESSARILY true (certainty).",
            listOf("All humans are mortal. Socrates is human. Therefore, Socrates is mortal."),
            "Used in mathematics and formal logic systems."
        ),
        UtilityItemData(
            "Inductive Reasoning", 
            "Bottom-up logic. From specific observations to general conclusions.",
            "The conclusion is PROBABLY true based on evidence, but not guaranteed.",
            listOf("Every swan I've seen is white. Therefore, all swans are probably white."),
            "The basis of scientific research and everyday decision-making."
        )
    )
    UtilityList(types, onClick)
}

@Composable
private fun TruthTablesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Negation (~P)")
                Text("Reverses the truth value.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "~P"),
                    rows = listOf(
                        listOf("T", "F"),
                        listOf("F", "T")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Conjunction (P ∧ Q)")
                Text("True only if both P and Q are true.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P ∧ Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "F"),
                        listOf("F", "T", "F"),
                        listOf("F", "F", "F")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Disjunction (P ∨ Q)")
                Text("False only if both P and Q are false.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P ∨ Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "T"),
                        listOf("F", "T", "T"),
                        listOf("F", "F", "F")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Conditional (P → Q)")
                Text("False only when P is true and Q is false.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P → Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "F"),
                        listOf("F", "T", "T"),
                        listOf("F", "F", "T")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Biconditional (P ↔ Q)")
                Text("True only if P and Q have the same truth value.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P ↔ Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "F"),
                        listOf("F", "T", "F"),
                        listOf("F", "F", "T")
                    )
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
private fun TruthTableDisplay(headers: List<String>, rows: List<List<String>>) {
    Surface(
        shape = ContinuousRoundedRectangle(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                headers.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Column divider
                    if (index < headers.size - 1) {
                        VerticalDivider(
                            modifier = Modifier.height(20.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Data Rows
            rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val isResult = colIndex == row.size - 1
                            val isTrue = cell == "T"
                            
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isTrue && isResult -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    !isTrue && isResult -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                    else -> Color.Transparent
                                },
                                modifier = Modifier.sizeIn(minWidth = 36.dp, minHeight = 36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = cell,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isResult) FontWeight.Black else FontWeight.Bold,
                                        color = when {
                                            isTrue -> if (isResult) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            else -> if (isResult) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Row column divider
                        if (colIndex < row.size - 1) {
                            VerticalDivider(
                                modifier = Modifier.height(24.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
                
                if (rowIndex < rows.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhysicsFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData("Velocity (v)", "v = Δx / Δt", "The rate of change of displacement with time.")
    )
    UtilityList(formulas, onClick)
}

@Composable
private fun PhysicsConstantsContent(onClick: (UtilityItemData) -> Unit) {
    val constants = listOf(
        UtilityItemData("Gravity (g)", "9.81 m/s²", "Standard gravity on Earth.")
    )
    UtilityList(constants, onClick)
}

@Composable
private fun MathAlgebraContent(onClick: (UtilityItemData) -> Unit) {
    val identities = listOf(
        UtilityItemData("Difference of Squares", "a² - b² = (a - b)(a + b)", "A core algebraic factorization.")
    )
    UtilityList(identities, onClick)
}

@Composable
private fun MathCalculusContent(onClick: (UtilityItemData) -> Unit) {
    val rules = listOf(
        UtilityItemData("Power Rule", "d/dx [x^n] = n * x^(n-1)", "Used to differentiate polynomial functions.")
    )
    UtilityList(rules, onClick)
}

@Composable
private fun EconFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData("GDP", "GDP = C + I + G + (X - M)", "Expenditure approach to national income.")
    )
    UtilityList(formulas, onClick)
}

@Composable
private fun UtilityList(items: List<UtilityItemData>, onClick: (UtilityItemData) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        itemsIndexed(items) { index, item ->
            RuleCard(item, index, items.size, onClick = { onClick(item) })
        }
    }
}
