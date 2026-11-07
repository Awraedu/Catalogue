package com.awra.catalogue.ui.schedule

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ClassSchedule
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.notification.NotificationScheduler
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: CatalogueRepository,
    private val appContext: Context? = null
) : ViewModel() {

    val schedules: StateFlow<List<ClassSchedule>> = repository.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allSubjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addSchedule(schedule: ClassSchedule) {
        viewModelScope.launch {
            repository.addSchedule(schedule)
            // Immediately reschedule notifications so the new class/exam is armed!
            appContext?.let { NotificationScheduler.scheduleAllReminders(it) }
        }
    }

    fun removeSchedule(schedule: ClassSchedule) {
        viewModelScope.launch {
            repository.removeSchedule(schedule)
            // Immediately update notifications so deleted classes don't fire!
            appContext?.let { NotificationScheduler.scheduleAllReminders(it) }
        }
    }
}