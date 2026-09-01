package com.awra.catalogue.ui.utility.tools.entrepreneurship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun PitchDeckStructureContent(onClick: (UtilityItemData) -> Unit) {
    val slides = listOf(
        PitchSlide("The Vision", "1-sentence hook", "Your elevator pitch. High-level vision of the company."),
        PitchSlide("The Problem", "Define the pain point", "What real-world problem are you solving? Use numbers/facts."),
        PitchSlide("The Solution", "Your value proposition", "How your product solves the problem. Show the benefits."),
        PitchSlide("Market Opportunity", "Size and potential", "TAM, SAM, SOM analysis. Who is the target customer?"),
        PitchSlide("Business Model", "How you make money", "Pricing strategy, sales channels, and revenue streams."),
        PitchSlide("The Team", "Why you?", "Key members, relevant experience, and shared successes."),
        PitchSlide("The Competition", "Competitive landscape", "Who are the competitors? What is your secret sauce?"),
        PitchSlide("Marketing & Growth", "Go-to-market plan", "How will you acquire users and scale the business?"),
        PitchSlide("The Ask", "What do you need?", "Amount of funding, resources, or partnership requested.")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Investor Pitch Deck",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(slides) { index, slide ->
                Surface(
                    onClick = {
                        onClick(
                            UtilityItemData(
                                title = "Slide ${index + 1}: ${slide.title}",
                                shortDesc = slide.goal,
                                detailedExplanation = slide.details,
                                examples = emptyList(),
                                realWorldUse = "Used to secure funding from VCs and Angel Investors."
                            )
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    (index + 1).toString(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(Modifier.width(16.dp))
                        
                        Column {
                            Text(slide.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(slide.goal, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private data class PitchSlide(val title: String, val goal: String, val details: String)
