package com.cashflow.app.ui.allocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.model.EnvelopeAllocation
import com.cashflow.app.domain.model.IncomeOccurrence
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AllocationViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AllocationState())
    val state: StateFlow<AllocationState> = _state.asStateFlow()

    init {
        handleIntent(AllocationIntent.LoadData)
    }

    fun handleIntent(intent: AllocationIntent) {
        when (intent) {
            is AllocationIntent.LoadData -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    
                    // Load envelopes
                    repository.getAllActiveEnvelopes()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { envelopes ->
                            _state.update { it.copy(envelopes = envelopes) }
                        }
                    
                    // Load upcoming income
                    repository.getAllActiveIncome()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { incomeList ->
                            val timeZone = TimeZone.currentSystemDefault()
                            val today = Clock.System.now().toLocalDateTime(timeZone).date
                            val endDate = LocalDate.fromEpochDays(today.toEpochDays() + 365)
                            
                            val occurrences = incomeList.flatMap { income ->
                                repository.getFutureIncomeOccurrences(income, today, endDate)
                                    .filter { !it.isReceived }
                            }.sortedBy { it.date }
                            
                            _state.update { it.copy(incomeOccurrences = occurrences, isLoading = false) }
                        }
                }
            }
            is AllocationIntent.SelectIncome -> {
                _state.update {
                    it.copy(
                        selectedIncomeOccurrence = intent.occurrence,
                        allocations = emptyMap(),
                        showAllocationDialog = true
                    )
                }
            }
            is AllocationIntent.HideAllocationDialog -> {
                _state.update {
                    it.copy(
                        selectedIncomeOccurrence = null,
                        allocations = emptyMap(),
                        showAllocationDialog = false
                    )
                }
            }
            is AllocationIntent.SetAllocation -> {
                val currentAllocations = _state.value.allocations.toMutableMap()
                if (intent.amount > 0) {
                    currentAllocations[intent.envelopeId] = intent.amount
                } else {
                    currentAllocations.remove(intent.envelopeId)
                }
                _state.update { it.copy(allocations = currentAllocations) }
            }
            is AllocationIntent.SaveAllocations -> {
                viewModelScope.launch {
                    try {
                        val selectedOccurrence = _state.value.selectedIncomeOccurrence
                        if (selectedOccurrence == null) return@launch
                        
                        val allocations = _state.value.allocations
                        val totalAllocated = allocations.values.sum()
                        
                        if (totalAllocated > selectedOccurrence.amount) {
                            _state.update { it.copy(error = "Total allocated cannot exceed income amount") }
                            return@launch
                        }
                        
                        // Create allocations for each envelope
                        for ((envelopeId, amount) in allocations) {
                            val envelope = _state.value.envelopes.find { it.id == envelopeId }
                            if (envelope != null) {
                                val (periodStart, periodEnd) = calculatePeriodDates(selectedOccurrence.date, envelope.periodType)
                                
                                // Check for carry-over
                                var finalAmount = amount
                                if (envelope.carryOverEnabled) {
                                    val previousAllocation = repository.getAllocationForPeriod(envelopeId, selectedOccurrence.date)
                                    if (previousAllocation != null) {
                                        val previousTransactions = repository.getEnvelopeTransactions(envelopeId).first()
                                            .filter { 
                                                it.date >= previousAllocation.periodStart && 
                                                it.date <= previousAllocation.periodEnd 
                                            }
                                        val previousSpent = previousTransactions.sumOf {
                                            when (it.type) {
                                                com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                                                com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                                                else -> 0.0
                                            }
                                        }
                                        val previousBalance = previousAllocation.amount - previousSpent
                                        if (previousBalance > 0) {
                                            finalAmount = amount + previousBalance
                                        }
                                    }
                                }
                                
                                val allocation = EnvelopeAllocation(
                                    id = 0,
                                    envelopeId = envelopeId,
                                    amount = finalAmount,
                                    periodStart = periodStart,
                                    periodEnd = periodEnd,
                                    incomeId = selectedOccurrence.income.id,
                                    createdAt = Clock.System.now()
                                )
                                
                                repository.insertAllocation(allocation)
                            }
                        }
                        
                        _state.update {
                            it.copy(
                                selectedIncomeOccurrence = null,
                                allocations = emptyMap(),
                                showAllocationDialog = false
                            )
                        }
                        handleIntent(AllocationIntent.LoadData) // Reload
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
        }
    }
    
    private fun calculatePeriodDates(date: LocalDate, periodType: com.cashflow.app.data.model.RecurrenceType): Pair<LocalDate, LocalDate> {
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
