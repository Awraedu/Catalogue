package com.awra.catalogue.data.database

import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.model.*

object DatabaseSeeder {
    val initialSubjects = listOf(
        // Common Subjects
        Subject(name = "Communicative English I", description = "Foundational language skills.", stream = Stream.COMMON, shape = ExpressiveShape.CIRCLE),
        Subject(name = "Communicative English II", description = "Advanced academic writing.", stream = Stream.COMMON, shape = ExpressiveShape.OVAL),
        Subject(name = "General Psychology", description = "Human behavior and mind.", stream = Stream.COMMON, shape = ExpressiveShape.PILL),
        Subject(name = "Logic & Critical Thinking", description = "Analytical reasoning skills.", stream = Stream.COMMON, shape = ExpressiveShape.SEMICIRCLE),
        Subject(name = "Geography of Ethiopia", description = "Regional geography and Horn.", stream = Stream.COMMON, shape = ExpressiveShape.ARCH),
        Subject(name = "History of Ethiopia", description = "National history and heritage.", stream = Stream.COMMON, shape = ExpressiveShape.BUN),
        Subject(name = "Social Anthropology", description = "Human cultures and diversity.", stream = Stream.COMMON, shape = ExpressiveShape.FAN),
        Subject(name = "Emerging Technologies", description = "AI, IoT, and future trends.", stream = Stream.COMMON, shape = ExpressiveShape.HEART),
        Subject(name = "Moral & Civics", description = "Ethics and citizenship.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_4),
        Subject(name = "Physical Fitness", description = "Health and wellness.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_6),
        Subject(name = "Inclusiveness", description = "Disability and vulnerability.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_7),
        Subject(name = "Global Trends", description = "International relations.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_9),
        
        // Natural Science Specific
        Subject(name = "Math for Nat Science", description = "Calculus and algebra.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_12),
        Subject(name = "General Physics", description = "Mechanics and heat.", stream = Stream.NATURAL, shape = ExpressiveShape.CLOVER_4),
        Subject(name = "General Chemistry", description = "Matter and its properties.", stream = Stream.NATURAL, shape = ExpressiveShape.CLOVER_8),
        Subject(name = "General Biology", description = "Study of living systems.", stream = Stream.NATURAL, shape = ExpressiveShape.PUFFY),
        
        // Social Science Specific
        Subject(name = "Math for Soc Science", description = "Basic math and stats.", stream = Stream.SOCIAL, shape = ExpressiveShape.SOFT_BOOM),
        Subject(name = "Intro to Economics", description = "Markets and society.", stream = Stream.SOCIAL, shape = ExpressiveShape.SOFT_BURST),
        Subject(name = "Entrepreneurship", description = "Business creation basics.", stream = Stream.SOCIAL, shape = ExpressiveShape.GHOST)
    )

    suspend fun seed(dao: CatalogueDao) {
        // To fix the "doubled" issue and ensure latest data structure:
        // Clear old subjects. This triggers CASCADE deletes for chapters, topics, etc.
        dao.deleteAllSubjects()
        
        val subjectIds = dao.insertSubjects(initialSubjects)
        
        for (i in initialSubjects.indices) {
            val subjectId = subjectIds[i]
            val subject = initialSubjects[i]
            
            when (subject.name) {
                "Communicative English I" -> seedEnglishI(dao, subjectId)
                "Communicative English II" -> seedEnglishII(dao, subjectId)
                "General Psychology" -> seedPsychology(dao, subjectId)
                "Logic & Critical Thinking" -> seedLogic(dao, subjectId)
                "Geography of Ethiopia" -> seedGeography(dao, subjectId)
                "History of Ethiopia" -> seedHistory(dao, subjectId)
                "Social Anthropology" -> seedAnthropology(dao, subjectId)
                "Emerging Technologies" -> seedEmergingTech(dao, subjectId)
                "Moral & Civics" -> seedCivics(dao, subjectId)
                "Physical Fitness" -> seedFitness(dao, subjectId)
                "Inclusiveness" -> seedInclusiveness(dao, subjectId)
                "Global Trends" -> seedGlobalTrends(dao, subjectId)
                "Math for Nat Science" -> seedMathNat(dao, subjectId)
                "General Physics" -> seedPhysics(dao, subjectId)
                "General Chemistry" -> seedChemistry(dao, subjectId)
                "General Biology" -> seedBiology(dao, subjectId)
                "Math for Soc Science" -> seedMathSoc(dao, subjectId)
                "Intro to Economics" -> seedEconomics(dao, subjectId)
                "Entrepreneurship" -> seedEntrepreneurship(dao, subjectId)
                else -> seedGenericMockData(dao, subjectId, subject.name)
            }
            
            seedUtilities(dao, subjectId, subject.name)
        }
    }

    private suspend fun seedDetailedSubject(
        dao: CatalogueDao,
        subjectId: Long,
        subjectName: String,
        units: List<Pair<String, List<String>>>
    ) {
        units.forEachIndexed { index, (unitTitle, topics) ->
            val chapterId = dao.insertChapter(
                Chapter(subjectId = subjectId, number = index + 1, title = unitTitle)
            )
            topics.forEach { topicTitle ->
                val topicId = dao.insertTopic(
                    Topic(chapterId = chapterId, title = topicTitle)
                )
                dao.insertHandout(
                    Handout(topicId = topicId, content = "Full detailed module content for $topicTitle in $subjectName.")
                )
            }
        }
    }

    private suspend fun seedEnglishI(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Study Skills" to listOf(
                "Nature of Lectures and Note-taking",
                "Formal and Informal Introductions",
                "Reading for Study: Pre, While and Post-reading",
                "Grammar: Modals and Infinitives for Advice",
                "Vocabulary: Guessing Meaning from Context"
            ),
            "Health and Fitness" to listOf(
                "Healthy Lifestyles and Sports Stars",
                "Reading: Fitness and Physical Activity",
                "Speaking: Benefits of Regular Exercise",
                "Grammar: Zero, First, Second and Third Conditionals",
                "Vocabulary: Health and Fitness Terminology"
            ),
            "Cultural Values" to listOf(
                "Lecture: Cultural Tourism and Heritage",
                "Reading: Cultural Diversity and Indigenous Knowledge",
                "Speaking: Group Discussion on Cultural Identity",
                "Grammar: Contrast of Verb Tenses",
                "Writing: Paragraphs on Personal Values"
            ),
            "Wildlife" to listOf(
                "Reading: Endemic Animals and National Parks",
                "Speaking: Conservation Threats and Solutions",
                "Grammar: Passive Voice for Reports",
                "Grammar: Comparisons and Superlatives",
                "Vocabulary: Conservation and Description"
            ),
            "Population" to listOf(
                "Listening: Population Trends and Demographics",
                "Reading: Impact of Population on Resources",
                "Grammar: Reported Speech",
                "Grammar: Subject-Verb Agreement",
                "Writing: Summarizing Data and Report Writing"
            )
        )
        seedDetailedSubject(dao, subjectId, "Communicative English I", units)
    }

    private suspend fun seedEnglishII(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Life Skills" to listOf(
                "Concept of Life Skills and Importance",
                "Grammar: Active and Passive Voice in Reports",
                "Speaking: Interpersonal Skills and Values",
                "Writing: Paragraph Development and Summarizing"
            ),
            "Future of Science" to listOf(
                "Scientific Contributions: Dr. Aklilu Lemma",
                "Grammar: Future Tense and Predictions",
                "Speaking: Future Technology and Trends",
                "Writing: Describing Scientific Processes"
            ),
            "Environmental Protection" to listOf(
                "Pollution, Deforestation and Water Management",
                "Grammar: Modal Verbs for Necessity and Permission",
                "Speaking: Environmental Solutions",
                "Writing: Argumentative Paragraphs on Preservation"
            ),
            "Indigenous Knowledge" to listOf(
                "Value of Local Knowledge Systems in Ethiopia",
                "Grammar: Conditional Sentences (Types 0-3)",
                "Speaking: Modern Science vs Traditional Knowledge",
                "Writing: Reflective Essays on Indigenous Wisdom"
            ),
            "Cultural Heritage" to listOf(
                "Ethiopian Cultural Heritage Sites",
                "Grammar: Defining and Non-defining Relative Clauses",
                "Speaking: Presentations on Local Cultural Icons",
                "Writing: Descriptive Writing about Historical Places"
            )
        )
        seedDetailedSubject(dao, subjectId, "Communicative English II", units)
    }

    private suspend fun seedPsychology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Essence of Psychology" to listOf("Historical Roots", "Modern Perspectives"),
            "Sensation and Perception" to listOf("Sensory Processes", "Interpretation of Information"),
            "Learning" to listOf("Classical Conditioning", "Operant Conditioning", "Observational Learning"),
            "Memory and Forgetting" to listOf("Stages of Memory", "Encoding and Retrieval", "Reasons for Forgetting"),
            "Motivation and Emotions" to listOf("Theories of Motivation", "Nature and Functions of Emotions"),
            "Personality" to listOf("Psychoanalytic Theory", "Trait and Humanistic Perspectives"),
            "Psychological Disorders" to listOf("Defining Abnormality", "Types of Disorders", "Therapy Techniques"),
            "Life Skills" to listOf("Self-awareness", "Empathy", "Conflict Resolution", "Time Management")
        )
        seedDetailedSubject(dao, subjectId, "General Psychology", units)
    }

    private suspend fun seedLogic(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Logic and Philosophy" to listOf("Nature and Branches of Philosophy"),
            "Basic Concepts of Logic" to listOf("Arguments, Premises, and Conclusions"),
            "Logic and Language" to listOf("Functions of Language", "Emotive Language"),
            "Critical Thinking" to listOf("Standards of Critical Thinking", "Benefits"),
            "Informal Fallacies" to listOf("Fallacies of Relevance", "Weak Induction", "Ambiguity"),
            "Categorical Propositions" to listOf("Components and Square of Opposition")
        )
        seedDetailedSubject(dao, subjectId, "Logic & Critical Thinking", units)
    }

    private suspend fun seedGeography(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Location, Shape, and Size of Ethiopia"),
            "Geology" to listOf("Geologic Time Scale", "Mineral Resources"),
            "Topography" to listOf("Physiographic Divisions", "Highlands and Lowlands"),
            "Drainage Systems" to listOf("Major River Basins", "Lakes of Ethiopia"),
            "Climate" to listOf("Weather Elements", "Agro-ecological Zones"),
            "Natural Resources" to listOf("Soils", "Vegetation", "Wildlife Conservation"),
            "Population" to listOf("Distribution", "Migration", "Ethnic Diversity"),
            "Economic Activities" to listOf("Agriculture", "Manufacturing", "Service Sectors")
        )
        seedDetailedSubject(dao, subjectId, "Geography of Ethiopia", units)
    }

    private suspend fun seedHistory(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Nature of History", "Historiography of Ethiopia"),
            "Peoples and Cultures" to listOf("Human Evolution", "Neolithic Revolution"),
            "Ancient Polities" to listOf("Aksumite Empire", "Zagwe Dynasty", "Damot"),
            "13th-16th Century" to listOf("Solomonic Dynasty", "Muslim Sultanates", "Conflict and Interaction"),
            "16th-18th Century" to listOf("Oromo Population Movements", "Gondarine Period", "Zemene Mesafint"),
            "1800-1941" to listOf("State Building", "Modernization", "Italian Occupation"),
            "1941-1995" to listOf("Imperial Era", "Derg Regime", "Federal System")
        )
        seedDetailedSubject(dao, subjectId, "History of Ethiopia", units)
    }

    private suspend fun seedAnthropology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Scope and Features of Anthropology"),
            "Branches" to listOf("Physical", "Archaeology", "Linguistics", "Cultural"),
            "Human Culture" to listOf("Relativism vs Ethnocentrism", "Characteristics of Culture"),
            "Marginalized Groups" to listOf("Gender and Age Vulnerability"),
            "Ethnic Relations" to listOf("Identity and Multiculturalism in Ethiopia"),
            "Indigenous Knowledge" to listOf("Customary Governance", "Peace-making Systems")
        )
        seedDetailedSubject(dao, subjectId, "Social Anthropology", units)
    }

    private suspend fun seedEmergingTech(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Evolution of Tech", "Industrial Revolutions"),
            "Data Science" to listOf("Data Acquisition", "Lifecycle of Data"),
            "Artificial Intelligence" to listOf("Types of AI", "Applications in Health and Agriculture"),
            "Internet of Things" to listOf("Sensors and Smart Cities", "IoT Architecture"),
            "AR, VR and MR" to listOf("Virtual and Augmented Reality Applications"),
            "Ethics and Professionalism" to listOf("Privacy", "Security", "Future of Work"),
            "Other Technologies" to listOf("Blockchain", "Cloud Computing", "Quantum Computing")
        )
        seedDetailedSubject(dao, subjectId, "Emerging Technologies", units)
    }

    private suspend fun seedCivics(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Ethics and Morality" to listOf("Definitions", "Course Goals", "Importance of Civics"),
            "Ethical Theories" to listOf("Teleological Theories", "Deontological Theories", "Virtue Ethics", "Rights-based Ethics"),
            "Ethical Decision Making" to listOf("Decision Factors", "Moral Reasoning", "Choice Frameworks"),
            "State and Citizenship" to listOf("Theories of State", "Systems of Government", "Acquiring Citizenship"),
            "Constitution and Democracy" to listOf("History of Constitutions", "Democratic Principles", "Human Rights Standards")
        )
        seedDetailedSubject(dao, subjectId, "Moral & Civics", units)
    }

    private suspend fun seedFitness(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Concepts of Fitness" to listOf("Definitions", "Training Principles", "Factors Affecting Fitness"),
            "Health Benefits" to listOf("Impact on Body", "Preventing Hypokinetic Diseases"),
            "Nutrition" to listOf("Nutrition Basics", "Diet and Performance", "Eating Practices"),
            "Health-Related Fitness" to listOf("Cardiovascular Endurance", "Muscular Strength", "Flexibility", "Body Composition"),
            "Assessment" to listOf("Fitness Measurement", "Personalized conditioning programs")
        )
        seedDetailedSubject(dao, subjectId, "Physical Fitness", units)
    }

    private suspend fun seedInclusiveness(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Disabilities and Vulnerabilities" to listOf("Definitions", "Types of Disabilities", "Historical Movements"),
            "Concept of Inclusion" to listOf("Meaning and Benefits", "Barriers to Inclusion"),
            "Assessment of Needs" to listOf("Identification Techniques", "Screening Tools", "Early Intervention"),
            "Inclusive Culture" to listOf("School and Workplace Environments", "Differentiated Services"),
            "Inclusive Community" to listOf("Participation and Diversity", "Equal Opportunity"),
            "Policy Frameworks" to listOf("International Conventions", "Ethiopian Legal Provisions")
        )
        seedDetailedSubject(dao, subjectId, "Inclusiveness", units)
    }

    private suspend fun seedGlobalTrends(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "International Relations" to listOf("Evolution of System", "State/Non-state Actors", "IR Theories"),
            "Foreign Policy" to listOf("National Interest", "Diplomacy Tools", "Ethiopian Foreign Policy History"),
            "International Political Economy" to listOf("Politics vs Economics", "Global Trade/Finance", "WTO, IMF, World Bank"),
            "International Law" to listOf("Nature of Law", "Sovereignty", "State Responsibility"),
            "Regionalism and Globalization" to listOf("Globalization Debates", "African Union", "EU"),
            "Global Issues" to listOf("Security Threats", "Climate Change", "Inequality")
        )
        seedDetailedSubject(dao, subjectId, "Global Trends", units)
    }

    private suspend fun seedMathNat(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Logic and Set Theory" to listOf("Propositions", "Truth Tables", "Set Operations"),
            "Number Systems" to listOf("Real Numbers", "Mathematical Induction", "Complex Numbers"),
            "Functions" to listOf("Polynomial and Rational", "Logarithmic and Exponential", "Trigonometric and Hyperbolic"),
            "Analytic Geometry" to listOf("Line Equations", "Conic Sections: Circle, Parabola, Ellipse, Hyperbola")
        )
        seedDetailedSubject(dao, subjectId, "Math for Nat Science", units)
    }

    private suspend fun seedPhysics(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Preliminaries" to listOf("Measurement", "Vectors"),
            "Kinematics" to listOf("1D and 2D Motion", "Projectile Motion"),
            "Dynamics" to listOf("Newton's Laws", "Friction", "Circular Motion"),
            "Energy" to listOf("Work-Energy Theorem", "Conservation of Energy"),
            "Momentum" to listOf("Linear Momentum", "Collisions"),
            "Gravitation" to listOf("Gravitation", "Kepler's Laws"),
            "Fluid Mechanics" to listOf("Density and Pressure", "Archimedes' Principle", "Bernoulli's Equation"),
            "Thermodynamics" to listOf("Heat Transfer", "Laws of Thermodynamics"),
            "Waves" to listOf("Simple Harmonic Motion", "Sound Waves"),
            "Electromagnetism" to listOf("Electric Fields", "Circuits", "Magnetic Fields"),
            "Optics" to listOf("Reflection and Refraction", "Mirrors and Lenses"),
            "Applications" to listOf("Physics in Health and Industry")
        )
        seedDetailedSubject(dao, subjectId, "General Physics", units)
    }

    private suspend fun seedChemistry(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Essential Ideas" to listOf("Scientific Method", "States of Matter"),
            "Atoms and Molecules" to listOf("Atomic Theory", "Isotopes", "Nomenclature"),
            "Electronic Structure" to listOf("Quantum Numbers", "Electron Configuration", "Periodic Trends"),
            "Chemical Bonding" to listOf("Lewis Structures", "VSEPR Theory", "Polarity"),
            "Stoichiometry" to listOf("The Mole Concept", "Limiting Reactants"),
            "Solution Chemistry" to listOf("Concentration Units", "Electrolyte Solutions", "Buffers"),
            "Thermochemistry" to listOf("Enthalpy", "Entropy", "Gibbs Free Energy"),
            "Kinetics and Equilibrium" to listOf("Reaction Rates", "Le Chatelier's Principle")
        )
        seedDetailedSubject(dao, subjectId, "General Chemistry", units)
    }

    private suspend fun seedBiology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Scope of Biology", "Origin of Life"),
            "Biological Molecules" to listOf("Carbohydrates", "Proteins", "Nucleic Acids", "Vitamins"),
            "Cellular Basis" to listOf("Organelles", "Membrane Structure", "Cell Cycle"),
            "Metabolism" to listOf("Enzymes", "Respiration", "Photosynthesis"),
            "Genetics" to listOf("Mendelian Genetics", "DNA Replication", "Natural Selection"),
            "Infectious Diseases" to listOf("Bacteria and Viruses", "Immune Response"),
            "Taxonomy" to listOf("Five-kingdom System", "Binomial Nomenclature"),
            "Ecology" to listOf("Ecosystems", "Energy Flow", "Resource Management"),
            "Botany and Zoology" to listOf("Features of Plants", "Features of Animals"),
            "Applications" to listOf("Biotechnology", "Medicine")
        )
        seedDetailedSubject(dao, subjectId, "General Biology", units)
    }

    private suspend fun seedMathSoc(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Logic and Sets" to listOf("Logical Connectives", "Quantifiers", "Venn Diagrams"),
            "Real Numbers and Functions" to listOf("Real Number Properties", "Types of Functions"),
            "Matrices" to listOf("Matrix Algebra", "Elementary Row Operations", "Linear Systems"),
            "Calculus" to listOf("Limits and Continuity", "Basic Derivatives"),
            "Business Applications" to listOf("Linear Programming", "Simple and Compound Interest")
        )
        seedDetailedSubject(dao, subjectId, "Math for Soc Science", units)
    }

    private suspend fun seedEconomics(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Scarcity and Choice", "Micro vs Macro"),
            "Demand and Supply" to listOf("Laws of Demand/Supply", "Market Equilibrium"),
            "Consumer Behavior" to listOf("Utility Theory", "Indifference Curves"),
            "Production and Cost" to listOf("Production Functions", "Law of Diminishing Returns", "Cost Analysis"),
            "Market Structure" to listOf("Perfect Competition", "Monopoly", "Oligopoly"),
            "Macroeconomics" to listOf("National Income (GDP)", "Unemployment and Inflation", "Fiscal Policy")
        )
        seedDetailedSubject(dao, subjectId, "Intro to Economics", units)
    }

    private suspend fun seedEntrepreneurship(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Nature of Entrepreneurship" to listOf("Qualities of Entrepreneurs", "Economic Development Role"),
            "Business Planning" to listOf("Process of Writing Plan", "Startup Success Factors"),
            "Business Formation" to listOf("Forms of Ownership", "Legal Requirements in Ethiopia"),
            "Product Development" to listOf("Innovation and Creativity", "Development Procedures"),
            "Marketing Basics" to listOf("Marketing Mix (4 Ps)", "Market Research"),
            "Financing" to listOf("Sources of Capital", "Microfinance and Banks"),
            "Ethics and CSR" to listOf("Business Ethics", "Social Responsibility")
        )
        seedDetailedSubject(dao, subjectId, "Entrepreneurship", units)
    }

    private suspend fun seedGenericMockData(dao: CatalogueDao, subjectId: Long, name: String) {
        for (chNum in 1..3) {
            val chapterId = dao.insertChapter(
                Chapter(subjectId = subjectId, number = chNum, title = "Mastery of $name")
            )
            for (topNum in 1..2) {
                val topicId = dao.insertTopic(
                    Topic(chapterId = chapterId, title = "Topic $chNum.$topNum: Core Concepts")
                )
                dao.insertHandout(
                    Handout(topicId = topicId, content = "Detailed study material for $name - Topic $chNum.$topNum.")
                )
            }
        }
    }

    private suspend fun seedUtilities(dao: CatalogueDao, subjectId: Long, subjectName: String) {
        val utilities = when {
            subjectName.contains("Math") -> listOf(
                Utility(subjectId = subjectId, name = "Scientific Calculator", type = "CALCULATOR", icon = "calculate"),
                Utility(subjectId = subjectId, name = "Formula Sheet", type = "DOCUMENT", icon = "description")
            )
            subjectName.contains("Chemistry") -> listOf(
                Utility(subjectId = subjectId, name = "Periodic Table", type = "IMAGE", icon = "grid_view")
            )
            subjectName.contains("Physics") -> listOf(
                Utility(subjectId = subjectId, name = "Constants Reference", type = "DOCUMENT", icon = "list_alt")
            )
            else -> emptyList<Utility>()
        }
        for (utility in utilities) {
            dao.insertUtility(utility)
        }
    }
}
