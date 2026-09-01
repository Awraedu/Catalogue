package com.awra.catalogue.ui.utility.tools.logic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.SectionTitle
import com.awra.catalogue.ui.utility.TruthTableDisplay

@Composable
fun TruthTablesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Negation (~P)")
                Text("Reverses the truth value.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "~P"),
                    rows = listOf(
                        listOf("T", "F"),
                        listOf("F", "T")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Conjunction (P ∧ Q)")
                Text("True only if both P and Q are true.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P ∧ Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "F"),
                        listOf("F", "T", "F"),
                        listOf("F", "F", "F")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Disjunction (P ∨ Q)")
                Text("False only if both P and Q are false.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P ∨ Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "T"),
                        listOf("F", "T", "T"),
                        listOf("F", "F", "F")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Conditional (P → Q)")
                Text("False only when P is true and Q is false.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P → Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "F"),
                        listOf("F", "T", "T"),
                        listOf("F", "F", "T")
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionTitle("Biconditional (P ↔ Q)")
                Text("True only if P and Q have the same truth value.", style = MaterialTheme.typography.bodyMedium)
                TruthTableDisplay(
                    headers = listOf("P", "Q", "P ↔ Q"),
                    rows = listOf(
                        listOf("T", "T", "T"),
                        listOf("T", "F", "F"),
                        listOf("F", "T", "F"),
                        listOf("F", "F", "T")
                    )
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}
