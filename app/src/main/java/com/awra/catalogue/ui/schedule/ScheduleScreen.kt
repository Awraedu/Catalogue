package com.awra.catalogue.ui.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.ClassSchedule
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.cloudy
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky
import java.text.SimpleDateFormat
import java.util.*

private fun format24to12(time24: String): String {
    return try {
        val parts = time24.trim().split(" ")[0].split(":")
        val hour = parts[0].toInt()
        val min = parts[1].toInt()
        val ampm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        String.format(Locale.US, "%d:%02d %s", hour12, min, ampm)
    } catch (e: Exception) {
        time24
    }
}

private fun parseHourMinute(time24: String, defaultHour: Int = 8, defaultMin: Int = 0): Pair<Int, Int> {
    return try {
        val parts = time24.trim().split(" ")[0].split(":")
        parts[0].toInt().coerceIn(0, 23) to parts[1].toInt().coerceIn(0, 59)
    } catch (e: Exception) {
        defaultHour to defaultMin
    }
}

private fun isTimeAfter(startTime: String, endTime: String): Boolean {
    return try {
        val (sh, sm) = parseHourMinute(startTime)
        val (eh, em) = parseHourMinute(endTime)
        (eh * 60 + em) > (sh * 60 + sm)
    } catch (e: Exception) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel
) {
    val schedules by viewModel.schedules.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()

    // O(1) Subject lookup map so LazyColumn items never run O(N) list scans
    val subjectById = remember(subjects) {
        subjects.associateBy { it.id }
    }

    val currentDay = remember {
        val cal = Calendar.getInstance()
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    var selectedDay by remember { mutableIntStateOf(currentDay) }

    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddTestDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<ClassSchedule?>(null) }
    var fabExpanded by remember { mutableStateOf(false) }

    val days = remember { listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday") }

    val filteredSchedules = remember(schedules, selectedDay) {
        val cal = Calendar.getInstance()
        schedules.filter {
            if (it.isTest && it.date != null) {
                cal.timeInMillis = it.date
                val day = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
                day == selectedDay
            } else {
                it.dayOfWeek == selectedDay
            }
        }
    }

    val scheduleSky = rememberSky()

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientMeshBackground(
            modifier = Modifier
                .fillMaxSize()
                .sky(scheduleSky)
        )
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                val inverseSurface = MaterialTheme.colorScheme.inverseSurface
                val inverseOnSurface = MaterialTheme.colorScheme.inverseOnSurface

                FloatingActionButtonMenu(
                    expanded = fabExpanded,
                    button = {
                        ToggleFloatingActionButton(
                            checked = fabExpanded,
                            onCheckedChange = { fabExpanded = it },
                            containerColor = { _ -> inverseSurface }
                        ) {
                            Icon(
                                imageVector = if (checkedProgress > 0.5f) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add Menu",
                                tint = inverseOnSurface
                            )
                        }
                    },
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    FloatingActionButtonMenuItem(
                        onClick = {
                            fabExpanded = false
                            editingSchedule = null
                            showAddTestDialog = true
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null, tint = inverseOnSurface) },
                        text = { Text("Add Exam", color = inverseOnSurface) },
                        containerColor = inverseSurface
                    )
                    FloatingActionButtonMenuItem(
                        onClick = {
                            fabExpanded = false
                            editingSchedule = null
                            showAddClassDialog = true
                        },
                        icon = { Icon(Icons.Default.School, null, tint = inverseOnSurface) },
                        text = { Text("Add Class", color = inverseOnSurface) },
                        containerColor = inverseSurface
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {
                            Text(
                                "Schedule",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            ScrollableTabRow(
                                selectedTabIndex = (selectedDay - 1).coerceIn(0, days.lastIndex),
                                containerColor = Color.Transparent,
                                edgePadding = 0.dp,
                                divider = {}
                            ) {
                                days.forEachIndexed { index, day ->
                                    val selected = selectedDay == index + 1
                                    Tab(
                                        selected = selected,
                                        onClick = { selectedDay = index + 1 },
                                        selectedContentColor = MaterialTheme.colorScheme.primary,
                                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                        text = {
                                            Text(
                                                day,
                                                style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    if (filteredSchedules.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.EventNote,
                                        null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        "Nothing scheduled for ${days[(selectedDay - 1).coerceIn(0, days.lastIndex)]}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    } else {
                        itemsIndexed(filteredSchedules, key = { _, item -> item.id }) { index, schedule ->
                            val subject = schedule.subjectId?.let { subjectById[it] }
                            ScheduleItemWrapper(
                                schedule = schedule,
                                subject = subject,
                                index = index,
                                count = filteredSchedules.size,
                                onDelete = {
                                    viewModel.removeSchedule(it)
                                },
                                onEdit = {
                                    editingSchedule = it
                                    if (it.isTest) showAddTestDialog = true else showAddClassDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAddClassDialog) {
            AddClassDialog(
                subjects = subjects,
                selectedDay = selectedDay,
                editingSchedule = editingSchedule,
                sky = scheduleSky,
                onDismiss = {
                    showAddClassDialog = false
                    editingSchedule = null
                },
                onConfirm = { schedule ->
                    viewModel.addSchedule(schedule)
                    showAddClassDialog = false
                    editingSchedule = null
                }
            )
        }

        if (showAddTestDialog) {
            AddTestDialog(
                editingSchedule = editingSchedule,
                sky = scheduleSky,
                onDismiss = {
                    showAddTestDialog = false
                    editingSchedule = null
                },
                onConfirm = { schedule ->
                    viewModel.addSchedule(schedule)
                    showAddTestDialog = false
                    editingSchedule = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleItemWrapper(
    schedule: ClassSchedule,
    subject: Subject?,
    index: Int,
    count: Int,
    onDelete: (ClassSchedule) -> Unit,
    onEdit: (ClassSchedule) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val shape = remember(index, count) { getItemShape(index, count) }

    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        LaunchedEffect(schedule.id) {
            onDelete(schedule)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = MaterialTheme.colorScheme.errorContainer
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = {
            ScheduleItem(schedule, subject, shape, onClick = { onEdit(schedule) })
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@SuppressLint("NonObservableLocale")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ScheduleItem(
    schedule: ClassSchedule,
    subject: Subject?,
    shape: Shape,
    onClick: () -> Unit
) {
    val subjectColors = if (subject != null) getSubjectAdaptiveColors(subject.id) else null
    val displayColor = if (schedule.isTest) MaterialTheme.colorScheme.error else (subjectColors?.content ?: Color(schedule.color))
    val containerColor = if (schedule.isTest) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val locale = LocalLocale.current.platformLocale

    val formattedTimeSubtitle = remember(schedule.isTest, schedule.date, schedule.startTime, schedule.endTime, locale) {
        if (schedule.isTest && schedule.date != null) {
            val dateStr = SimpleDateFormat("MMM dd, yyyy", locale).format(Date(schedule.date))
            "$dateStr • ${format24to12(schedule.startTime)}"
        } else {
            "${format24to12(schedule.startTime)} - ${format24to12(schedule.endTime ?: "")}"
        }
    }

    val iconShape = subject?.shape?.toShape() ?: ContinuousRoundedRectangle(16.dp)

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(iconShape)
                    .background(displayColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (schedule.isTest) Icons.Default.Warning else Icons.Default.Class,
                    contentDescription = null,
                    tint = displayColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (schedule.isTest) "${schedule.testType}: ${schedule.subjectName}" else schedule.subjectName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (schedule.isTest) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    formattedTimeSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (schedule.isTest) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (schedule.room.isNotEmpty()) {
                    Text(
                        "Room: ${schedule.room}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (schedule.isTest) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun getItemShape(index: Int, count: Int): Shape {
    val outerRadius = 32.dp
    val innerRadius = 8.dp
    return when {
        count == 1 -> ContinuousRoundedRectangle(outerRadius)
        index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
        index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
        else -> RoundedCornerShape(innerRadius)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddClassDialog(
    subjects: List<Subject>,
    selectedDay: Int,
    editingSchedule: ClassSchedule? = null,
    sky: Sky,
    onDismiss: () -> Unit,
    onConfirm: (ClassSchedule) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<Subject?>(subjects.find { it.id == editingSchedule?.subjectId }) }
    var dayOfWeek by remember { mutableIntStateOf(editingSchedule?.dayOfWeek ?: selectedDay) }
    var startTime by remember { mutableStateOf(editingSchedule?.startTime ?: "08:00") }
    var endTime by remember { mutableStateOf(editingSchedule?.endTime ?: "09:00") }
    var room by remember { mutableStateOf(editingSchedule?.room ?: "") }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var expandedSubjects by remember { mutableStateOf(false) }

    val popupShape = ContinuousRoundedRectangle(32.dp)
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = MaterialTheme.colorScheme.onSurface
    val isTimeValid = remember(startTime, endTime) { isTimeAfter(startTime, endTime) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                )
                .cloudy(
                    sky = sky,
                    radius = 35,
                    tint = containerColor.copy(alpha = 0.85f),
                    shape = popupShape
                )
                .clip(popupShape)
                .border(
                    width = 1.dp,
                    color = contentColor.copy(alpha = 0.25f),
                    shape = popupShape
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (editingSchedule != null) "Edit Class" else "Add Class",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = contentColor
                )

                ExposedDropdownMenuBox(
                    expanded = expandedSubjects,
                    onExpandedChange = { expandedSubjects = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Subject") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjects) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = CircleShape
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubjects,
                        onDismissRequest = { expandedSubjects = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject.name) },
                                onClick = {
                                    selectedSubject = subject
                                    expandedSubjects = false
                                }
                            )
                        }
                    }
                }

                Text("Day", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
                    dayNames.forEachIndexed { index, day ->
                        val selected = dayOfWeek == index + 1
                        Surface(
                            onClick = { dayOfWeek = index + 1 },
                            shape = CircleShape,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = day,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedCard(
                            onClick = { showStartTimePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape
                        ) {
                            Column(
                                Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Start", style = MaterialTheme.typography.labelSmall)
                                Text(format24to12(startTime), fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedCard(
                            onClick = { showEndTimePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape
                        ) {
                            Column(
                                Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("End", style = MaterialTheme.typography.labelSmall)
                                Text(format24to12(endTime), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (!isTimeValid) {
                        Text(
                            "End time must be after start time",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    singleLine = true
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (selectedSubject != null && isTimeValid) {
                                onConfirm(
                                    ClassSchedule(
                                        id = editingSchedule?.id ?: 0,
                                        subjectId = selectedSubject!!.id,
                                        subjectName = selectedSubject!!.name,
                                        dayOfWeek = dayOfWeek,
                                        startTime = startTime,
                                        endTime = endTime,
                                        room = room.trim(),
                                        isTest = false
                                    )
                                )
                            }
                        },
                        enabled = selectedSubject != null && isTimeValid,
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Text(if (editingSchedule != null) "Save" else "Add Class", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showStartTimePicker) {
        val (initH, initM) = parseHourMinute(startTime, 8, 0)
        TimePickerDialog(
            initialHour = initH,
            initialMinute = initM,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { h, m ->
                val newStartTime = String.format(Locale.US, "%02d:%02d", h, m)
                if (!isTimeAfter(newStartTime, endTime)) {
                    val endH = (h + 1) % 24
                    endTime = String.format(Locale.US, "%02d:%02d", endH, m)
                }
                startTime = newStartTime
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        val (initH, initM) = parseHourMinute(endTime, 9, 0)
        TimePickerDialog(
            initialHour = initH,
            initialMinute = initM,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { h, m ->
                endTime = String.format(Locale.US, "%02d:%02d", h, m)
                showEndTimePicker = false
            }
        )
    }
}

@SuppressLint("NonObservableLocale")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddTestDialog(
    editingSchedule: ClassSchedule? = null,
    sky: Sky,
    onDismiss: () -> Unit,
    onConfirm: (ClassSchedule) -> Unit
) {
    var testName by remember { mutableStateOf(editingSchedule?.subjectName ?: "") }
    var selectedType by remember { mutableStateOf(editingSchedule?.testType ?: "Test") }
    var room by remember { mutableStateOf(editingSchedule?.room ?: "") }

    val today = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    var selectedDate by remember {
        mutableLongStateOf(editingSchedule?.date?.coerceAtLeast(today) ?: today)
    }
    var startTime by remember { mutableStateOf(editingSchedule?.startTime ?: "08:00") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(false) }

    val testTypes = remember { listOf("Midterm", "Final", "Test", "Quiz") }
    val locale = LocalLocale.current.platformLocale
    val formattedDate = remember(selectedDate, locale) {
        SimpleDateFormat("MMM dd", locale).format(Date(selectedDate))
    }

    val popupShape = ContinuousRoundedRectangle(32.dp)
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                )
                .cloudy(
                    sky = sky,
                    radius = 35,
                    tint = containerColor.copy(alpha = 0.85f),
                    shape = popupShape
                )
                .clip(popupShape)
                .border(
                    width = 1.dp,
                    color = contentColor.copy(alpha = 0.25f),
                    shape = popupShape
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = if (editingSchedule != null) "Edit Exam/Test" else "Add Exam/Test",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = contentColor
                )

                OutlinedTextField(
                    value = testName,
                    onValueChange = { testName = it },
                    label = { Text("Exam Title (e.g. Physics)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedTypes,
                    onExpandedChange = { expandedTypes = it }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Exam Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypes) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = CircleShape
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTypes,
                        onDismissRequest = { expandedTypes = false }
                    ) {
                        testTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expandedTypes = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedCard(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1.2f),
                        shape = CircleShape
                    ) {
                        Column(
                            Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Date", style = MaterialTheme.typography.labelSmall)
                            Text(formattedDate, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedCard(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Column(
                            Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Time", style = MaterialTheme.typography.labelSmall)
                            Text(format24to12(startTime), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Hall (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    singleLine = true
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (testName.isNotBlank()) {
                                val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                                val day = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1

                                onConfirm(
                                    ClassSchedule(
                                        id = editingSchedule?.id ?: 0,
                                        subjectName = testName.trim(),
                                        testType = selectedType,
                                        dayOfWeek = day,
                                        startTime = startTime,
                                        room = room.trim(),
                                        date = selectedDate,
                                        isTest = true
                                    )
                                )
                            }
                        },
                        enabled = testName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Text(if (editingSchedule != null) "Save" else "Add Exam", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= today
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        val (initH, initM) = parseHourMinute(startTime, 8, 0)
        TimePickerDialog(
            initialHour = initH,
            initialMinute = initM,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { h, m ->
                startTime = String.format(Locale.US, "%02d:%02d", h, m)
                showStartTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int = 8,
    initialMinute: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = state)
            }
        },
        shape = ContinuousRoundedRectangle(28.dp)
    )
}