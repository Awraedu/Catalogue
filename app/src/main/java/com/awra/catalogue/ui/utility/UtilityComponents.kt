package com.awra.catalogue.ui.utility

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle

data class UtilityItemData(
    val title: String,
    val shortDesc: String,
    val detailedExplanation: String = "",
    val examples: List<String> = emptyList(),
    val realWorldUse: String = ""
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RuleCard(item: UtilityItemData, index: Int, count: Int, onClick: () -> Unit) {
    val outerRadius = 32.dp
    val innerRadius = 8.dp
    val itemShape = when {
        count == 1 -> ContinuousRoundedRectangle(outerRadius)
        index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
        index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
        else -> RoundedCornerShape(innerRadius)
    }

    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text(item.shortDesc, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun UtilityList(items: List<UtilityItemData>, onClick: (UtilityItemData) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        itemsIndexed(items) { index, item ->
            RuleCard(item, index, items.size, onClick = { onClick(item) })
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 4.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun TruthTableDisplay(headers: List<String>, rows: List<List<String>>) {
    Surface(
        shape = ContinuousRoundedRectangle(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            // Header Row - Fills the top width
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                headers.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Column divider
                    if (index < headers.size - 1) {
                        VerticalDivider(
                            modifier = Modifier.height(20.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                // Data Rows
                rows.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEachIndexed { colIndex, cell ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val isResult = colIndex == row.size - 1
                                val isTrue = cell == "T"
                                
                                Surface(
                                    shape = CircleShape,
                                    color = when {
                                        isTrue && isResult -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                        !isTrue && isResult -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                        else -> Color.Transparent
                                    },
                                    modifier = Modifier.sizeIn(minWidth = 36.dp, minHeight = 36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = cell,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isResult) FontWeight.Black else FontWeight.Bold,
                                            color = when {
                                                isTrue -> if (isResult) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                else -> if (isResult) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Row column divider
                            if (colIndex < row.size - 1) {
                                VerticalDivider(
                                    modifier = Modifier.height(24.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                    
                    if (rowIndex < rows.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UtilityDetailSheetContent(item: UtilityItemData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Explanation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (item.detailedExplanation.isNotEmpty()) item.detailedExplanation else item.shortDesc,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }

        if (item.examples.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Examples",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                item.examples.forEach { example ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        if (item.realWorldUse.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Real World Application",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.realWorldUse,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
