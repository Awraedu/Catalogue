package com.awra.catalogue.data.database

import android.content.Context
import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.seed.*
import com.awra.catalogue.data.util.QuizLoader

object DatabaseSeeder {
    val initialSubjects = listOf(
        // Common Subjects
        Subject(name = "Communicative English I", description = "Foundational language skills.", stream = Stream.COMMON, shape = ExpressiveShape.CIRCLE),
        Subject(name = "Communicative English II", description = "Advanced academic writing.", stream = Stream.COMMON, shape = ExpressiveShape.OVAL),
        Subject(name = "General Psychology", description = "Introduction to human behavior.", stream = Stream.COMMON, shape = ExpressiveShape.PILL),
        Subject(name = "Social Anthropology", description = "Study of human societies and cultures.", stream = Stream.COMMON, shape = ExpressiveShape.SEMICIRCLE),
        Subject(name = "Critical Thinking", description = "Logic and reasoning skills.", stream = Stream.COMMON, shape = ExpressiveShape.ARCH),
        Subject(name = "Moral and Civic Education", description = "Ethics and citizenship.", stream = Stream.COMMON, shape = ExpressiveShape.BUN),
        Subject(name = "Inclusiveness", description = "Diverse learners and special needs.", stream = Stream.COMMON, shape = ExpressiveShape.FAN),
        Subject(name = "Entrepreneurship", description = "Business mindset and innovation.", stream = Stream.COMMON, shape = ExpressiveShape.HEART),
        Subject(name = "Emerging Technologies", description = "AI, IoT, and future tech.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_4),
        Subject(name = "Fitness", description = "Physical health and well-being.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_6),
        Subject(name = "Global Trends", description = "International relations and issues.", stream = Stream.COMMON, shape = ExpressiveShape.COOKIE_7),
        
        // Natural Stream
        Subject(name = "General Physics", description = "Mechanics and thermodynamics.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_9),
        Subject(name = "General Chemistry", description = "Atomic structure and bonding.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_12),
        Subject(name = "General Biology", description = "Cell biology and genetics.", stream = Stream.NATURAL, shape = ExpressiveShape.CLOVER_4),
        Subject(name = "Mathematics for Natural Sciences", description = "Calculus and linear algebra.", stream = Stream.NATURAL, shape = ExpressiveShape.CIRCLE),
        
        // Social Stream
        Subject(name = "Geography of Ethiopia and the Horn", description = "Physical and human geography.", stream = Stream.SOCIAL, shape = ExpressiveShape.OVAL),
        Subject(name = "History of Ethiopia and the Horn", description = "Historical developments in the region.", stream = Stream.SOCIAL, shape = ExpressiveShape.PILL),
        Subject(name = "Introduction to Economics", description = "Micro and macroeconomics principles.", stream = Stream.SOCIAL, shape = ExpressiveShape.SEMICIRCLE),
        Subject(name = "Introduction to Sociology", description = "Social structures and interactions.", stream = Stream.SOCIAL, shape = ExpressiveShape.ARCH),
        Subject(name = "Mathematics for Social Sciences", description = "Probability and statistics.", stream = Stream.SOCIAL, shape = ExpressiveShape.BUN)
    )

    private val subjectFolderMap = mapOf(
        "Communicative English I" to "Communicative_English_I",
        "Communicative English II" to "Communicative_English_II",
        "General Psychology" to "General_Psychology",
        "Social Anthropology" to "Social_Anthropology",
        "Critical Thinking" to "Critical_Thinking",
        "Moral and Civic Education" to "Moral_and_Civic_Education",
        "Inclusiveness" to "Inclusiveness",
        "Entrepreneurship" to "Entrepreneurship",
        "Emerging Technologies" to "Emerging_Technologies",
        "Fitness" to "Fitness",
        "Global Trends" to "Global_Trends",
        "General Physics" to "General_Physics",
        "General Chemistry" to "General_Chemistry",
        "General Biology" to "General_Biology",
        "Mathematics for Natural Sciences" to "Mathematics_for_Natural_Sciences",
        "Geography of Ethiopia and the Horn" to "Geography_of_Ethiopia_and_the_Horn",
        "History of Ethiopia and the Horn" to "History_of_Ethiopia_and_the_Horn",
        "Introduction to Economics" to "Introduction_to_Economics",
        "Introduction to Sociology" to "Introduction_to_Sociology",
        "Mathematics for Social Sciences" to "Mathematics_for_Social_Sciences"
    )

    suspend fun seed(context: Context, dao: CatalogueDao) {
        if (dao.getSubjectCount() > 0) return

        initialSubjects.forEach { subject ->
            val subjectId = dao.insertSubject(subject)
            
            when (subject.name) {
                "Communicative English I" -> seedEnglishI(context, dao, subjectId)
                "Communicative English II" -> seedEnglishII(context, dao, subjectId)
                "General Psychology" -> {
                    seedPsychology(context, dao, subjectId)
                    seedPsychologyUtilities(dao, subjectId)
                }
                "Social Anthropology" -> {
                    seedAnthropology(context, dao, subjectId)
                    seedAnthropologyUtilities(dao, subjectId)
                }
                "Critical Thinking" -> {
                    seedCriticalThinking(context, dao, subjectId)
                    seedLogicUtilities(dao, subjectId)
                }
                "Moral and Civic Education" -> {
                    seedCivics(context, dao, subjectId)
                    seedCivicsUtilities(dao, subjectId)
                }
                "Inclusiveness" -> {
                    seedInclusiveness(context, dao, subjectId)
                    seedInclusivenessUtilities(dao, subjectId)
                }
                "Entrepreneurship" -> {
                    seedEntrepreneurship(context, dao, subjectId)
                    seedEntrepreneurshipUtilities(dao, subjectId)
                }
                "Emerging Technologies" -> {
                    seedEmergingTech(context, dao, subjectId)
                    seedEmergingTechUtilities(dao, subjectId)
                }
                "Fitness" -> {
                    seedFitness(context, dao, subjectId)
                    seedFitnessUtilities(dao, subjectId)
                }
                "Global Trends" -> {
                    seedGlobalTrends(context, dao, subjectId)
                    seedGlobalTrendsUtilities(dao, subjectId)
                }
                "General Physics" -> {
                    seedPhysics(context, dao, subjectId)
                    seedPhysicsUtilities(dao, subjectId)
                }
                "General Chemistry" -> {
                    seedChemistry(context, dao, subjectId)
                    seedChemistryUtilities(dao, subjectId)
                }
                "General Biology" -> {
                    seedBiology(context, dao, subjectId)
                    seedBiologyUtilities(dao, subjectId)
                }
                "Mathematics for Natural Sciences" -> {
                    seedNaturalMath(context, dao, subjectId)
                    seedMathUtilities(dao, subjectId)
                }
                "Geography of Ethiopia and the Horn" -> {
                    seedGeography(context, dao, subjectId)
                    seedGeographyUtilities(dao, subjectId)
                }
                "History of Ethiopia and the Horn" -> seedHistory(context, dao, subjectId)
                "Introduction to Economics" -> {
                    seedEconomics(context, dao, subjectId)
                    seedEconomicsUtilities(dao, subjectId)
                }
                "Introduction to Sociology" -> seedSociology(context, dao, subjectId)
                "Mathematics for Social Sciences" -> {
                    seedSocialMath(context, dao, subjectId)
                    seedMathUtilities(dao, subjectId)
                }
                else -> seedGenericSubject(context, dao, subjectId, subject.name)
            }
        }
    }

    private suspend fun seedInclusiveness(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Inclusiveness", InclusivenessData.units)
    }

    private suspend fun seedInclusivenessUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Disability Types", type = "DISABILITY_TYPES", icon = "Accessible"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Sign Language", type = "SIGN_LANGUAGE", icon = "SignLanguage"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Braille Chart", type = "BRAILLE", icon = "Braille"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Inclusive Design", type = "INCLUSIVE_DESIGN", icon = "DesignServices"))
    }

    private suspend fun seedEntrepreneurship(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Entrepreneurship", EntrepreneurshipData.units)
    }

    private suspend fun seedEntrepreneurshipUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Business Model Canvas", type = "BMC", icon = "ViewQuilt"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Startup Financials", type = "STARTUP_FINANCE", icon = "AccountBalanceWallet"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Lean Startup Loop", type = "LEAN_STARTUP", icon = "Autorenew"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Growth Metrics", type = "GROWTH_METRICS", icon = "Insights"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Pitch Deck Structure", type = "PITCH_DECK", icon = "PresentToAll"))
    }

