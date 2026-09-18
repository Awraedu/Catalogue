package com.awra.catalogue.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.model.RecentlyOpenedDisplayItem
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun PinnedItemsSection(
    items: List<RecentlyOpenedDisplayItem>,
    onItemClick: (RecentlyOpenedDisplayItem) -> Unit,
) {
    if (items.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pinned Items",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                PinnedItemView(item = item) {
                    onItemClick(item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PinnedItemView(
    item: RecentlyOpenedDisplayItem,
    onClick: () -> Unit,
) {
    val shape = ContinuousRoundedRectangle(20.dp)
    val subjectColors = getSubjectAdaptiveColors(item.subjectId)
    val displayColor = if (item.type == MaterialType.UTILITY) subjectColors.content else item.color

    val icon = if (item.type == MaterialType.UTILITY) {
        mapUtilityIcon(item.icon)
    } else {
        when (item.type) {
            MaterialType.HANDOUT -> Icons.AutoMirrored.Filled.MenuBook
            MaterialType.INFOGRAPHIC -> Icons.Default.BarChart
            else -> Icons.Default.Category
        }
    }

    Row(
        modifier = Modifier
            .width(180.dp)
            .height(64.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(ContinuousRoundedRectangle(12.dp))
                .background(displayColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = displayColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.type.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun mapUtilityIcon(iconName: String): ImageVector {
    return when (iconName) {
        "MenuBook" -> Icons.AutoMirrored.Filled.MenuBook
        "AutoStories" -> Icons.Default.AutoStories
        "HistoryEdu" -> Icons.Default.HistoryEdu
        "RecordVoiceOver" -> Icons.Default.RecordVoiceOver
        "AutoAwesome" -> Icons.Default.AutoAwesome
        "Shield" -> Icons.Default.Shield
        "Timeline" -> Icons.Default.Timeline
        "Psychology" -> Icons.Default.Psychology
        "SupportAgent" -> Icons.Default.SupportAgent
        "Gavel" -> Icons.Default.Gavel
        "Code" -> Icons.Default.Code
        "Functions" -> Icons.Default.Functions
        "Numbers" -> Icons.Default.Numbers
        "Calculate" -> Icons.Default.Calculate
        "TrendingUp" -> Icons.AutoMirrored.Filled.TrendingUp
        "GridOn" -> Icons.Default.GridOn
        "AccountBalance" -> Icons.Default.AccountBalance
        "Flag" -> Icons.Default.Flag
        "Person" -> Icons.Default.Person
        "Waves" -> Icons.Default.Waves
        "Visibility" -> Icons.Default.Visibility
        "Bolt" -> Icons.Default.Bolt
        "Schema" -> Icons.Default.Schema
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "Speed" -> Icons.Default.Speed
        "WaterDrop" -> Icons.Default.WaterDrop
        "Air" -> Icons.Default.Air
        "Dataset" -> Icons.Default.Dataset
        "AccessibilityNew" -> Icons.Default.AccessibilityNew
        "Diversity3" -> Icons.Default.Diversity3
        "Public" -> Icons.Default.Public
        "WbSunny" -> Icons.Default.WbSunny
        "Layers" -> Icons.Default.Layers
        "Map" -> Icons.Default.Map
        "Accessible" -> Icons.AutoMirrored.Filled.Accessible
        "SignLanguage" -> Icons.Default.SignLanguage
        "Braille" -> Icons.Default.Blind
        "DesignServices" -> Icons.Default.DesignServices
        "ViewQuilt" -> Icons.AutoMirrored.Filled.ViewQuilt
        "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
        "Autorenew" -> Icons.Default.Autorenew
        "Insights" -> Icons.Default.Insights
        "PresentToAll" -> Icons.Default.PresentToAll
        "Cloud" -> Icons.Default.Cloud
        "Link" -> Icons.Default.Link
        "ChangeHistory" -> Icons.Default.ChangeHistory
        "SquareFoot" -> Icons.Default.SquareFoot
        "Percent" -> Icons.Default.Percent
        "BarChart" -> Icons.Default.BarChart
        "Restaurant" -> Icons.Default.Restaurant
        "Favorite" -> Icons.Default.Favorite
        "Language" -> Icons.Default.Language
        "AutoGraph" -> Icons.Default.AutoGraph
        "Storefront" -> Icons.Default.Storefront
        "CallMerge" -> Icons.AutoMirrored.Filled.CallMerge
        else -> Icons.Default.Build
    }
}
