package com.cashflow.app.ui.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EnvelopeHistoryViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EnvelopeHistoryState())
    val state: StateFlow<EnvelopeHistoryState> = _state.asStateFlow()

    fun handleIntent(intent: EnvelopeHistoryIntent) {
        when (intent) {
            is EnvelopeHistoryIntent.LoadHistory -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    
                    try {
                        val category = repository.getCategoryById(intent.categoryId)
                        if (category != null) {
                            val timeZone = TimeZone.currentSystemDefault()
                            val today = Clock.System.now().toLocalDateTime(timeZone).date
                            val startDate = LocalDate(today.year - 2, 1, 1) // Last 2 years
                            
                            val history = repository.getCategoryHistory(intent.categoryId, startDate, today)
                            
                            _state.update {
                                it.copy(
                                    category = category,
                                    history = history,
                                    isLoading = false
                                )
                            }
                        } else {
                            _state.update { it.copy(error = "Category not found", isLoading = false) }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
            }
        }
    }
}
