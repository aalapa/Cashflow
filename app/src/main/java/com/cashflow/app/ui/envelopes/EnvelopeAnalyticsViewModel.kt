package com.cashflow.app.ui.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.domain.repository.MonthlySpending
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EnvelopeAnalyticsViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EnvelopeAnalyticsState())
    val state: StateFlow<EnvelopeAnalyticsState> = _state.asStateFlow()

    init {
        handleIntent(EnvelopeAnalyticsIntent.LoadAnalytics)
    }

    fun handleIntent(intent: EnvelopeAnalyticsIntent) {
        when (intent) {
            is EnvelopeAnalyticsIntent.LoadAnalytics -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    
                    repository.getAllActiveEnvelopes()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { envelopes ->
                            val timeZone = TimeZone.currentSystemDefault()
                            val today = Clock.System.now().toLocalDateTime(timeZone).date
                            val startDate = today.let {
                                var date = LocalDate(it.year, it.monthNumber, 1)
                                repeat(3) {
                                    date = if (date.monthNumber == 1) {
                                        LocalDate(date.year - 1, 12, 1)
                                    } else {
                                        LocalDate(date.year, date.monthNumber - 1, 1)
                                    }
                                }
                                date
                            }
                            
                            val spendingTrends = mutableMapOf<Long, List<MonthlySpending>>()
                            for (envelope in envelopes) {
                                val trend = repository.getEnvelopeSpendingTrend(envelope.id, 3)
                                spendingTrends[envelope.id] = trend
                            }
                            
                            val spendingByEnvelope = repository.getTotalSpendingByEnvelope(startDate, today)
                            
                            _state.update {
                                it.copy(
                                    envelopes = envelopes,
                                    spendingTrends = spendingTrends,
                                    spendingByEnvelope = spendingByEnvelope,
                                    isLoading = false
                                )
                            }
                        }
                }
            }
        }
    }
}
