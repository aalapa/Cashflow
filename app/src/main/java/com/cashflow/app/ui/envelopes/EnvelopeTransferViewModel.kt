package com.cashflow.app.ui.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EnvelopeTransferViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EnvelopeTransferState())
    val state: StateFlow<EnvelopeTransferState> = _state.asStateFlow()

    init {
        handleIntent(EnvelopeTransferIntent.LoadEnvelopes)
    }

    fun handleIntent(intent: EnvelopeTransferIntent) {
        when (intent) {
            is EnvelopeTransferIntent.LoadEnvelopes -> {
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
            is EnvelopeTransferIntent.SetFromEnvelope -> {
                _state.update { it.copy(fromEnvelopeId = intent.envelopeId) }
            }
            is EnvelopeTransferIntent.SetToEnvelope -> {
                _state.update { it.copy(toEnvelopeId = intent.envelopeId) }
            }
            is EnvelopeTransferIntent.SetAmount -> {
                _state.update { it.copy(amount = intent.amount) }
            }
            is EnvelopeTransferIntent.SetDescription -> {
                _state.update { it.copy(description = intent.description) }
            }
            is EnvelopeTransferIntent.SaveTransfer -> {
                viewModelScope.launch {
                    try {
                        val fromId = _state.value.fromEnvelopeId
                        val toId = _state.value.toEnvelopeId
                        val amount = _state.value.amount.toDoubleOrNull() ?: 0.0
                        val description = _state.value.description

                        if (fromId == null || toId == null) {
                            _state.update { it.copy(error = "Please select both envelopes") }
                            return@launch
                        }

                        if (fromId == toId) {
                            _state.update { it.copy(error = "Cannot transfer to the same envelope") }
                            return@launch
                        }

                        if (amount <= 0) {
                            _state.update { it.copy(error = "Amount must be greater than 0") }
                            return@launch
                        }

                        val timeZone = TimeZone.currentSystemDefault()
                        val today = Clock.System.now().toLocalDateTime(timeZone).date

                        repository.transferBetweenEnvelopes(fromId, toId, amount, today, description)

                        _state.update {
                            it.copy(
                                fromEnvelopeId = null,
                                toEnvelopeId = null,
                                amount = "",
                                description = "",
                                error = null
                            )
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
        }
    }
}
