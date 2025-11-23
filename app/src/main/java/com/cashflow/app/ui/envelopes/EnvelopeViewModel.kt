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
        handleIntent(EnvelopeIntent.LoadEnvelopes)
    }

    fun handleIntent(intent: EnvelopeIntent) {
        when (intent) {
            is EnvelopeIntent.LoadEnvelopes -> {
                viewModelScope.launch {
                    repository.getAllActiveEnvelopes()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { envelopes ->
                            _state.update { it.copy(envelopes = envelopes, isLoading = false) }
                        }
                }
            }
            is EnvelopeIntent.ShowAddDialog -> {
                _state.update { it.copy(showAddDialog = true, editingEnvelope = null, selectedColor = androidx.compose.ui.graphics.Color(0xFF7C3AED), selectedIcon = "Folder") }
            }
            is EnvelopeIntent.HideAddDialog -> {
                _state.update { it.copy(showAddDialog = false, editingEnvelope = null) }
            }
            is EnvelopeIntent.EditEnvelope -> {
                _state.update { it.copy(
                    showAddDialog = true,
                    editingEnvelope = intent.envelope,
                    selectedColor = intent.envelope.color,
                    selectedIcon = intent.envelope.icon ?: "Folder"
                ) }
            }
            is EnvelopeIntent.SaveEnvelope -> {
                viewModelScope.launch {
                    try {
                        if (intent.envelope.id == 0L) {
                            repository.insertEnvelope(intent.envelope)
                        } else {
                            repository.updateEnvelope(intent.envelope)
                        }
                        _state.update { it.copy(showAddDialog = false, editingEnvelope = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is EnvelopeIntent.DeleteEnvelope -> {
                viewModelScope.launch {
                    try {
                        repository.deleteEnvelope(intent.envelope)
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
