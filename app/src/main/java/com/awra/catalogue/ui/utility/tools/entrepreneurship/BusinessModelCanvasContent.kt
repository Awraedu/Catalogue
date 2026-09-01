package com.awra.catalogue.ui.utility.tools.entrepreneurship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun BusinessModelCanvasContent(onClick: (UtilityItemData) -> Unit) {
    val blocks = listOf(
        BMCBlock("Customer Segments", "Who are we creating value for?", "Mass market, niche, multi-sided platforms, segmented, diversified."),
        BMCBlock("Value Propositions", "What value do we deliver?", "Newness, performance, customization, getting the job done, design, brand/status, price, cost reduction, risk reduction, accessibility, convenience/usability."),
        BMCBlock("Channels", "How do we reach customers?", "Direct (sales force, web sales, own stores) or Indirect (partner stores, wholesaler)."),
        BMCBlock("Customer Relationships", "What relationship does each segment expect?", "Personal assistance, dedicated personal assistance, self-service, automated services, communities, co-creation."),
        BMCBlock("Revenue Streams", "Where does money come from?", "Asset sale, usage fee, subscription fee, lending/renting/leasing, licensing, brokerage fee, advertising."),
        BMCBlock("Key Resources", "What assets are required?", "Physical, intellectual (brand patents, copyrights, data), human, financial."),
        BMCBlock("Key Activities", "What do we need to do?", "Production, problem-solving, platform/network management."),
        BMCBlock("Key Partnerships", "Who are our key partners?", "Strategic alliances between non-competitors, coopetition (strategic partnerships between competitors), joint ventures to develop new businesses, buyer-supplier relationships."),
        BMCBlock("Cost Structure", "What are the most important costs?", "Fixed costs (salaries, rents, utilities), variable costs, economies of scale, economies of scope.")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "The 9 Building Blocks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            modifier = Modifier.weight(1f)
        ) {
            items(blocks) { block ->
                Surface(
                    onClick = {
                        onClick(
                            UtilityItemData(
                                title = block.name,
                                shortDesc = block.question,
                                detailedExplanation = block.description,
                                examples = listOf("Standard implementation for ${block.name}"),
                                realWorldUse = "Used to map out, design, and pivot business models."
                            )
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = block.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = block.question,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = block.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class BMCBlock(val name: String, val question: String, val description: String)
