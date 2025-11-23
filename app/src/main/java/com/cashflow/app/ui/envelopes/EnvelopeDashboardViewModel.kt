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

class EnvelopeDashboardViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EnvelopeDashboardState())
    val state: StateFlow<EnvelopeDashboardState> = _state.asStateFlow()

    init {
        handleIntent(EnvelopeDashboardIntent.LoadDashboard)
    }

    fun handleIntent(intent: EnvelopeDashboardIntent) {
        when (intent) {
            is EnvelopeDashboardIntent.LoadDashboard -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    
                    repository.getAllActiveEnvelopes()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { envelopes ->
                            loadBalances(envelopes, _state.value.selectedDate)
                        }
                }
            }
            is EnvelopeDashboardIntent.SetDate -> {
                viewModelScope.launch {
                    val currentEnvelopes = _state.value.envelopes
                    loadBalances(currentEnvelopes, intent.date)
                }
            }
        }
    }
    
    private suspend fun loadBalances(envelopes: List<com.cashflow.app.domain.model.Envelope>, date: LocalDate) {
        val balancesMap = mutableMapOf<Long, EnvelopeBalance>()
        
        for (envelope in envelopes) {
            // Get allocation for current period
            val allocation = repository.getAllocationForPeriod(envelope.id, date)
            val allocated = allocation?.amount ?: 0.0
            
            // Use allocation period if available, otherwise calculate from date
            val (periodStart, periodEnd) = if (allocation != null) {
                Pair(allocation.periodStart, allocation.periodEnd)
            } else {
                calculatePeriodDates(date, envelope.periodType)
            }
            
            // Get transactions for this envelope within the period
            val transactions = repository.getEnvelopeTransactions(envelope.id).first()
            val periodTransactions = transactions.filter { 
                it.date >= periodStart && it.date <= periodEnd && it.date <= date
            }
            
            val spent = periodTransactions.sumOf { 
                when (it.type) {
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                    else -> 0.0
                }
            }
            
            // Get transfers for this envelope within the period
            val transfers = repository.getEnvelopeTransfers(envelope.id).first()
                .filter { it.date >= periodStart && it.date <= periodEnd && it.date <= date }
            
            val transferOut = transfers.filter { it.fromEnvelopeId == envelope.id }.sumOf { it.amount }
            val transferIn = transfers.filter { it.toEnvelopeId == envelope.id }.sumOf { it.amount }
            
            // Balance = allocated + transfers in - spent - transfers out
            val balance = allocated + transferIn - spent - transferOut
            
            balancesMap[envelope.id] = EnvelopeBalance(
                envelope = envelope,
                allocated = allocated,
                spent = spent,
                balance = balance,
                periodStart = periodStart,
                periodEnd = periodEnd
            )
        }
        
        _state.update {
            it.copy(
                envelopes = envelopes,
                balances = balancesMap,
                isLoading = false
            )
        }
    }
    
    private fun calculatePeriodDates(
        date: LocalDate,
        periodType: com.cashflow.app.data.model.RecurrenceType
    ): Pair<LocalDate, LocalDate> {
        return when (periodType) {
            com.cashflow.app.data.model.RecurrenceType.MONTHLY -> {
                val start = LocalDate(date.year, date.monthNumber, 1)
                val end = if (date.monthNumber == 12) {
                    LocalDate(date.year + 1, 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                } else {
                    LocalDate(date.year, date.monthNumber + 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                }
                Pair(start, end)
            }
            com.cashflow.app.data.model.RecurrenceType.BI_WEEKLY -> {
                val start = date
                val end = LocalDate.fromEpochDays(date.toEpochDays() + 13)
                Pair(start, end)
            }
            com.cashflow.app.data.model.RecurrenceType.WEEKLY -> {
                val start = date
                val end = LocalDate.fromEpochDays(date.toEpochDays() + 6)
                Pair(start, end)
            }
            else -> {
                val start = LocalDate(date.year, date.monthNumber, 1)
                val end = if (date.monthNumber == 12) {
                    LocalDate(date.year + 1, 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                } else {
                    LocalDate(date.year, date.monthNumber + 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                }
                Pair(start, end)
            }
        }
    }
}
