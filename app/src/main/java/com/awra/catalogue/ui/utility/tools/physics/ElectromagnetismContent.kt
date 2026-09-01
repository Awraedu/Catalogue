package com.awra.catalogue.ui.utility.tools.physics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun ElectromagnetismContent(onClick: (UtilityItemData) -> Unit) {
    val items = listOf(
        UtilityItemData(
            "Electrostatics",
            "F = k|q₁q₂|/r², E = F/q",
            "Coulomb's law and electric field strength.",
            listOf("Static electricity attraction", "Inkjet printers"),
            "Semiconductor manufacturing and air purification."
        ),
        UtilityItemData(
            "Electric Potential",
            "V = W/q, V = kq/r",
            "Electric potential energy and voltage at a point.",
            listOf("Battery voltage", "Capacitor storage"),
            "Power supply design and electronics."
        ),
        UtilityItemData(
            "Circuits",
            "V = IR, P = IV = I²R",
            "Ohm's law and electrical power dissipation.",
            listOf("Designing home wiring", "Estimating energy costs"),
            "Electrical engineering."
        ),
        UtilityItemData(
            "Magnetism",
            "F = qvB sinθ, F = ILB sinθ",
            "Magnetic force on moving charges and current-carrying wires.",
            listOf("Electric motors", "Magnetic resonance imaging (MRI)"),
            "Modern medicine and transportation (Maglev)."
        ),
        UtilityItemData(
            "Electromagnetic Induction",
            "ε = -N(ΔΦ/Δt)",
            "Faraday's law of induction for induced EMF.",
            listOf("Electrical generators", "Transformers"),
            "Power grid infrastructure."
        )
    )
    UtilityList(items, onClick)
}
