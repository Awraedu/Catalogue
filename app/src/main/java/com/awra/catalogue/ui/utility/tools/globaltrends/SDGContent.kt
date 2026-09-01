package com.awra.catalogue.ui.utility.tools.globaltrends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun SDGContent(onClick: (UtilityItemData) -> Unit) {
    val sdgs = listOf(
        "No Poverty", "Zero Hunger", "Good Health", "Quality Education",
        "Gender Equality", "Clean Water", "Affordable Energy", "Decent Work",
        "Industry & Innovation", "Reduced Inequalities", "Sustainable Cities",
        "Responsible Consumption", "Climate Action", "Life Below Water",
        "Life on Land", "Peace & Justice", "Partnerships"
    )

    val colors = listOf(
        Color(0xFFE5243B), Color(0xFFDDA63A), Color(0xFF4C9F38), Color(0xFFC5192D),
        Color(0xFFFF3A21), Color(0xFF26BDE2), Color(0xFFFCC30B), Color(0xFFA21942),
        Color(0xFFFD6925), Color(0xFFDD1367), Color(0xFFFD9D24), Color(0xFFBF8B2E),
        Color(0xFF3F7E44), Color(0xFF0A97D9), Color(0xFF56C02B), Color(0xFF00689D),
        Color(0xFF19486A)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "17 Sustainable Development Goals",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(sdgs) { index, goal ->
                val goalColor = colors[index % colors.size]
                Surface(
                    onClick = {
                        onClick(UtilityItemData(
                            title = "Goal ${index + 1}: $goal",
                            shortDesc = "SDG Target",
                            detailedExplanation = "Adopted by the UN in 2015 as a universal call to action to end poverty and protect the planet.",
                            examples = emptyList(),
                            realWorldUse = "Part of the 2030 Agenda for Sustainable Development."
                        ))
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = goalColor,
                    tonalElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.aspectRatio(1f).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                (index + 1).toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                goal,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                lineHeight = 12.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
