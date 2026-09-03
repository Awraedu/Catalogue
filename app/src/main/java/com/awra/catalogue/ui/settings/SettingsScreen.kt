package com.awra.catalogue.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val studyReminders by viewModel.studyReminders.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCustomThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Cancel") }
            },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    listOf("SYSTEM", "LIGHT", "DARK").forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDarkMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = darkMode == mode, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(mode.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            shape = ContinuousRoundedRectangle(28.dp)
        )
    }

    if (showTimePicker) {
        val currentTime = notificationTime.split(":")
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime[0].toInt(),
            initialMinute = currentTime[1].toInt(),
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    viewModel.setNotificationTime(formattedTime)
                    showTimePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            },
            shape = ContinuousRoundedRectangle(28.dp)
        )
    }

    if (showCustomThemeDialog) {
        AlertDialog(
            onDismissRequest = { showCustomThemeDialog = false },
            confirmButton = {
                TextButton(onClick = { showCustomThemeDialog = false }) { Text("Cancel") }
            },
            title = { Text("Choose Palette") },
            text = {
                Column {
                    listOf(
                        "DEFAULT" to "Adaptive (System)", 
                        "MIDNIGHT_MONO" to "Midnight Mono",
                        "NORDIC_BLUE" to "Nordic Blue",
                        "FOREST_MOSS" to "Forest Moss",
                        "CRIMSON_VELVET" to "Crimson Velvet"
                    ).forEach { (id, name) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedTheme(id)
                                    showCustomThemeDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = selectedTheme == id, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(name)
                        }
                    }
                }
            },
            shape = ContinuousRoundedRectangle(28.dp)
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                        SettingsClickableItem(
                            title = "Theme Mode",
                            subtitle = darkMode.lowercase().replaceFirstChar { it.uppercase() },
                            icon = Icons.Default.DarkMode,
                            onClick = { showThemeDialog = true },
                            index = 0,
                            count = 1
                        )
                    }
                }
            }

            // Customization Section (Redesigned Color Palette)
            item {
                SettingsSection(title = "Customization") {
                    SegmentedListItem(
                        onClick = { showCustomThemeDialog = true },
                        shapes = ListItemDefaults.shapes(shape = ContinuousRoundedRectangle(32.dp)),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        trailingContent = {
                            // Pill-shaped stack of 3 colors representing current selection
                            val colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.surfaceContainer,
                                MaterialTheme.colorScheme.secondary
                            )
                            Column(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(56.dp)
                                    .clip(CircleShape)
                            ) {
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().background(colors[0]))
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().background(colors[1]))
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().background(colors[2]))
                            }
                        }
                    ) {
                        Column {
                            Text("Color palette", fontWeight = FontWeight.Bold)
                            Text(
                                if (selectedTheme == "DEFAULT") "Choose a palette based on colors from your wallpaper."
                                else "Current: ${selectedTheme.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Notifications Section
            item {
                SettingsSection(title = "Notifications") {
                    Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                        SettingsToggleItem(
                            title = "Study Reminders",
                            subtitle = "Get notified to stay on track",
                            icon = Icons.Default.Notifications,
                            checked = studyReminders,
                            onCheckedChange = { viewModel.setStudyReminders(it) },
                            index = 0,
                            count = 2
                        )
                        SettingsClickableItem(
                            title = "Arrival Time from Class",
                            subtitle = notificationTime,
                            icon = Icons.Default.Schedule,
                            onClick = { showTimePicker = true },
                            index = 1,
                            count = 2
                        )
                    }
                }
            }

            // Storage Section
            item {
                SettingsSection(title = "Storage") {
                    SettingsClickableItem(
                        title = "Clear Cache",
                        subtitle = "Free up some space (42MB)",
                        icon = Icons.Default.DeleteSweep,
                        onClick = { viewModel.clearCache() },
                        index = 0,
                        count = 1
                    )
                }
            }

            // Support Section
            item {
                SettingsSection(title = "Support") {
                    Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                        SettingsClickableItem(
                            title = "Help & Feedback",
                            subtitle = "Contact support or report issues",
                            icon = Icons.Default.Help,
                            onClick = { },
                            index = 0,
                            count = 2
                        )
                        SettingsClickableItem(
                            title = "Visit our website",
                            subtitle = "www.awra-catalogue.com",
                            icon = Icons.Default.Language,
                            onClick = { },
                            index = 1,
                            count = 2
                        )
                    }
                }
            }

            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingsClickableItem(
                        title = "App Version",
                        subtitle = "1.0.0 (Alpha)",
                        icon = Icons.Default.Info,
                        onClick = { },
                        index = 0,
                        count = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    index: Int,
    count: Int
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

    SegmentedListItem(
        onClick = { onCheckedChange(!checked) },
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        leadingContent = {
            Icon(icon, null, modifier = Modifier.size(24.dp))
        },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    index: Int,
    count: Int
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

    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        leadingContent = {
            Icon(icon, null, modifier = Modifier.size(24.dp))
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, null)
        }
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
