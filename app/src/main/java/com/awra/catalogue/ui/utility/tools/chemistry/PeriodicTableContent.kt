package com.awra.catalogue.ui.utility.tools.chemistry

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun PeriodicTableContent(onClick: (UtilityItemData) -> Unit) {
    val elements = remember { getPeriodicTableData() }
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxSize()) {
        // Legend Section
        CategoryLegend(colorScheme)
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp).alpha(0.5f))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .verticalScroll(verticalScrollState)
                    .padding(16.dp)
            ) {
                val maxRow = elements.maxOf { it.row }
                val maxCol = 18

                for (r in 1..maxRow) {
                    if (r == 8) {
                        Spacer(modifier = Modifier.height(24.dp))
                        continue
                    }

                    Row {
                        for (c in 1..maxCol) {
                            val element = elements.find { it.row == r && it.column == c }
                            if (element != null) {
                                ElementCard(element, colorScheme, onClick)
                            } else {
                                Box(modifier = Modifier.size(64.dp).padding(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryLegend(colorScheme: ColorScheme) {
    val categories = remember(colorScheme) {
        listOf(
            "Alkali Metal" to getCategoryColors("alkali metal", colorScheme),
            "Alkaline Earth" to getCategoryColors("alkaline earth metal", colorScheme),
            "Transition Metal" to getCategoryColors("transition metal", colorScheme),
            "Lanthanide" to getCategoryColors("lanthanide", colorScheme),
            "Actinide" to getCategoryColors("actinide", colorScheme),
            "Post-transition" to getCategoryColors("post-transition metal", colorScheme),
            "Metalloid" to getCategoryColors("metalloid", colorScheme),
            "Nonmetal" to getCategoryColors("nonmetal", colorScheme),
            "Halogen" to getCategoryColors("halogen", colorScheme),
            "Noble Gas" to getCategoryColors("noble gas", colorScheme)
        )
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { pair ->
            val (name, colors) = pair
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colors.container)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ElementCard(element: Element, colorScheme: ColorScheme, onClick: (UtilityItemData) -> Unit) {
    val colors = getCategoryColors(element.category, colorScheme)
    
    Surface(
        onClick = {
            onClick(
                UtilityItemData(
                    title = element.symbol,
                    shortDesc = element.name,
                    detailedExplanation = element.category,
                    examples = listOf(element.number.toString()),
                    realWorldUse = getElementUsage(element.symbol)
                )
            )
        },
        modifier = Modifier
            .size(64.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(12.dp),
        color = colors.container,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = element.number.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                color = colors.onContainer.copy(alpha = 0.8f)
            )
            Text(
                text = element.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = colors.onContainer
            )
            Text(
                text = element.name,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = colors.onContainer.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ElementDetailSheet(item: UtilityItemData) {
    val colorScheme = MaterialTheme.colorScheme
    val category = item.detailedExplanation
    val number = item.examples.firstOrNull() ?: ""
    val symbol = item.title
    val name = item.shortDesc
    val usage = item.realWorldUse
    val colors = getCategoryColors(category, colorScheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Section
        Surface(
            modifier = Modifier.size(140.dp),
            shape = RoundedCornerShape(32.dp),
            color = colors.container,
            tonalElevation = 8.dp,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = number, 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold,
                    color = colors.onContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = symbol, 
                    style = MaterialTheme.typography.displayLarge, 
                    fontWeight = FontWeight.Black,
                    color = colors.onContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onSurface
        )

        Surface(
            color = colors.container.copy(alpha = 0.3f),
            shape = CircleShape,
            modifier = Modifier.padding(top = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.onContainer.copy(alpha = 0.2f))
        ) {
            Text(
                text = category.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onContainer
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Info Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.surfaceContainerHigh,
            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Chemical Properties",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailRow("Atomic Number", number)
                DetailRow("Classification", category.replaceFirstChar { it.uppercase() })
                DetailRow("Block", if (number.toIntOrNull() in 57..71) "f-block (Lanthanide)" else if (number.toIntOrNull() in 89..103) "f-block (Actinide)" else if (number.toIntOrNull() in listOf(1, 2, 3, 4, 11, 12, 19, 20, 37, 38, 55, 56, 87, 88)) "s-block" else "p/d-block")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Usage Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.primaryContainer.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Real World Application",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = usage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

private data class Element(
    val number: Int,
    val symbol: String,
    val name: String,
    val row: Int,
    val column: Int,
    val category: String
)

private data class CategoryColors(
    val container: Color,
    val onContainer: Color
)

private fun getCategoryColors(category: String, colorScheme: ColorScheme): CategoryColors {
    return when (category) {
        "nonmetal" -> CategoryColors(colorScheme.primaryContainer, colorScheme.onPrimaryContainer)
        "noble gas" -> CategoryColors(colorScheme.secondaryContainer, colorScheme.onSecondaryContainer)
        "alkali metal" -> CategoryColors(colorScheme.tertiaryContainer, colorScheme.onTertiaryContainer)
        "alkaline earth metal" -> CategoryColors(colorScheme.errorContainer, colorScheme.onErrorContainer)
        "metalloid" -> CategoryColors(colorScheme.surfaceVariant, colorScheme.onSurfaceVariant)
        "halogen" -> CategoryColors(colorScheme.inverseSurface, colorScheme.inverseOnSurface)
        "transition metal" -> CategoryColors(colorScheme.primary.copy(alpha = 0.15f), colorScheme.primary)
        "post-transition metal" -> CategoryColors(colorScheme.secondary.copy(alpha = 0.15f), colorScheme.secondary)
        "lanthanide" -> CategoryColors(colorScheme.tertiary.copy(alpha = 0.15f), colorScheme.tertiary)
        "actinide" -> CategoryColors(colorScheme.outlineVariant.copy(alpha = 0.4f), colorScheme.onSurface)
        else -> CategoryColors(colorScheme.surfaceContainer, colorScheme.onSurface)
    }
}

private fun getElementUsage(symbol: String): String {
    return when (symbol) {
        "H" -> "Used in rocket fuel, water synthesis, and ammonia production for fertilizers."
        "He" -> "Critical for filling balloons and cooling superconducting magnets in MRI machines."
        "Li" -> "Found in rechargeable batteries for phones, laptops, and electric vehicles."
        "C" -> "The basic building block of life; also used in steel production and diamond jewelry."
        "O" -> "Essential for human respiration, steel manufacturing, and as a rocket propellant."
        "Fe" -> "Crucial for construction (buildings, bridges) and forming the iron core of Earth."
        "Au" -> "Used in jewelry, investment bars, and highly reliable electronics components."
        "Ag" -> "Key in solar panels, water purification systems, and photographic film."
        "Cu" -> "Primary material for electrical wiring, plumbing pipes, and high-quality cookware."
        "Al" -> "Used for soda cans, kitchen foil, and lightweight components for the aircraft industry."
        else -> "Utilized in specialized industrial processes, scientific research, and complex chemical synthesis."
    }
}

private fun getPeriodicTableData(): List<Element> {
    return listOf(
        Element(1, "H", "Hydrogen", 1, 1, "nonmetal"),
        Element(2, "He", "Helium", 1, 18, "noble gas"),
        Element(3, "Li", "Lithium", 2, 1, "alkali metal"),
        Element(4, "Be", "Beryllium", 2, 2, "alkaline earth metal"),
        Element(5, "B", "Boron", 2, 13, "metalloid"),
        Element(6, "C", "Carbon", 2, 14, "nonmetal"),
        Element(7, "N", "Nitrogen", 2, 15, "nonmetal"),
        Element(8, "O", "Oxygen", 2, 16, "nonmetal"),
        Element(9, "F", "Fluorine", 2, 17, "halogen"),
        Element(10, "Ne", "Neon", 2, 18, "noble gas"),
        Element(11, "Na", "Sodium", 3, 1, "alkali metal"),
        Element(12, "Mg", "Magnesium", 3, 2, "alkaline earth metal"),
        Element(13, "Al", "Aluminum", 3, 13, "post-transition metal"),
        Element(14, "Si", "Silicon", 3, 14, "metalloid"),
        Element(15, "P", "Phosphorus", 3, 15, "nonmetal"),
        Element(16, "S", "Sulfur", 3, 16, "nonmetal"),
        Element(17, "Cl", "Chlorine", 3, 17, "halogen"),
        Element(18, "Ar", "Argon", 3, 18, "noble gas"),
        Element(19, "K", "Potassium", 4, 1, "alkali metal"),
        Element(20, "Ca", "Calcium", 4, 2, "alkaline earth metal"),
        Element(21, "Sc", "Scandium", 4, 3, "transition metal"),
        Element(22, "Ti", "Titanium", 4, 4, "transition metal"),
        Element(23, "V", "Vanadium", 4, 5, "transition metal"),
        Element(24, "Cr", "Chromium", 4, 6, "transition metal"),
        Element(25, "Mn", "Manganese", 4, 7, "transition metal"),
        Element(26, "Fe", "Iron", 4, 8, "transition metal"),
        Element(27, "Co", "Cobalt", 4, 9, "transition metal"),
        Element(28, "Ni", "Nickel", 4, 10, "transition metal"),
        Element(29, "Cu", "Copper", 4, 11, "transition metal"),
        Element(30, "Zn", "Zinc", 4, 12, "transition metal"),
        Element(31, "Ga", "Gallium", 4, 13, "post-transition metal"),
        Element(32, "Ge", "Germanium", 4, 14, "metalloid"),
        Element(33, "As", "Arsenic", 4, 15, "metalloid"),
        Element(34, "Se", "Selenium", 4, 16, "nonmetal"),
        Element(35, "Br", "Bromine", 4, 17, "halogen"),
        Element(36, "Kr", "Krypton", 4, 18, "noble gas"),
        Element(37, "Rb", "Rubidium", 5, 1, "alkali metal"),
        Element(38, "Sr", "Strontium", 5, 2, "alkaline earth metal"),
        Element(39, "Y", "Yttrium", 5, 3, "transition metal"),
        Element(40, "Zr", "Zirconium", 5, 4, "transition metal"),
        Element(41, "Nb", "Niobium", 5, 5, "transition metal"),
        Element(42, "Mo", "Molybdenum", 5, 6, "transition metal"),
        Element(43, "Tc", "Technetium", 5, 7, "transition metal"),
        Element(44, "Ru", "Ruthenium", 5, 8, "transition metal"),
        Element(45, "Rh", "Rhodium", 5, 9, "transition metal"),
        Element(46, "Pd", "Palladium", 5, 10, "transition metal"),
        Element(47, "Ag", "Silver", 5, 11, "transition metal"),
        Element(48, "Cd", "Cadmium", 5, 12, "transition metal"),
        Element(49, "In", "Indium", 5, 13, "post-transition metal"),
        Element(50, "Sn", "Tin", 5, 14, "post-transition metal"),
        Element(51, "Sb", "Antimony", 5, 15, "metalloid"),
        Element(52, "Te", "Tellurium", 5, 16, "metalloid"),
        Element(53, "I", "Iodine", 5, 17, "halogen"),
        Element(54, "Xe", "Xenon", 5, 18, "noble gas"),
        Element(55, "Cs", "Cesium", 6, 1, "alkali metal"),
        Element(56, "Ba", "Barium", 6, 2, "alkaline earth metal"),
        Element(57, "La", "Lanthanum", 9, 4, "lanthanide"),
        Element(58, "Ce", "Cerium", 9, 5, "lanthanide"),
        Element(59, "Pr", "Praseodymium", 9, 6, "lanthanide"),
        Element(60, "Nd", "Neodymium", 9, 7, "lanthanide"),
        Element(61, "Pm", "Promethium", 9, 8, "lanthanide"),
        Element(62, "Sm", "Samarium", 9, 9, "lanthanide"),
        Element(63, "Eu", "Europium", 9, 10, "lanthanide"),
        Element(64, "Gd", "Gadolinium", 9, 11, "lanthanide"),
        Element(65, "Tb", "Terbium", 9, 12, "lanthanide"),
        Element(66, "Dy", "Dysprosium", 9, 13, "lanthanide"),
        Element(67, "Ho", "Holmium", 9, 14, "lanthanide"),
        Element(68, "Er", "Erbium", 9, 15, "lanthanide"),
        Element(69, "Tm", "Thulium", 9, 16, "lanthanide"),
        Element(70, "Yb", "Ytterbium", 9, 17, "lanthanide"),
        Element(71, "Lu", "Lutetium", 9, 18, "lanthanide"),
        Element(72, "Hf", "Hafnium", 6, 4, "transition metal"),
        Element(73, "Ta", "Tantalum", 6, 5, "transition metal"),
        Element(74, "W", "Tungsten", 6, 6, "transition metal"),
        Element(75, "Re", "Rhenium", 6, 7, "transition metal"),
        Element(76, "Os", "Osmium", 6, 8, "transition metal"),
        Element(77, "Ir", "Iridium", 6, 9, "transition metal"),
        Element(78, "Pt", "Platinum", 6, 10, "transition metal"),
        Element(79, "Au", "Gold", 6, 11, "transition metal"),
        Element(80, "Hg", "Mercury", 6, 12, "transition metal"),
        Element(81, "Tl", "Thallium", 6, 13, "post-transition metal"),
        Element(82, "Pb", "Lead", 6, 14, "post-transition metal"),
        Element(83, "Bi", "Bismuth", 6, 15, "post-transition metal"),
        Element(84, "Po", "Polonium", 6, 16, "post-transition metal"),
        Element(85, "At", "Astatine", 6, 17, "halogen"),
        Element(86, "Rn", "Radon", 6, 18, "noble gas"),
        Element(87, "Fr", "Francium", 7, 1, "alkali metal"),
        Element(88, "Ra", "Radium", 7, 2, "alkaline earth metal"),
        Element(89, "Ac", "Actinium", 10, 4, "actinide"),
        Element(90, "Th", "Thorium", 10, 5, "actinide"),
        Element(91, "Pa", "Protactinium", 10, 6, "actinide"),
        Element(92, "U", "Uranium", 10, 7, "actinide"),
        Element(93, "Np", "Neptunium", 10, 8, "actinide"),
        Element(94, "Pu", "Plutonium", 10, 9, "actinide"),
        Element(95, "Am", "Americium", 10, 10, "actinide"),
        Element(96, "Cm", "Curium", 10, 11, "actinide"),
        Element(97, "Bk", "Berkelium", 10, 12, "actinide"),
        Element(98, "Cf", "Californium", 10, 13, "actinide"),
        Element(99, "Es", "Einsteinium", 10, 14, "actinide"),
        Element(100, "Fm", "Fermium", 10, 15, "actinide"),
        Element(101, "Md", "Mendelevium", 10, 16, "actinide"),
        Element(102, "No", "Nobelium", 10, 17, "actinide"),
        Element(103, "Lr", "Lawrencium", 10, 18, "actinide"),
        Element(104, "Rf", "Rutherfordium", 7, 4, "transition metal"),
        Element(105, "Db", "Dubnium", 7, 5, "transition metal"),
        Element(106, "Sg", "Seaborgium", 7, 6, "transition metal"),
        Element(107, "Bh", "Bohrium", 7, 7, "transition metal"),
        Element(108, "Hs", "Hassium", 7, 8, "transition metal"),
        Element(109, "Mt", "Meitnerium", 7, 9, "transition metal"),
        Element(110, "Ds", "Darmstadtium", 7, 10, "transition metal"),
        Element(111, "Rg", "Roentgenium", 7, 11, "transition metal"),
        Element(112, "Cn", "Copernicium", 7, 12, "transition metal"),
        Element(113, "Nh", "Nihonium", 7, 13, "post-transition metal"),
        Element(114, "Fl", "Flerovium", 7, 14, "post-transition metal"),
        Element(115, "Mc", "Moscovium", 7, 15, "post-transition metal"),
        Element(116, "Lv", "Livermorium", 7, 16, "post-transition metal"),
        Element(117, "Ts", "Tennessine", 7, 17, "halogen"),
        Element(118, "Og", "Oganesson", 7, 18, "noble gas")
    )
}
