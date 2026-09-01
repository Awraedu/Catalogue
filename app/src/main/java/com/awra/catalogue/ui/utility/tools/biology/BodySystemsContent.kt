package com.awra.catalogue.ui.utility.tools.biology

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun BodySystemsContent(onClick: (UtilityItemData) -> Unit) {
    val systems = listOf(
        UtilityItemData(
            "Circulatory System",
            "Transports oxygen and nutrients",
            "Consists of heart, blood, and blood vessels. Moves blood throughout the body to deliver nutrients and remove waste.",
            listOf("Heart pumping blood", "Arteries vs Veins"),
            "Critical for medicine and exercise physiology."
        ),
        UtilityItemData(
            "Nervous System",
            "Control and communication",
            "Includes brain, spinal cord, and nerves. Sends electrical signals to coordinate all body activities.",
            listOf("Reflex actions", "Sensory processing"),
            "Basis of psychology and neurology."
        ),
        UtilityItemData(
            "Respiratory System",
            "Gas exchange",
            "Lungs, trachea, and diaphragm work together to bring in oxygen and expel carbon dioxide.",
            listOf("Gas exchange in alveoli", "Breathing mechanics"),
            "Involved in asthma and respiratory health."
        ),
        UtilityItemData(
            "Digestive System",
            "Nutrient absorption",
            "Breaks down food into smaller molecules that cells can use for energy.",
            listOf("Enzymatic breakdown in stomach", "Absorption in small intestine"),
            "Nutrition and metabolic health."
        ),
        UtilityItemData(
            "Skeletal System",
            "Support and protection",
            "Framework of bones and cartilage that protects organs and allows movement.",
            listOf("Marrow producing blood cells", "Skull protecting the brain"),
            "Orthopedics and sports science."
        ),
        UtilityItemData(
            "Muscular System",
            "Movement and heat production",
            "Works with the skeleton to move the body. Skeletal, cardiac, and smooth muscle types.",
            listOf("Bicep contraction", "Involuntary heart muscle"),
            "Physical therapy and strength training."
        )
    )
    UtilityList(systems, onClick)
}
