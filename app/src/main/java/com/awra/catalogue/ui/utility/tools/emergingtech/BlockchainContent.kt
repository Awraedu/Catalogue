package com.awra.catalogue.ui.utility.tools.emergingtech

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun BlockchainContent(onClick: (UtilityItemData) -> Unit) {
    val scrollState = rememberScrollState()
    val blocks = listOf(
        BlockData("Block #0 (Genesis)", "0000", "001A", "Init: System Online"),
        BlockData("Block #1", "001A", "A5B2", "Tx: User A -> User B (50 BTC)"),
        BlockData("Block #2", "A5B2", "C99F", "Tx: Smart Contract Executed"),
        BlockData("Block #3", "C99F", "E421", "Tx: Property Deed Registered")
    )

    Column(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
        Text(
            "Visualizing the Chain",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            blocks.forEachIndexed { index, block ->
                BlockCard(block, onClick)
                if (index < blocks.size - 1) {
                    Icon(
                        Icons.Default.Link,
                        contentDescription = "Link",
                        modifier = Modifier.size(32.dp).padding(horizontal = 4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Text(
            "Each block contains its own hash and the hash of the previous block, creating an immutable chain.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BlockCard(block: BlockData, onClick: (UtilityItemData) -> Unit) {
    Surface(
        onClick = {
            onClick(
                UtilityItemData(
                    title = block.id,
                    shortDesc = "Hash: ${block.hash}",
                    detailedExplanation = "This block records: ${block.data}. It is linked to the previous hash ${block.prevHash}.",
                    examples = listOf("Consensus: Proof of Work", "Network: Peer-to-Peer"),
                    realWorldUse = "Decentralized finance, supply chain tracking, and digital identity."
                )
            )
        },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(block.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            
            HashInfo("Prev Hash", block.prevHash)
            HashInfo("Curr Hash", block.hash)
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            
            Text("Data:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(block.data, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

@Composable
private fun HashInfo(label: String, hash: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                hash, 
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private data class BlockData(val id: String, val prevHash: String, val hash: String, val data: String)