    private suspend fun seedEmergingTech(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Emerging Technologies", EmergingTechData.units)
    }

    private suspend fun seedEmergingTechUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "AI Ethics", type = "AI_ETHICS", icon = "AutoAwesome"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "IoT Architecture", type = "IOT_STACK", icon = "Layers"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Blockchain Visualizer", type = "BLOCKCHAIN_VIS", icon = "Link"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Cloud Service Models", type = "CLOUD_MODELS", icon = "Cloud"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Tech Roadmap", type = "TECH_TIMELINE", icon = "Timeline"))
    }

    private suspend fun seedFitness(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Fitness", FitnessData.units)
    }

    private suspend fun seedFitnessUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "BMI Calculator", type = "BMI_CALC", icon = "Calculate"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Nutrition Guide", type = "NUTRITION", icon = "Restaurant"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Training Principles", type = "TRAINING_PRO", icon = "FitnessCenter"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Heart Rate Zones", type = "HR_ZONES", icon = "Favorite"))
    }

    private suspend fun seedGlobalTrends(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Global Trends", GlobalTrendsData.units)
    }

    private suspend fun seedGlobalTrendsUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "UN & International Orgs", type = "INT_ORGS", icon = "Language"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "African Union (AU)", type = "AU_HUB", icon = "Flag"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Sustainable Goals (SDG)", type = "SDG_GRID", icon = "AutoGraph"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Geopolitical Concepts", type = "GEO_CONCEPTS", icon = "Gavel"))
    }

    private suspend fun seedSocialMath(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Mathematics for Social Sciences", MathData.socialMathUnits)
    }

    private suspend fun seedDetailedSubject(
        context: Context,
        dao: CatalogueDao,
        subjectId: Long,
        subjectName: String,
        units: List<Pair<String, List<String>>>
    ) {
        val folderName = subjectFolderMap[subjectName] ?: "generic"
        
        // Load all quizzes for the subject
        val easyQuizzes = QuizLoader.loadQuizzes(context, folderName, Difficulty.EASY)
        val mediumQuizzes = QuizLoader.loadQuizzes(context, folderName, Difficulty.MEDIUM)
        val hardQuizzes = QuizLoader.loadQuizzes(context, folderName, Difficulty.HARD)
        val allJsonQuizzes = easyQuizzes + mediumQuizzes + hardQuizzes

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
                
                // Seed quizzes from JSON if available for this topic, otherwise fallback
                val topicQuizzes = allJsonQuizzes.filter { 
                    it.chapter.contains(unitTitle, ignoreCase = true) && 
                    it.topic.contains(topicTitle, ignoreCase = true) 
                }

                if (topicQuizzes.isNotEmpty()) {
                    dao.insertQuizQuestions(topicQuizzes.map { jsonQ ->
                        QuizQuestion(
                            topicId = topicId,
                            question = jsonQ.question,
                            options = jsonQ.choices,
                            correctOptionIndex = jsonQ.answerIndex,
                            difficulty = when {
                                easyQuizzes.contains(jsonQ) -> Difficulty.EASY
                                mediumQuizzes.contains(jsonQ) -> Difficulty.MEDIUM
                                else -> Difficulty.HARD
                            },
                            hint = jsonQ.tip,
                            explanation = jsonQ.explanation
                        )
                    })
                } else {
                    // Fallback mock quiz questions
                    dao.insertQuizQuestions(listOf(
                        QuizQuestion(
                            topicId = topicId,
                            question = "Core concept of $topicTitle?",
                            options = listOf("Answer 1", "Answer 2", "Answer 3", "Answer 4"),
                            correctOptionIndex = 0,
                            difficulty = Difficulty.EASY,
                            hint = "Review the basic definitions.",
                            explanation = "This is the foundational principle of the topic."
                        )
                    ))
                }

                // Seed some mock flashcards
                dao.insertFlashcards(listOf(
                    Flashcard(
                        topicId = topicId,
                        front = "Define $topicTitle",
                        back = "The study and application of $topicTitle in the context of $subjectName.",
                        difficulty = Difficulty.EASY
                    )
                ))

                // Real Terms for Dictionary (Sample per Topic)
                val termWord = when {
                    topicTitle.contains("Logic") -> "Logic"
                    topicTitle.contains("Culture") -> "Culture"
                    topicTitle.contains("Physics") -> "Physics"
                    topicTitle.contains("Kinematics") -> "Kinematics"
                    topicTitle.contains("Thermodynamics") -> "Thermodynamics"
                    topicTitle.contains("Psychology") -> "Psychology"
                    topicTitle.contains("Anthropology") -> "Anthropology"
                    topicTitle.contains("Ethics") -> "Ethics"
                    topicTitle.contains("Civics") -> "Civics"
                    topicTitle.contains("Geology") -> "Geology"
                    topicTitle.contains("Economy") -> "Economy"
                    topicTitle.contains("Sociology") -> "Sociology"
                    else -> topicTitle.split(" ").first()
                }
                
                val termDef = when (termWord) {
                    "Logic" -> "The study of the principles of correct reasoning and valid arguments."
                    "Culture" -> "The learned and shared beliefs, values, and practices of a society."
                    "Physics" -> "The branch of science concerned with the nature and properties of matter and energy."
                    "Kinematics" -> "The branch of mechanics concerned with the motion of objects without reference to forces."
                    "Thermodynamics" -> "The study of the relationships between heat, work, temperature, and energy."
                    "Psychology" -> "The scientific study of the human mind and its functions, especially behavior."
                    "Anthropology" -> "The comparative study of human societies and cultures and their development."
                    "Ethics" -> "The branch of philosophy that involves systematizing, defending, and recommending concepts of right and wrong behavior."
                    "Civics" -> "The study of the rights and duties of citizenship."
                    "Geology" -> "The science that deals with the earth's physical structure and substance."
                    "Economy" -> "The wealth and resources of a country or region, especially in terms of the production and consumption of goods and services."
                    "Sociology" -> "The study of the development, structure, and functioning of human society."
                    else -> "Key terminology and fundamental principle relating to $topicTitle."
                }
                
                dao.insertTerms(listOf(
                    Term(
                        topicId = topicId,
                        word = termWord,
                        definition = termDef
                    )
                ))

                // Seed Infographics
                if (subjectName == "General Biology" && topicTitle == "Introduction to Biology") {
                    dao.insertInfographic(Infographic(topicId = topicId, imageUrl = "bio.png"))
                }
            }
        }
    }

    private suspend fun seedEnglishI(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Communicative English I", EnglishData.englishIUnits)
        seedEnglishUtilities(dao, subjectId)
    }

    private suspend fun seedEnglishII(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Communicative English II", EnglishData.englishIIUnits)
        seedEnglishUtilities(dao, subjectId)
    }

    private suspend fun seedEnglishUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Grammar Rules", type = "GRAMMAR", icon = "MenuBook"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Literary Devices", type = "LITERARY", icon = "AutoStories"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Writing Styles", type = "WRITING", icon = "HistoryEdu"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Phonetic Chart", type = "PHONETIC", icon = "RecordVoiceOver"))
    }

    private suspend fun seedGeographyUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Interactive World Map", type = "WORLD_MAP", icon = "Public"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Climate Zones", type = "CLIMATE_ZONES", icon = "WbSunny"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Earth Layers", type = "EARTH_LAYERS", icon = "Layers"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Ethiopia Regions", type = "ETHIOPIA_REGIONS", icon = "Map"))
    }

    private suspend fun seedPsychologyUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Psychological Perspectives", type = "PSYCH_PERSPECTIVE", icon = "AutoAwesome"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Defense Mechanisms", type = "DEFENSE_MECH", icon = "Shield"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Developmental Stages", type = "DEV_STAGES", icon = "Timeline"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Brain Regions", type = "BRAIN_REGIONS", icon = "Psychology"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Mental Health Resources", type = "HOTLINE", icon = "SupportAgent"))
    }

    private suspend fun seedPhysicsUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Formula Bank", type = "FORMULA_BANK", icon = "Functions"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Waves and Optics", type = "WAVES_OPTICS", icon = "Visibility"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Electromagnetism", type = "ELECTROMAGNETISM", icon = "Bolt"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Physical Constants", type = "PHYS_CONSTANTS", icon = "Numbers"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Circuit Symbols", type = "CIRCUIT_SYMBOLS", icon = "Schema"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Dynamics and Energy", type = "DYNAMICS_ENERGY", icon = "FitnessCenter"))
    }

    private suspend fun seedMathUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Derivative Table", type = "DERIVATIVES", icon = "Timeline"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Integration Formulas", type = "INTEGRALS", icon = "Functions"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Trig Identities", type = "TRIG_IDENTITY", icon = "ChangeHistory"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Geometry Formulas", type = "GEOMETRY", icon = "SquareFoot"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Logarithm Laws", type = "LOG_LAWS", icon = "Percent"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Statistics Guide", type = "STATS_GUIDE", icon = "BarChart"))
    }

    private suspend fun seedEconomicsUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Economic Formulas", type = "ECON_FORMULA", icon = "TrendingUp"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Market Structures", type = "MARKET_STRUCTURES", icon = "Storefront"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Supply & Demand", type = "SUPPLY_DEMAND", icon = "CallMerge"))
    }

    private suspend fun seedChemistryUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Periodic Table", type = "PERIODIC_TABLE", icon = "GridOn"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Solubility Rules", type = "SOLUBILITY_RULES", icon = "WaterDrop"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Gas Law Tool", type = "GAS_LAWS", icon = "Air"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Stoichiometry Tool", type = "STOICHIOMETRY", icon = "Calculate"))
    }

    private suspend fun seedBiologyUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Organelle Library", type = "ORGANELLE_LIB", icon = "Dataset"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Body Systems", type = "BODY_SYSTEMS", icon = "AccessibilityNew"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Kingdoms of Life", type = "KINGDOMS_OF_LIFE", icon = "Diversity3"))
    }

    private suspend fun seedPsychology(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "General Psychology", PsychologyData.units)
    }

    private suspend fun seedAnthropology(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Social Anthropology", AnthropologyData.units)
    }

    private suspend fun seedAnthropologyUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Evolution Stages", type = "EVOLUTION_STAGES", icon = "Timeline"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Culture Concept", type = "CULTURE_CONCEPT", icon = "Groups"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Archeological Methods", type = "ARCH_METHODS", icon = "HistoryEdu"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Human Variation", type = "HUMAN_VARIATION", icon = "Face"))
    }

    private suspend fun seedLogicUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Logical Fallacies", type = "LOGICAL_FALLACIES", icon = "Gavel"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Logic Operators", type = "LOGIC_OPERATORS", icon = "Code"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Reasoning Types", type = "REASONING_TYPES", icon = "Psychology"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Truth Tables", type = "TRUTH_TABLE", icon = "GridOn"))
    }

    private suspend fun seedCriticalThinking(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Critical Thinking", LogicData.units)
    }

    private suspend fun seedCivics(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Moral and Civic Education", CivicsData.units)
    }

    private suspend fun seedCivicsUtilities(dao: CatalogueDao, subjectId: Long) {
        dao.insertUtility(Utility(subjectId = subjectId, name = "Constitution Explorer", type = "CONST_EXPLORER", icon = "MenuBook"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Human Rights Charter", type = "HR_CHARTER", icon = "Gavel"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Government Structure", type = "GOV_STRUCTURE", icon = "AccountBalance"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "National Symbols", type = "NAT_SYMBOL", icon = "Flag"))
        dao.insertUtility(Utility(subjectId = subjectId, name = "Civic Duties", type = "CIVIC_DUTIES", icon = "Person"))
    }

    private suspend fun seedPhysics(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "General Physics", PhysicsData.units)
    }

    private suspend fun seedChemistry(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "General Chemistry", ChemistryData.units)
    }

    private suspend fun seedBiology(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "General Biology", BiologyData.units)
    }

    private suspend fun seedNaturalMath(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Mathematics for Natural Sciences", MathData.naturalMathUnits)
    }

    private suspend fun seedGeography(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Geography of Ethiopia and the Horn", GeographyData.units)
    }

    private suspend fun seedHistory(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "History of Ethiopia and the Horn", HistoryData.units)
    }

    private suspend fun seedEconomics(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Introduction to Economics", EconomicsData.units)
    }

    private suspend fun seedSociology(context: Context, dao: CatalogueDao, subjectId: Long) {
        seedDetailedSubject(context, dao, subjectId, "Introduction to Sociology", SociologyData.units)
    }

    private suspend fun seedGenericSubject(context: Context, dao: CatalogueDao, subjectId: Long, subjectName: String) {
        val units = listOf(
            "Unit 1: Introduction" to listOf("Basic Concepts", "Fundamental Theories"),
            "Unit 2: Core Principles" to listOf("Main Principles", "Standard Applications"),
            "Unit 3: Advanced Topics" to listOf("Complex Systems", "Future Trends")
        )
        seedDetailedSubject(context, dao, subjectId, subjectName, units)
    }
}
