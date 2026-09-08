package com.awra.catalogue.ui.paywall

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.kyant.capsule.ContinuousRoundedRectangle

data class PricingPlan(
    val id: String,
    val title: String,
    val price: String,
    val billing: String,
    val tag: String? = null,
    val isBestValue: Boolean = false
)

val AvailablePlans = listOf(
    PricingPlan("Decoy", "Monthly Decoy", "50 ETB", "Renewed monthly"),
    PricingPlan("Pro", "Yearly Pro", "450 ETB", "Billed annually", tag = "Save 25%", isBestValue = true),
    PricingPlan("Ultimate", "Lifetime Ultimate", "999 ETB", "One-time payment")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PaywallScreen(
    onBackClick: () -> Unit,
    onPlanSelected: (String) -> Unit
) {
    var selectedPlanId by remember { mutableStateOf("Pro") }
    val selectedPlan = remember(selectedPlanId) { AvailablePlans.find { it.id == selectedPlanId }!! }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            item {
                HeaderSection()
            }

            // Features Section
            item {
                FeaturesSection()
            }

            // Plans Section
            item {
                PlansSection(
                    selectedPlanId = selectedPlanId,
                    onPlanChange = { selectedPlanId = it }
                )
            }

            // Subscribe Button
            item {
                Button(
                    onClick = { onPlanSelected(selectedPlanId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(64.dp),
                    shape = ContinuousRoundedRectangle(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPlan.isBestValue) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Unlock for ${selectedPlan.price}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }

            // Footer Links
            item {
                FooterSection()
            }
        }

        // Floating Close Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f))
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("premiume.lottie"))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(ContinuousRoundedRectangle(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Master Your Courses\nwith Premium",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join 10,000+ students excelling in their freshman year.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FeatureItem(
            icon = Icons.Default.CloudDownload,
            title = "Offline Access",
            description = "Study anywhere, even without internet."
        )
        FeatureItem(
            icon = Icons.Default.Quiz,
            title = "Unlimited Quizzes",
            description = "Access all question banks and explanations."
        )
        FeatureItem(
            icon = Icons.Default.Analytics,
            title = "Advanced Analytics",
            description = "Track your mastery and identify weak spots."
        )
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlansSection(selectedPlanId: String, onPlanChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        AvailablePlans.forEachIndexed { index, plan ->
            PlanCard(
                title = plan.title,
                price = plan.price,
                billing = plan.billing,
                isSelected = selectedPlanId == plan.id,
                onClick = { onPlanChange(plan.id) },
                tag = plan.tag,
                isBestValue = plan.isBestValue,
                index = index,
                count = AvailablePlans.size
            )
            
            if (index < AvailablePlans.size - 1) {
                Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlanCard(
    title: String,
    price: String,
    billing: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String? = null,
    isBestValue: Boolean = false,
    index: Int = 0,
    count: Int = 1
) {
    val outerRadius = 32.dp
    val innerRadius = 8.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    // Use theme colors for integration
    val accentColor = if (isBestValue) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    val borderColor = if (isSelected) accentColor 
                     else if (isBestValue) accentColor.copy(alpha = 0.5f)
                     else Color.Transparent
    
    val containerColor = if (isBestValue && isSelected) accentColor.copy(alpha = 0.2f)
                         else if (isBestValue) accentColor.copy(alpha = 0.1f)
                         else MaterialTheme.colorScheme.surfaceContainerHigh

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 3.dp else if (isBestValue) 2.dp else 0.dp,
                color = borderColor,
                shape = itemShape
            ),
        shape = itemShape,
        color = containerColor,
        tonalElevation = if (isBestValue) 12.dp else 2.dp
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            title, 
                            fontWeight = FontWeight.Black, 
                            fontSize = 18.sp,
                            color = if (isBestValue) accentColor else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (tag != null) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(accentColor)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    tag, 
                                    color = if (isBestValue) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        billing, 
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    price, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 20.sp, 
                    color = if (isSelected || isBestValue) accentColor else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Restore Purchase",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Privacy Policy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Text("Terms of Service", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
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
