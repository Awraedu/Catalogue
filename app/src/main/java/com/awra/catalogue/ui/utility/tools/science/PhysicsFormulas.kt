package com.awra.catalogue.ui.utility.tools.science

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun PhysicsFormulasContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData(
            "Velocity (v)", 
            "v = Δx / Δt", 
            "The rate of change of displacement with time. Measured in meters per second (m/s).",
            listOf("A car traveling 100m in 5s has a velocity of 20m/s."),
            "Used in traffic safety and mechanical engineering."
        ),
        UtilityItemData(
            "Acceleration (a)", 
            "a = Δv / Δt", 
            "The rate of change of velocity with time. Measured in m/s².",
            listOf("A car speeding up from 0 to 20m/s in 4s has an acceleration of 5m/s²."),
            "Crucial for calculating forces and impact."
        ),
        UtilityItemData(
            "Newton's Second Law", 
            "F = m * a", 
            "The force acting on an object is equal to the mass of that object times its acceleration.",
            listOf("Pushing a 10kg cart with 5m/s² acceleration requires 50N of force."),
            "Universal law for classical mechanics."
        )
    )
    UtilityList(formulas, onClick)
}
