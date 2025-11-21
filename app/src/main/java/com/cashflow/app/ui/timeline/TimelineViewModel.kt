package com.cashflow.app.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.model.Account
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TimelineViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()

    private val accountsFlow = repository.getAllAccounts()
    private val incomeFlow = repository.getAllActiveIncome()
    private val billsFlow = repository.getAllActiveBills()

    init {
        // Watch for changes in accounts, income, or bills - combine all flows
        viewModelScope.launch {
            combine(
                accountsFlow,
                incomeFlow,
                billsFlow
            ) { accounts, _, _ ->
                accounts
            }.collect { accounts ->
                val currentState = _state.value
                if (accounts.isNotEmpty() || currentState.selectedTimePeriod != null) {
                    calculateCashFlow(accounts, currentState)
                }
            }
        }
        
        // Watch for time period changes
        viewModelScope.launch {
            _state.map { it.selectedTimePeriod }
                .distinctUntilChanged()
                .collect {
                    val accounts = accountsFlow.first()
                    val currentState = _state.value
                    calculateCashFlow(accounts, currentState)
                }
        }
    }

    fun handleIntent(intent: TimelineIntent) {
        when (intent) {
            is TimelineIntent.SetTimePeriod -> {
                _state.update { it.copy(selectedTimePeriod = intent.period) }
            }
            is TimelineIntent.Refresh -> {
                // Trigger recalculation by updating state
                _state.update { it }
            }
        }
    }

    private suspend fun calculateCashFlow(accounts: List<Account>, currentState: TimelineState) {
        _state.update { it.copy(isLoading = true, error = null) }

        try {
            val timeZone = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(timeZone).date
            val (startDate, endDate) = when (currentState.selectedTimePeriod) {
                TimePeriod.DAYS_30 -> today to LocalDate.fromEpochDays(today.toEpochDays() + 29)
                TimePeriod.DAYS_60 -> today to LocalDate.fromEpochDays(today.toEpochDays() + 59)
                TimePeriod.DAYS_90 -> today to LocalDate.fromEpochDays(today.toEpochDays() + 89)
                TimePeriod.DAYS_180 -> today to LocalDate.fromEpochDays(today.toEpochDays() + 179)
                TimePeriod.DAYS_360 -> today to LocalDate.fromEpochDays(today.toEpochDays() + 359)
            }

            val cashFlowDays = repository.calculateCashFlow(startDate, endDate, accounts)
            _state.update {
                it.copy(
                    cashFlowDays = cashFlowDays,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Error calculating cash flow"
                )
            }
        }
    }
}

