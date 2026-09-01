package com.awra.catalogue.ui.utility.tools.physics

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun FormulaBankContent(onClick: (UtilityItemData) -> Unit) {
    val formulas = listOf(
        UtilityItemData(
            "Kinematics",
            "v = u + at, s = ut + ½at², v² = u² + 2as",
            "Equations of motion for uniform acceleration.",
            listOf("Calculating braking distance of a car", "Determining the time for an object to fall"),
            "Automotive safety engineering and projectile calculations."
        ),
        UtilityItemData(
            "Dynamics",
            "F = ma, p = mv, J = Δp",
            "Newton's laws, momentum, and impulse.",
            listOf("Calculating force needed to move a load", "Collision analysis in sports"),
            "Mechanical engineering and accident reconstruction."
        ),
        UtilityItemData(
            "Work & Energy",
            "W = Fd cosθ, KE = ½mv², PE = mgh",
            "Energy forms and the work-energy theorem.",
            listOf("Calculating efficiency of a pulley", "Determining potential energy of a dam"),
            "Renewable energy systems and structural design."
        ),
        UtilityItemData(
            "Circular Motion",
            "a_c = v²/r, F_c = mv²/r",
            "Centripetal acceleration and force.",
            listOf("Design of curved roads (banking)", "Satellite orbital velocity"),
            "Civil engineering and aerospace."
        ),
        UtilityItemData(
            "Rotational Dynamics",
            "τ = rF sinθ, L = Iω, KE_rot = ½Iω²",
            "Torque, angular momentum, and rotational kinetic energy.",
            listOf("Tightening a bolt with a wrench", "Figure skater's spin"),
            "Robotics and mechanical systems."
        )
    )
    UtilityList(formulas, onClick)
}
