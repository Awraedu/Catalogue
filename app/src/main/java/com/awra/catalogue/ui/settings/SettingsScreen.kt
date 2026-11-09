package com.awra.catalogue.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.awra.catalogue.data.model.Stream
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect
import com.kyant.capsule.ContinuousRoundedRectangle
import java.util.Locale

@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val studyReminders by viewModel.studyReminders.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()
    val studyGoal by viewModel.studyGoal.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val selectedStream by viewModel.selectedStream.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()

    val context = LocalContext.current

    var showTimePicker by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCustomThemeDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showStreamDialog by remember { mutableStateOf(false) }

    if (showNameDialog) {
        var nameText by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (nameText.isNotBlank()) {
                        viewModel.setUserName(nameText.trim())
                    }
                    showNameDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("Cancel") }
            },
            title = { Text("What should we call you?") },
            text = {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("User Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = ContinuousRoundedRectangle(28.dp)
        )
    }

    if (showGoalDialog) {
        val goals = remember {
            listOf(
                5 to "Casual (5 mins / day)",
                15 to "Regular (15 mins / day)",
                30 to "Serious (30 mins / day)",
                45 to "Dedicated (45 mins / day)",
                60 to "Intense (60 mins / day)"
            )
        }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            confirmButton = {
                TextButton(onClick = { showGoalDialog = false }) { Text("Cancel") }
            },
            title = { Text("Daily Study Goal") },
            text = {
                Column {
                    goals.forEach { (mins, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setStudyGoal(mins)
                                    showGoalDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = studyGoal == mins, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(label)
                        }
                    }
                }
            },
            shape = ContinuousRoundedRectangle(28.dp)
        )
    }

    if (showStreamDialog) {
        AlertDialog(
            onDismissRequest = { showStreamDialog = false },
            confirmButton = {
                TextButton(onClick = { showStreamDialog = false }) { Text("Cancel") }
            },
            title = { Text("Change Stream") },
            text = {
                Column {
                    Stream.entries.forEach { stream ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedStream(stream.name)
                                    showStreamDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = selectedStream == stream.name, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(stream.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            shape = ContinuousRoundedRectangle(28.dp)
        )
    }

    if (showTimePicker) {
        val (initHour, initMin) = remember(notificationTime) {
            try {
                val cleaned = notificationTime.trim().uppercase(Locale.US)
                val parts = cleaned.split(" ")
                val hm = parts[0].split(":")
                var h = hm[0].toInt()
                val m = hm[1].toInt()
                if (parts.size > 1) {
                    if (parts[1] == "PM" && h < 12) h += 12
                    if (parts[1] == "AM" && h == 12) h = 0
                }
                h.coerceIn(0, 23) to m.coerceIn(0, 59)
            } catch (e: Exception) {
                17 to 0
            }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = initHour,
            initialMinute = initMin,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = String.format(Locale.US, "%02d:%02d", timePickerState.hour, timePickerState.minute)
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
                        "VIBRANT_ORANGE" to "Vibrant Orange (Brand)",
                        "OCEAN_BLUE" to "Ocean Blue",
                        "MIDNIGHT_MONO" to "Midnight Mono",
                        "FOREST_SAGE" to "Forest Sage",
                        "EARTH_BROWN" to "Earth Brown",
                        "TWILIGHT_LAVENDER" to "Twilight Lavender"
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

    val backdrop = rememberLayerBackdrop()
    val scrollState = rememberLazyListState()

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
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 140.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        SettingsSection(title = "Account") {
                            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                                SettingsClickableItem(
                                    title = "User Name",
                                    subtitle = userName,
                                    icon = Icons.Default.Person,
                                    onClick = { showNameDialog = true },
                                    index = 0,
                                    count = 2
                                )
                                SettingsClickableItem(
                                    title = "Academic Stream",
                                    subtitle = selectedStream.lowercase().replaceFirstChar { it.uppercase() },
                                    icon = Icons.Default.School,
                                    onClick = { showStreamDialog = true },
                                    index = 1,
                                    count = 2
                                )
                            }
                        }
                    }

                    item {
                        SettingsSection(title = "Customization") {
                            SegmentedListItem(
                                onClick = { showCustomThemeDialog = true },
                                shapes = ListItemDefaults.shapes(shape = ContinuousRoundedRectangle(32.dp)),
                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                trailingContent = {
                                    val colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                    Column(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .height(48.dp)
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
                                    val themeName = when (selectedTheme) {
                                        "DEFAULT" -> "Adaptive (System)"
                                        "VIBRANT_ORANGE" -> "Vibrant Orange (Brand)"
                                        "OCEAN_BLUE" -> "Ocean Blue"
                                        "MIDNIGHT_MONO" -> "Midnight Mono"
                                        "FOREST_SAGE" -> "Forest Sage"
                                        "EARTH_BROWN" -> "Earth Brown"
                                        "TWILIGHT_LAVENDER" -> "Twilight Lavender"
                                        else -> selectedTheme
                                    }
                                    Text(
                                        if (selectedTheme == "DEFAULT") "Choose a palette based on colors from your wallpaper."
                                        else "Current: $themeName",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        SettingsSection(title = "Notifications & Goals") {
                            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                                SettingsToggleItem(
                                    title = "Study Reminders",
                                    subtitle = "Smart nudges, streak savers & class alerts",
                                    icon = Icons.Default.Notifications,
                                    checked = studyReminders,
                                    onCheckedChange = { viewModel.setStudyReminders(it) },
                                    index = 0,
                                    count = 3
                                )
                                SettingsClickableItem(
                                    title = "Preferred Study Time",
                                    subtitle = notificationTime,
                                    icon = Icons.Default.Schedule,
                                    onClick = { showTimePicker = true },
                                    index = 1,
                                    count = 3
                                )
                                SettingsClickableItem(
                                    title = "Daily Study Goal",
                                    subtitle = "$studyGoal minutes / day",
                                    icon = Icons.Default.Flag,
                                    onClick = { showGoalDialog = true },
                                    index = 2,
                                    count = 3
                                )
                            }
                        }
                    }

                    item {
                        SettingsSection(title = "Storage") {
                            SettingsClickableItem(
                                title = "Clear Cache",
                                subtitle = "Free up some space ($cacheSize)",
                                icon = Icons.Default.DeleteSweep,
                                onClick = { viewModel.clearCache() },
                                index = 0,
                                count = 1
                            )
                        }
                    }

                    item {
                        SettingsSection(title = "Support") {
                            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                                SettingsClickableItem(
                                    title = "Help & Feedback",
                                    subtitle = "Contact support or report issues",
                                    icon = Icons.Default.Help,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:awra.edu@gmail.com")
                                            putExtra(Intent.EXTRA_SUBJECT, "Catalogue App Feedback")
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                        }
                                    },
                                    index = 0,
                                    count = 2
                                )
                                SettingsClickableItem(
                                    title = "Visit our website",
                                    subtitle = "www.AwraDigitals.com",
                                    icon = Icons.Default.Language,
                                    onClick = {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            "https://awra-chi.vercel.app/".toUri()
                                        )
                                        context.startActivity(intent)
                                    },
                                    index = 1,
                                    count = 2
                                )
                            }
                        }
                    }

                    item {
                        SettingsSection(title = "About") {
                            SettingsClickableItem(
                                title = "App Version",
                                subtitle = viewModel.appVersion,
                                icon = Icons.Default.Info,
                                onClick = { },
                                index = 0,
                                count = 1
                            )
                        }
                    }
                }
            }

            // Top Bar Progressive Blur
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(innerPadding.calculateTopPadding() + 90.dp)
                    .drawPlainBackdrop(
                        backdrop = backdrop,
                        shape = { RectangleShape },
                        effects = {
                            blur(16f.dp.toPx())
                            runtimeShaderEffect(
                                "ProgressiveBlur",
                                """
                                uniform shader content;
                                uniform float2 size;
                                half4 main(float2 coord) {
                                    float alpha = 1.0 - smoothstep(size.y * 0.1, size.y, coord.y);
                                    return content.eval(coord) * alpha;
                                }
                                """,
                                "content"
                            ) {
                                setFloatUniform("size", size.width, size.height)
                            }
                        }
                    )
            )

            // Bottom Bar Progressive Blur
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(140.dp)
                    .drawPlainBackdrop(
                        backdrop = backdrop,
                        shape = { RectangleShape },
                        effects = {
                            blur(16f.dp.toPx())
                            runtimeShaderEffect(
                                "ProgressiveBlur",
                                """
                                uniform shader content;
                                uniform float2 size;
                                half4 main(float2 coord) {
                                    float alpha = smoothstep(0.0, size.y * 0.9, coord.y);
                                    return content.eval(coord) * alpha;
                                }
                                """,
                                "content"
                            ) {
                                setFloatUniform("size", size.width, size.height)
                            }
                        }
                    )
            )
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
            Icon(
                icon,
                null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
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
            Icon(
                icon,
                null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
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