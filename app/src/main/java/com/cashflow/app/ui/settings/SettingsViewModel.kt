package com.cashflow.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(
    initialDarkTheme: Boolean = false,
    private val onThemeChanged: (Boolean) -> Unit = {},
    private val repository: CashFlowRepository? = null
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(isDarkTheme = initialDarkTheme))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun toggleDarkTheme() {
        val newValue = !_state.value.isDarkTheme
        _state.value = _state.value.copy(isDarkTheme = newValue)
        onThemeChanged(newValue)
    }
    
    suspend fun exportData(): String {
        return repository?.exportData() ?: "{}"
    }
    
    suspend fun importData(jsonData: String): Result<Unit> {
        return repository?.importData(jsonData) ?: Result.failure(Exception("Repository not available"))
    }
}

data class SettingsState(
    val isDarkTheme: Boolean = false
)

