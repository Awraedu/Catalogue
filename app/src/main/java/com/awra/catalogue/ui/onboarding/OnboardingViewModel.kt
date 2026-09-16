package com.awra.catalogue.ui.onboarding

import androidx.lifecycle.ViewModel
import com.awra.catalogue.data.model.Stream
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class OnboardingStep {
    WELCOME, IDENTITY, STREAM, CUSTOMIZATION, GOALS, SCHEDULE, LAUNCHPAD
}

class OnboardingViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {

    private val _currentStep = MutableStateFlow(OnboardingStep.WELCOME)
    val currentStep: StateFlow<OnboardingStep> = _currentStep.asStateFlow()

    // Temporary storage for onboarding data
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _selectedStream = MutableStateFlow<Stream?>(null)
    val selectedStream: StateFlow<Stream?> = _selectedStream.asStateFlow()

    private val _selectedPalette = MutableStateFlow("VIBRANT_ORANGE")
    val selectedPalette: StateFlow<String> = _selectedPalette.asStateFlow()

    private val _darkMode = MutableStateFlow("LIGHT")
    val darkMode: StateFlow<String> = _darkMode.asStateFlow()

    private val _intensityMinutes = MutableStateFlow(15)
    val intensityMinutes: StateFlow<Int> = _intensityMinutes.asStateFlow()

    private val _arrivalTime = MutableStateFlow("05:00 PM") // Start with 12h format
    val arrivalTime: StateFlow<String> = _arrivalTime.asStateFlow()

    fun nextStep() {
        val steps = OnboardingStep.entries
        val currentIndex = steps.indexOf(_currentStep.value)
        if (currentIndex < steps.size - 1) {
            _currentStep.value = steps[currentIndex + 1]
        } else {
            completeOnboarding()
        }
    }

    fun previousStep() {
        val steps = OnboardingStep.entries
        val currentIndex = steps.indexOf(_currentStep.value)
        if (currentIndex > 0) {
            _currentStep.value = steps[currentIndex - 1]
        }
    }

    fun setName(name: String) { _name.value = name }
    fun setStream(stream: Stream) { _selectedStream.value = stream }
    fun setPalette(palette: String) { _selectedPalette.value = palette }
    fun setDarkMode(mode: String) { _darkMode.value = mode }
    fun setIntensity(minutes: Int) { _intensityMinutes.value = minutes }
    fun setArrivalTime(time: String) { _arrivalTime.value = time }

    private fun completeOnboarding() {
        preferenceManager.setUserName(_name.value.ifBlank { "User" })
        // Stream selection handled in MainActivity or via PreferenceManager if needed
        preferenceManager.setSelectedTheme(_selectedPalette.value)
        preferenceManager.setDarkMode(_darkMode.value)
        preferenceManager.setStudyGoal(_intensityMinutes.value)
        
        // Convert 12h arrival time back to 24h for storage if needed
        val time24 = convertTo24h(_arrivalTime.value)
        preferenceManager.setNotificationTime(time24)
        
        preferenceManager.setOnboardingCompleted(true)
    }

    private fun convertTo24h(time12: String): String {
        return try {
            val parts = time12.split(" ")
            val timeParts = parts[0].split(":")
            var hour = timeParts[0].toInt()
            val min = timeParts[1].toInt()
            val ampm = parts[1]
            
            if (ampm == "PM" && hour < 12) hour += 12
            if (ampm == "AM" && hour == 12) hour = 0
            
            String.format(java.util.Locale.US, "%02d:%02d", hour, min)
        } catch (e: Exception) {
            "17:00"
        }
    }
}
