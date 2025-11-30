package com.cashflow.app.ui.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class EnvelopeViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EnvelopeState())
    val state: StateFlow<EnvelopeState> = _state.asStateFlow()

    init {
        handleIntent(EnvelopeIntent.LoadCategories)
    }

    fun handleIntent(intent: EnvelopeIntent) {
        when (intent) {
            is EnvelopeIntent.LoadCategories -> {
                viewModelScope.launch {
                    repository.getAllActiveCategories()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { categories ->
                            _state.update { it.copy(categories = categories, isLoading = false) }
                        }
                }
            }
            is EnvelopeIntent.ShowAddDialog -> {
                _state.update { it.copy(showAddDialog = true, editingCategory = null, selectedColor = androidx.compose.ui.graphics.Color(0xFF7C3AED), selectedIcon = "Folder") }
            }
            is EnvelopeIntent.HideAddDialog -> {
                _state.update { it.copy(showAddDialog = false, editingCategory = null) }
            }
            is EnvelopeIntent.EditCategory -> {
                _state.update { it.copy(
                    showAddDialog = true,
                    editingCategory = intent.category,
                    selectedColor = intent.category.color,
                    selectedIcon = intent.category.icon ?: "Folder"
                ) }
            }
            is EnvelopeIntent.SaveCategory -> {
                viewModelScope.launch {
                    try {
                        if (intent.category.id == 0L) {
                            repository.insertCategory(intent.category)
                        } else {
                            repository.updateCategory(intent.category)
                        }
                        _state.update { it.copy(showAddDialog = false, editingCategory = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is EnvelopeIntent.DeleteCategory -> {
                viewModelScope.launch {
                    try {
                        repository.deleteCategory(intent.category)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is EnvelopeIntent.SetSelectedColor -> {
                _state.update { it.copy(selectedColor = intent.color) }
            }
            is EnvelopeIntent.SetSelectedIcon -> {
                _state.update { it.copy(selectedIcon = intent.icon) }
            }
        }
    }
}
