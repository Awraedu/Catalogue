package com.awra.catalogue.ui.paywall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle

data class FeatureRow(
    val title: String,
    val freeLimit: String,
    val premiumLimit: String,
    val icon: ImageVector,
    val isHighlightRow: Boolean = false
)

val ComparisonFeatures = listOf(
    FeatureRow("AI Quiz & Explanations", "5/mo.", "✓", Icons.Default.Quiz),
    FeatureRow("Spaced Repetition Flashcards", "20/mo.", "∞", Icons.Default.Style),
    FeatureRow("Offline Encrypted Handouts", "—", "✓", Icons.Default.CloudDownload),
    FeatureRow("Performance Analytics", "—", "✓", Icons.Default.Analytics),
    FeatureRow("Priority Schedule Sync", "—", "✓", Icons.Default.Event),
    FeatureRow("Plan Price", "Free", "450 ETB/yr", Icons.Default.Payments, isHighlightRow = true)
)

/**
 * Sky Blue Sky/Ocean Mesh Gradient Background matching reference image.
 */
@Composable
private fun SkyBluePaywallBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xFF4792FB), // Hardcoded vibrant sky blue
                        0.45f to Color(0xFF60A5FA),
                        0.70f to Color(0xFFC7DFFC), // Soft fading transition
                        1.00f to Color(0xFFFFFFFF)  // Clean white bottom
                    )
                )
            )
    )
}

@Composable
fun PaywallScreen(
    onBackClick: () -> Unit,
    onPlanSelected: (String) -> Unit
) {
    val singlePlanId = "CataloguePremium"
    val singlePlanPrice = "450 ETB"

    Box(modifier = Modifier.fillMaxSize()) {
        // Sky Blue Ocean Mesh Gradient Background
        SkyBluePaywallBackground(modifier = Modifier.fillMaxSize())

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Top Headline
            item {
                PaywallHeader()
            }

            // Side-by-Side Juno Comparison Matrix Table with Unified Premium Glass Capsule
            item {
                SkyBlueComparisonMatrixSection()
            }

            // Action Buttons
            item {
                ActionButtonsSection(
                    planPrice = singlePlanPrice,
                    onSubscribeClick = { onPlanSelected(singlePlanId) },
                    onContinueFreeClick = onBackClick
                )
            }

            // Footer Links
            item {
                PaywallFooter()
            }
        }

        // Top Glassmorphic Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun PaywallHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Unlock the full\nCatalogue experience\nwith Premium",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp,
            color = Color.White
        )
    }
}

/**
 * Juno-style comparison table featuring ONE unified Premium vertical glass capsule container.
 */
@Composable
private fun SkyBlueComparisonMatrixSection() {
    val glassCapsuleShape = ContinuousRoundedRectangle(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Feature Icons + Titles
            Column(
                modifier = Modifier
                    .weight(1.8f)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Table header item
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                ComparisonFeatures.forEach { feature ->
                    Row(
                        modifier = Modifier.height(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            feature.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (feature.isHighlightRow) Color.Black else Color.White
                        )
                        Text(
                            text = feature.title,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (feature.isHighlightRow) FontWeight.Black else FontWeight.SemiBold,
                            color = if (feature.isHighlightRow) Color.Black else Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Middle Column: Free Limits
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Table header item
                Text(
                    text = "Free",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.90f)
                )

                ComparisonFeatures.forEach { feature ->
                    Box(
                        modifier = Modifier.height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feature.freeLimit,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (feature.isHighlightRow) FontWeight.Black else FontWeight.Medium,
                            color = if (feature.isHighlightRow) Color.Black else Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Right Column: ONE SINGLE UNIFIED PREMIUM GLASS CAPSULE CONTAINER
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(glassCapsuleShape)
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), glassCapsuleShape)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Table header item
                Text(
                    text = "Premium",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                ComparisonFeatures.forEach { feature ->
                    Box(
                        modifier = Modifier.height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feature.premiumLimit,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = if (feature.isHighlightRow) Color.Black else Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    planPrice: String,
    onSubscribeClick: () -> Unit,
    onContinueFreeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onSubscribeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = ContinuousRoundedRectangle(29.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = "Unlock Premium for $planPrice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }

        // Translucent glass pill button matching the back button vibe
        Button(
            onClick = onContinueFreeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = ContinuousRoundedRectangle(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.22f),
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
        ) {
            Text(
                text = "Continue for free",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun PaywallFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Restore Purchase",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF64748B),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Privacy Policy", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
            Text("Terms of Service", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaywallPreview() {
    MaterialTheme {
        PaywallScreen(onBackClick = {}, onPlanSelected = {})
    }
}