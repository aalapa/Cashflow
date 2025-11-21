package com.cashflow.app.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    initialDarkTheme: Boolean = false,
    private val onThemeChanged: (Boolean) -> Unit = {}
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(isDarkTheme = initialDarkTheme))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun toggleDarkTheme() {
        val newValue = !_state.value.isDarkTheme
        _state.value = _state.value.copy(isDarkTheme = newValue)
        onThemeChanged(newValue)
    }
}

data class SettingsState(
    val isDarkTheme: Boolean = false
)

