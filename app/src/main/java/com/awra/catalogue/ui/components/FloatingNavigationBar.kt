package com.awra.catalogue.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.R
import com.kyant.capsule.ContinuousRoundedRectangle

enum class NavDestination {
    HOME, SCHEDULE, ANALYTICS, TERMS
}

@Composable
fun FloatingNavigationBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .wrapContentWidth(),
            shape = ContinuousRoundedRectangle(32.dp),
            color = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavTab(
                    selected = currentDestination == NavDestination.HOME,
                    iconRes = R.drawable.home,
                    label = "Home",
                    onClick = { onNavigate(NavDestination.HOME) }
                )
                NavTab(
                    selected = currentDestination == NavDestination.SCHEDULE,
                    iconRes = R.drawable.schedule,
                    label = "Schedule",
                    onClick = { onNavigate(NavDestination.SCHEDULE) }
                )
                NavTab(
                    selected = currentDestination == NavDestination.ANALYTICS,
                    iconRes = R.drawable.analytics,
                    label = "Stats",
                    onClick = { onNavigate(NavDestination.ANALYTICS) }
                )
                NavTab(
                    selected = currentDestination == NavDestination.TERMS,
                    iconRes = R.drawable.terms,
                    label = "Terms",
                    onClick = { onNavigate(NavDestination.TERMS) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingNavigationBarPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            FloatingNavigationBar(
                currentDestination = NavDestination.HOME,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FloatingNavigationBarDarkPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            FloatingNavigationBar(
                currentDestination = NavDestination.HOME,
                onNavigate = {}
            )
        }
    }
}

@Composable
private fun NavTab(
    selected: Boolean,
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "background"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "content"
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(ContinuousRoundedRectangle(32.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .padding(horizontal = if (selected) 16.dp else 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            
            if (selected) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label,
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 14.sp
                )
            }
        }
    }
}
