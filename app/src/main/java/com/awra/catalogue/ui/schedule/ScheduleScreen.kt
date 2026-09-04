package com.awra.catalogue.ui.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.ClassSchedule
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.ui.theme.getSubjectAdaptiveColors
import com.awra.catalogue.ui.theme.toShape
import com.kyant.capsule.ContinuousRoundedRectangle
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel
) {
    val schedules by viewModel.schedules.collectAsState(initial = emptyList())
    val subjects by viewModel.allSubjects.collectAsState(initial = emptyList())
    var selectedDay by remember { mutableIntStateOf(1) } // 1 = Monday
    
    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddTestDialog by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    val days = remember { listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday") }
    val filteredSchedules = remember(schedules, selectedDay) {
        schedules.filter { 
            if (it.isTest && it.date != null) {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                val day = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
                day == selectedDay
            } else {
                it.dayOfWeek == selectedDay
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = { Text("Schedule", fontWeight = FontWeight.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                PrimaryTabRow(
                    selectedTabIndex = selectedDay - 1,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    days.forEachIndexed { index, day ->
                        val selected = selectedDay == index + 1
                        Tab(
                            selected = selected,
                            onClick = { selectedDay = index + 1 },
                            text = { 
                                Text(
                                    day.take(3), 
                                    style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                ) 
                            }
                        )
                    }
                }
            }
        },
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
                modifier = Modifier.padding(bottom = 80.dp) // Offset above the floating nav bar
            ) {
                FloatingActionButtonMenuItem(
                    onClick = { 
                        fabExpanded = false
                        showAddTestDialog = true 
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null, tint = inverseOnSurface) },
                    text = { Text("Add Exam", color = inverseOnSurface) },
                    containerColor = inverseSurface
                )
                FloatingActionButtonMenuItem(
                    onClick = { 
                        fabExpanded = false
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
            if (filteredSchedules.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.EventNote, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(16.dp))
                        Text("Nothing scheduled for ${days[selectedDay - 1]}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(filteredSchedules, key = { _, item -> item.id }) { index, schedule ->
                        val subject = subjects.find { it.id == schedule.subjectId }
                        ScheduleItemWrapper(
                            schedule = schedule,
                            subject = subject,
                            index = index,
                            count = filteredSchedules.size,
                            onDelete = { viewModel.removeSchedule(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddClassDialog) {
        AddClassDialog(
            subjects = subjects,
            onDismiss = { showAddClassDialog = false },
            onConfirm = { schedule ->
                viewModel.addSchedule(schedule)
                showAddClassDialog = false
            }
        )
    }

    if (showAddTestDialog) {
        AddTestDialog(
            onDismiss = { showAddTestDialog = false },
            onConfirm = { schedule ->
                viewModel.addSchedule(schedule)
                showAddTestDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleItemWrapper(
    schedule: ClassSchedule,
    subject: Subject?,
    index: Int,
    count: Int,
    onDelete: (ClassSchedule) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    
    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        LaunchedEffect(schedule) {
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
                    .clip(getItemShape(index, count))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = {
            ScheduleItem(schedule, subject, index, count)
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
    index: Int,
    count: Int
) {
    val subjectColors = if (subject != null) getSubjectAdaptiveColors(subject.id) else null
    val displayColor = if (schedule.isTest) MaterialTheme.colorScheme.error else (subjectColors?.content ?: Color(schedule.color))
    val containerColor = if (schedule.isTest) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    
    Card(
        shape = getItemShape(index, count),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(subject?.shape?.toShape() ?: ContinuousRoundedRectangle(16.dp))
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
                    if (schedule.isTest && schedule.date != null) {
                        val locale = LocalLocale.current.platformLocale
                        val dateStr = SimpleDateFormat("MMM dd, yyyy", locale).format(Date(schedule.date))
                        "$dateStr • ${schedule.startTime}"
                    } else {
                        "${schedule.startTime} - ${schedule.endTime}"
                    },
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

@Composable
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
    onDismiss: () -> Unit,
    onConfirm: (ClassSchedule) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var dayOfWeek by remember { mutableIntStateOf(1) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var room by remember { mutableStateOf("") }
    
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var expandedSubjects by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (selectedSubject != null) {
                        onConfirm(ClassSchedule(
                            subjectId = selectedSubject!!.id,
                            subjectName = selectedSubject!!.name,
                            dayOfWeek = dayOfWeek,
                            startTime = startTime,
                            endTime = endTime,
                            room = room,
                            isTest = false
                        ))
                    }
                },
                shape = CircleShape
            ) { Text("Add Class") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Class") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val dayNames = listOf("M", "T", "W", "T", "F", "S")
                    dayNames.forEachIndexed { index, day ->
                        val selected = dayOfWeek == index + 1
                        Surface(
                            onClick = { dayOfWeek = index + 1 },
                            shape = CircleShape,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(day, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedCard(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Column(Modifier.padding(vertical = 10.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Start", style = MaterialTheme.typography.labelSmall)
                            Text(startTime, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedCard(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Column(Modifier.padding(vertical = 10.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("End", style = MaterialTheme.typography.labelSmall)
                            Text(endTime, fontWeight = FontWeight.Bold)
                        }
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
            }
        },
        shape = ContinuousRoundedRectangle(28.dp)
    )

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { h, m ->
                val locale = Locale.getDefault()
                startTime = String.format(locale, "%02d:%02d", h, m)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { h, m ->
                val locale = Locale.getDefault()
                endTime = String.format(locale, "%02d:%02d", h, m)
                showEndTimePicker = false
            }
        )
    }
}

@SuppressLint("NonObservableLocale")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddTestDialog(
    onDismiss: () -> Unit,
    onConfirm: (ClassSchedule) -> Unit
) {
    var testName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Test") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var startTime by remember { mutableStateOf("08:00") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(false) }

    val testTypes = listOf("Midterm", "Final", "Test", "Quiz")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (testName.isNotEmpty()) {
                        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                        val day = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
                        
                        onConfirm(ClassSchedule(
                            subjectName = testName,
                            testType = selectedType,
                            dayOfWeek = day,
                            startTime = startTime,
                            date = selectedDate,
                            isTest = true
                        ))
                    }
                },
                shape = CircleShape
            ) { Text("Add Exam") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Exam/Test") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                        Column(Modifier.padding(vertical = 10.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Date", style = MaterialTheme.typography.labelSmall)
                            val locale = LocalLocale.current.platformLocale
                            Text(SimpleDateFormat("MMM dd", locale).format(Date(selectedDate)), fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedCard(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Column(Modifier.padding(vertical = 10.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Time", style = MaterialTheme.typography.labelSmall)
                            Text(startTime, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        shape = ContinuousRoundedRectangle(28.dp)
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
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
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { h, m ->
                startTime = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                showStartTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(is24Hour = true)
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
