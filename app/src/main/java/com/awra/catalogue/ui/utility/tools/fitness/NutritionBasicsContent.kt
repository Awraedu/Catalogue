package com.awra.catalogue.ui.utility.tools.fitness

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun NutritionBasicsContent(onClick: (UtilityItemData) -> Unit) {
    val nutrients = listOf(
        UtilityItemData(
            "Proteins",
            "The building blocks of life",
            "Proteins are essential for building and repairing tissues, making enzymes and hormones, and supporting immune function.",
            listOf("Meat", "Eggs", "Legumes", "Dairy"),
            "Critical for muscle growth and recovery after exercise."
        ),
        UtilityItemData(
            "Carbohydrates",
            "Primary source of energy",
            "Carbs provide glucose, which is converted into energy used to support bodily functions and physical activity.",
            listOf("Grains", "Fruits", "Vegetables", "Starchy tubers"),
            "Fuel for high-intensity training and brain function."
        ),
        UtilityItemData(
            "Fats",
            "Concentrated energy source",
            "Fats are necessary for hormone production, cell membrane integrity, and the absorption of fat-soluble vitamins (A, D, E, K).",
            listOf("Avocado", "Nuts", "Olive Oil", "Fatty Fish"),
            "Important for long-term health and insulation."
        ),
        UtilityItemData(
            "Vitamins & Minerals",
            "Micronutrients for metabolism",
            "Needed in small quantities to support a wide range of physiological functions including bone health and immune response.",
            listOf("Leafy greens", "Citrus fruits", "Whole grains"),
            "Prevent deficiencies and optimize performance."
        ),
        UtilityItemData(
            "Hydration",
            "The medium for all life",
            "Water regulates body temperature, lubricates joints, and transports nutrients throughout the body.",
            listOf("Pure water", "Electrolyte drinks", "Fruits with high water content"),
            "Crucial during and after exercise to prevent dehydration."
        )
    )
    UtilityList(nutrients, onClick)
}
