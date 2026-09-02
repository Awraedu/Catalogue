package com.awra.catalogue.data.seed

object MathData {
    val naturalMathUnits = listOf(
        "Logic and Set Theory" to listOf("Propositional Logic", "Quantifiers", "Arguments", "Operations on Sets"),
        "Number Systems" to listOf("Real Number System", "Complex Numbers", "Polar Form"),
        "Functions" to listOf("Polynomial Functions", "Rational Functions", "Trigonometric Functions", "Exponential and Logarithmic"),
        "Analytic Geometry" to listOf("Distance Formula", "Conic Sections: Circles", "Parabolas", "Ellipses and Hyperbolas")
    )

    val socialMathUnits = listOf(
        "Foundations of Math" to listOf("Basic Algebra", "Functions and Graphs", "Equation Solving"),
        "Data and Statistics" to listOf("Descriptive Statistics", "Probability Basics", "Data Interpretation"),
        "Business Math" to listOf("Interest and Annuities", "Linear Programming", "Decision Modeling")
    )
}
