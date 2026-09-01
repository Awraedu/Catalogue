package com.awra.catalogue.ui.utility.tools.physics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun DynamicsEnergyContent(onClick: (UtilityItemData) -> Unit) {
    val items = listOf(
        UtilityItemData(
            "Conservation of Energy",
            "E_total = KE + PE = Constant",
            "Energy cannot be created or destroyed, only transformed from one form to another.",
            listOf("Roller coaster mechanical energy", "Pendulum swing"),
            "Thermodynamics and structural engineering."
        ),
        UtilityItemData(
            "Power",
            "P = W/t = Fv",
            "The rate at which work is done or energy is transferred.",
            listOf("Motor power ratings", "Human metabolic rate"),
            "Mechanical and electrical engineering."
        ),
        UtilityItemData(
            "Elasticity",
            "F = -kx, PE_s = ½kx²",
            "Hooke's law and elastic potential energy stored in springs.",
            listOf("Shock absorbers in cars", "Exercise bands"),
            "Material science and mechanical design."
        ),
        UtilityItemData(
            "Universal Gravitation",
            "F = G(m₁m₂)/r²",
            "Newton's law describing the gravitational pull between two masses.",
            listOf("Calculating weight on different planets", "Orbiting planets"),
            "Astrophysics and space exploration."
        )
    )
    UtilityList(items, onClick)
}
