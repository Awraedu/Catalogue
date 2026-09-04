package com.awra.catalogue.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awra.catalogue.data.model.ClassSchedule
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.repository.CatalogueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: CatalogueRepository) : ViewModel() {
    val schedules: Flow<List<ClassSchedule>> = repository.getAllSchedules()
    val allSubjects: Flow<List<Subject>> = repository.getAllSubjects()

    fun addSchedule(schedule: ClassSchedule) {
        viewModelScope.launch {
            repository.addSchedule(schedule)
        }
    }

    fun removeSchedule(schedule: ClassSchedule) {
        viewModelScope.launch {
            repository.removeSchedule(schedule)
        }
    }
}
