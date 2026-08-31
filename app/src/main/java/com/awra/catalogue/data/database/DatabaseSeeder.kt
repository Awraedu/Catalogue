package com.awra.catalogue.data.database

import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.model.*

object DatabaseSeeder {
    val initialSubjects = listOf(
        // Common Subjects
        Subject(name = "Communicative English I", description = "Foundational language skills.", stream = Stream.COMMON, shape = ExpressiveShape.CIRCLE),
        Subject(name = "Communicative English II", description = "Advanced academic writing.", stream = Stream.COMMON, shape = ExpressiveShape.OVAL),
        Subject(name = "General Psychology", description = "Introduction to human behavior.", stream = Stream.COMMON, shape = ExpressiveShape.PILL),
        Subject(name = "Social Anthropology", description = "Study of human societies and cultures.", stream = Stream.COMMON, shape = ExpressiveShape.SEMICIRCLE),
        Subject(name = "Critical Thinking", description = "Logic and reasoning skills.", stream = Stream.COMMON, shape = ExpressiveShape.ARCH),
        Subject(name = "Moral and Civic Education", description = "Ethics and citizenship.", stream = Stream.COMMON, shape = ExpressiveShape.BUN),
        
        // Natural Stream
        Subject(name = "General Physics", description = "Mechanics and thermodynamics.", stream = Stream.NATURAL, shape = ExpressiveShape.FAN),
        Subject(name = "General Chemistry", description = "Atomic structure and bonding.", stream = Stream.NATURAL, shape = ExpressiveShape.HEART),
        Subject(name = "General Biology", description = "Cell biology and genetics.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_4),
        Subject(name = "Mathematics for Natural Sciences", description = "Calculus and linear algebra.", stream = Stream.NATURAL, shape = ExpressiveShape.COOKIE_6),
        
        // Social Stream
        Subject(name = "Geography of Ethiopia and the Horn", description = "Physical and human geography.", stream = Stream.SOCIAL, shape = ExpressiveShape.COOKIE_7),
        Subject(name = "History of Ethiopia and the Horn", description = "Historical developments in the region.", stream = Stream.SOCIAL, shape = ExpressiveShape.COOKIE_9),
        Subject(name = "Introduction to Economics", description = "Micro and macroeconomics principles.", stream = Stream.SOCIAL, shape = ExpressiveShape.COOKIE_12),
        Subject(name = "Introduction to Sociology", description = "Social structures and interactions.", stream = Stream.SOCIAL, shape = ExpressiveShape.CLOVER_4)
    )

    suspend fun seed(dao: CatalogueDao) {
        if (dao.getSubjectCount() > 0) return

        initialSubjects.forEach { subject ->
            val subjectId = dao.insertSubject(subject)
            
            when (subject.name) {
                "Communicative English I" -> seedEnglishI(dao, subjectId)
                "Communicative English II" -> seedEnglishII(dao, subjectId)
                "General Psychology" -> seedPsychology(dao, subjectId)
                "Social Anthropology" -> seedAnthropology(dao, subjectId)
                "Critical Thinking" -> seedCriticalThinking(dao, subjectId)
                "Moral and Civic Education" -> seedCivics(dao, subjectId)
                "General Physics" -> seedPhysics(dao, subjectId)
                "General Chemistry" -> seedChemistry(dao, subjectId)
                "General Biology" -> seedBiology(dao, subjectId)
                "Mathematics for Natural Sciences" -> seedNaturalMath(dao, subjectId)
                "Geography of Ethiopia and the Horn" -> seedGeography(dao, subjectId)
                "History of Ethiopia and the Horn" -> seedHistory(dao, subjectId)
                "Introduction to Economics" -> seedEconomics(dao, subjectId)
                "Introduction to Sociology" -> seedSociology(dao, subjectId)
                else -> seedGenericSubject(dao, subjectId, subject.name)
            }
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
                
                // Seed some mock quiz questions
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
            }
        }
    }

