package com.awra.catalogue.data.seed

object CatalogueMetadata {
    fun getUnitsForSubject(subjectName: String): List<Pair<String, List<String>>> {
        return when (subjectName) {
            "Communicative English I" -> EnglishData.englishIUnits
            "Communicative English II" -> EnglishData.englishIIUnits
            "General Psychology" -> PsychologyData.units
            "Social Anthropology" -> AnthropologyData.units
            "Critical Thinking" -> LogicData.units
            "Moral and Civic Education" -> CivicsData.units
            "General Physics" -> PhysicsData.units
            "General Chemistry" -> ChemistryData.units
            "General Biology" -> BiologyData.units
            "Mathematics for Natural Sciences" -> MathData.naturalMathUnits
            "Mathematics for Social Sciences" -> MathData.socialMathUnits
            "Geography of Ethiopia and the Horn" -> GeographyData.units
            "History of Ethiopia and the Horn" -> HistoryData.units
            "Introduction to Economics" -> EconomicsData.units
            "Introduction to Sociology" -> SociologyData.units
            "Inclusiveness" -> InclusivenessData.units
            "Entrepreneurship" -> EntrepreneurshipData.units
            "Emerging Technologies" -> EmergingTechData.units
            "Fitness" -> FitnessData.units
            "Global Trends" -> GlobalTrendsData.units
            else -> emptyList()
        }
    }
}
