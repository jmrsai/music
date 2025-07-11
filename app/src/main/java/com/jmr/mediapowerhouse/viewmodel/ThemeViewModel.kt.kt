package com.jmr.mediapowerhouse.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for managing application-wide theme settings.
 * Includes toggles for dark mode and glassmorphism effect.
 */
class `ThemeViewModel.kt` : ViewModel() {

    private val _isDarkMode = MutableStateFlow(true) // Default to dark mode
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _enableGlassmorphism = MutableStateFlow(true) // Default to enabled
    val enableGlassmorphism: StateFlow<Boolean> = _enableGlassmorphism.asStateFlow()

    /**
     * Toggles the dark mode setting.
     */
    fun toggleDarkMode() {
        _isDarkMode.update { !it }
    }

    /**
     * Toggles the glassmorphism effect setting.
     */
    fun toggleGlassmorphism() {
        _enableGlassmorphism.update { !it }
    }
}