    private suspend fun seedEnglishI(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Study Skills" to listOf("Lecture Note-taking", "Reading for Study", "Modals for Advice"),
            "Health and Fitness" to listOf("Healthy Lifestyles", "Conditionals", "Health Vocabulary"),
            "Cultural Values" to listOf("Cultural Tourism", "Cultural Diversity", "Verb Tenses"),
            "Wildlife" to listOf("Conservation Threats", "Passive Voice", "Endemic Animals"),
            "Population" to listOf("Demographics", "Reported Speech", "Summarizing Data")
        )
        seedDetailedSubject(dao, subjectId, "Communicative English I", units)
    }

    private suspend fun seedEnglishII(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Life Skills" to listOf("Interpersonal Skills", "Paragraph Development"),
            "Self-concept" to listOf("Developing Self-esteem", "Expressing Strengths"),
            "Environmental Protection" to listOf("Global Warming", "Conditional Sentences"),
            "Communication Skills" to listOf("Active Listening", "Business Correspondence"),
            "Time Management" to listOf("Overcoming Procrastination", "Planning Tasks")
        )
        seedDetailedSubject(dao, subjectId, "Communicative English II", units)
    }

    private suspend fun seedPsychology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Essence of Psychology" to listOf("Definition and Goals", "Modern Perspectives"),
            "Sensation and Perception" to listOf("Sensation Adaptation", "Perceptual Illusions"),
            "Learning" to listOf("Classical Conditioning", "Operant Conditioning"),
            "Memory" to listOf("Short-term Memory", "Long-term Memory"),
            "Motivation and Emotion" to listOf("Theories of Motivation", "Components of Emotion"),
            "Personality" to listOf("Psychoanalytic Theory", "Trait Theories"),
            "Psychological Disorders" to listOf("Anxiety Disorders", "Mood Disorders"),
            "Life Skills" to listOf("Self-management", "Critical Thinking")
        )
        seedDetailedSubject(dao, subjectId, "General Psychology", units)
    }

    private suspend fun seedAnthropology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introducing Anthropology" to listOf("Etymology and Scope", "Holism and Relativism"),
            "Sub-fields" to listOf("Socio-cultural", "Biological", "Archaeology"),
            "Culture" to listOf("Conceptualizing Culture", "Marriage and Kinship"),
            "Inclusiveness" to listOf("Concepts of Exclusion", "Marginalized Groups"),
            "Identity" to listOf("Race and Ethnicity", "Multiculturalism"),
            "Indigenous Knowledge" to listOf("The Gadaa System", "Gurage Sera"),
            "Culture Areas" to listOf("Plough Culture", "Enset Culture", "Pastoralism")
        )
        seedDetailedSubject(dao, subjectId, "Social Anthropology", units)
    }

    private suspend fun seedCriticalThinking(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introducing Philosophy" to listOf("Meaning and Nature", "Core Fields"),
            "Basic Logic" to listOf("Arguments and Premises", "Deduction and Induction"),
            "Logic and Language" to listOf("Functions of Language", "Definitions and Rules"),
            "Informal Fallacies" to listOf("Fallacies of Relevance", "Fallacies of Ambiguity"),
            "Categorical Propositions" to listOf("Standard Forms", "Venn Diagrams")
        )
        seedDetailedSubject(dao, subjectId, "Critical Thinking", units)
    }

    private suspend fun seedCivics(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Understanding Civics" to listOf("Defining Ethics", "Moral vs Legal Rules"),
            "Approaches to Ethics" to listOf("Normative Ethics", "Virtue Ethics"),
            "Decision Making" to listOf("Moral Judgments", "Responsibility"),
            "State and Citizenship" to listOf("Theories of State", "Rights and Duties"),
            "Constitution" to listOf("FDRE Constitution", "Democratic Values")
        )
        seedDetailedSubject(dao, subjectId, "Moral and Civic Education", units)
    }

    private suspend fun seedPhysics(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Vectors" to listOf("Vector Addition", "Vector Multiplication"),
            "Kinematics" to listOf("Uniform Motion", "Projectile Motion"),
            "Dynamics" to listOf("Newton's Laws", "Friction"),
            "Work and Energy" to listOf("Kinetic Energy", "Conservation of Energy"),
            "Rotational Motion" to listOf("Angular Variables", "Torque"),
            "Thermodynamics" to listOf("Temperature and Heat", "Heat Engines")
        )
        seedDetailedSubject(dao, subjectId, "General Physics", units)
    }

    private suspend fun seedChemistry(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Atoms" to listOf("Atomic Structure", "Periodic Table"),
            "Stoichiometry" to listOf("The Mole Concept", "Chemical Equations"),
            "Bonding" to listOf("Ionic Bonding", "Covalent Bonding"),
            "States of Matter" to listOf("Gases", "Liquids and Solids"),
            "Equilibrium" to listOf("Equilibrium Constants", "Le Chatelier's Principle"),
            "Acid-Base" to listOf("pH Concept", "Theories of Acids")
        )
        seedDetailedSubject(dao, subjectId, "General Chemistry", units)
    }

    private suspend fun seedBiology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Molecules" to listOf("Carbohydrates", "Proteins", "Nucleic Acids"),
            "Cell Structure" to listOf("Organelles", "Plasma Membrane"),
            "Metabolism" to listOf("Enzymes", "Photosynthesis"),
            "Genetics" to listOf("DNA Replication", "Mendelian Genetics"),
            "Evolution" to listOf("Natural Selection", "Human Evolution"),
            "Taxonomy" to listOf("Classification Systems", "Binary Nomenclature"),
            "Ecology" to listOf("Ecosystems", "Conservation Biology")
        )
        seedDetailedSubject(dao, subjectId, "General Biology", units)
    }

    private suspend fun seedNaturalMath(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Logic and Sets" to listOf("Propositional Logic", "Operations on Sets"),
            "Number Systems" to listOf("Real Numbers", "Complex Numbers"),
            "Functions" to listOf("Polynomial Functions", "Trigonometric Functions"),
            "Analytic Geometry" to listOf("Distance Formula", "Conic Sections")
        )
        seedDetailedSubject(dao, subjectId, "Mathematics for Natural Sciences", units)
    }

    private suspend fun seedGeography(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Scope" to listOf("Themes of Geography", "Location of Ethiopia"),
            "Geology" to listOf("Geological History", "Rocks and Minerals"),
            "Topography" to listOf("Physiographic Divisions", "The Rift Valley"),
            "Drainage" to listOf("River Basins", "Lakes of Ethiopia"),
            "Climate" to listOf("Factors affecting Climate", "Seasons in Ethiopia"),
            "Resources" to listOf("Soil Types", "Wildlife Conservation"),
            "Population" to listOf("Distribution and Density", "Migration Patterns"),
            "Economy" to listOf("Agriculture", "Manufacturing")
        )
        seedDetailedSubject(dao, subjectId, "Geography of Ethiopia and the Horn", units)
    }

    private suspend fun seedHistory(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Nature of History" to listOf("Sources of History", "Historiography"),
            "Peoples" to listOf("Human Origins", "Languages and Peoples"),
            "Ancient Polities" to listOf("The Aksumite Empire", "The Zagwe Dynasty"),
            "13th-16th Century" to listOf("Solomonic Dynasty", "Islamic Sultanates"),
            "16th-19th Century" to listOf("Oromo Movement", "Gondarine Period"),
            "1800-1941" to listOf("Modern Empire", "Italian Occupation"),
            "1941-Present" to listOf("Post-liberation", "The Derg Regime")
        )
        seedDetailedSubject(dao, subjectId, "History of Ethiopia and the Horn", units)
    }

    private suspend fun seedEconomics(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Nature" to listOf("Scarcity and Choice", "Micro vs Macroeconomics"),
            "Demand and Supply" to listOf("Law of Demand", "Market Equilibrium"),
            "Market Structures" to listOf("Perfect Competition", "Monopoly"),
            "Macroeconomics" to listOf("Measuring GDP", "Inflation and Unemployment"),
            "Growth" to listOf("Economic Growth", "Ethiopian Policies")
        )
        seedDetailedSubject(dao, subjectId, "Introduction to Economics", units)
    }

    private suspend fun seedSociology(dao: CatalogueDao, subjectId: Long) {
        val units = listOf(
            "Introduction" to listOf("Development of Sociology", "Research Methods"),
            "Culture" to listOf("Types of Societies", "Cultural Diversity"),
            "Socialization" to listOf("Agents of Socialization", "Nature vs Nurture"),
            "Organization" to listOf("Status and Roles", "Bureaucracy"),
            "Stratification" to listOf("Social Class", "Social Mobility"),
            "Social Problems" to listOf("Poverty", "Crime and Deviance"),
            "Social Action" to listOf("Community Development", "Social Policy")
        )
        seedDetailedSubject(dao, subjectId, "Introduction to Sociology", units)
    }

    private suspend fun seedGenericSubject(dao: CatalogueDao, subjectId: Long, subjectName: String) {
        val units = listOf(
            "Unit 1: Introduction" to listOf("Basic Concepts", "Fundamental Theories"),
            "Unit 2: Core Principles" to listOf("Main Principles", "Standard Applications"),
            "Unit 3: Advanced Topics" to listOf("Complex Systems", "Future Trends")
        )
        seedDetailedSubject(dao, subjectId, subjectName, units)
    }
}
