package com.awra.catalogue.ui.utility.tools.english

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.SectionTitle
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun PhoneticChart() {
    val vowels = listOf("i:", "ɪ", "ʊ", "u:", "e", "ə", "ɜ:", "ɔ:", "æ", "ʌ", "ɑ:", "ɒ")
    val diphthongs = listOf("ɪə", "eɪ", "ʊə", "ɔɪ", "əʊ", "eə", "aɪ", "aʊ")
    val consonants = listOf("p", "b", "t", "d", "tʃ", "dʒ", "k", "g", "f", "v", "θ", "ð", "s", "z", "ʃ", "ʒ", "m", "n", "ŋ", "h", "l", "r", "w", "j")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item { SectionTitle("Monophthongs (Vowels)") }
        item { PhoneticGrid(vowels) }

        item { SectionTitle("Diphthongs") }
        item { PhoneticGrid(diphthongs) }

        item { SectionTitle("Consonants") }
        item { PhoneticGrid(consonants) }
        
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhoneticGrid(items: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4
    ) {
        items.forEach { symbol ->
            PhoneticTile(symbol, modifier = Modifier.weight(1f))
        }
        // Fill empty slots in last row to maintain alignment if needed
        val remain = items.size % 4
        if (remain != 0) {
            repeat(4 - remain) {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PhoneticTile(symbol: String, modifier: Modifier = Modifier) {
    Surface(
        onClick = { /* Could show details or play sound */ },
        shape = ContinuousRoundedRectangle(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.aspectRatio(1f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
