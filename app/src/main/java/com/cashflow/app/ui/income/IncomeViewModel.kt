package com.cashflow.app.ui.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime

class IncomeViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IncomeState())
    val state: StateFlow<IncomeState> = _state.asStateFlow()

    init {
        handleIntent(IncomeIntent.LoadIncome)
        loadAccounts()
        loadFutureOccurrences()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            repository.getAllAccounts()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { accounts ->
                    _state.update { it.copy(accounts = accounts) }
                }
        }
    }

    private fun loadFutureOccurrences() {
        viewModelScope.launch {
            repository.getAllActiveIncome()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { incomeList ->
                    val timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
                    val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(timeZone).date
                    val endDate = kotlinx.datetime.LocalDate.fromEpochDays(today.toEpochDays() + 365) // Next year
                    
                    val occurrencesMap = incomeList.associateWith { income ->
                        repository.getFutureIncomeOccurrences(income, today, endDate)
                    }.mapKeys { it.key.id }
                    
                    _state.update { it.copy(incomeOccurrences = occurrencesMap) }
                }
        }
    }

    fun handleIntent(intent: IncomeIntent) {
        when (intent) {
            is IncomeIntent.LoadIncome -> {
                viewModelScope.launch {
                    repository.getAllActiveIncome()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { incomeList ->
                            _state.update { it.copy(incomeList = incomeList, isLoading = false) }
                            loadFutureOccurrences()
                        }
                }
            }
            is IncomeIntent.ShowAddDialog -> {
                _state.update { it.copy(showAddDialog = true, editingIncome = null) }
            }
            is IncomeIntent.HideAddDialog -> {
                _state.update { it.copy(showAddDialog = false, editingIncome = null) }
            }
            is IncomeIntent.EditIncome -> {
                _state.update { it.copy(showAddDialog = true, editingIncome = intent.income) }
            }
            is IncomeIntent.SaveIncome -> {
                viewModelScope.launch {
                    try {
                        if (intent.income.id == 0L) {
                            repository.insertIncome(intent.income)
                        } else {
                            repository.updateIncome(intent.income)
                        }
                        _state.update { it.copy(showAddDialog = false, editingIncome = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is IncomeIntent.DeleteIncome -> {
                viewModelScope.launch {
                    try {
                        repository.deleteIncome(intent.income)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is IncomeIntent.EditIncomeAmount -> {
                viewModelScope.launch {
                    try {
                        repository.setIncomeOverride(
                            incomeId = intent.occurrence.income.id,
                            date = intent.occurrence.date,
                            amount = intent.newAmount
                        )
                        _state.update { it.copy(showEditAmountDialog = false, incomeToEditAmount = null) }
                        loadFutureOccurrences()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is IncomeIntent.ShowEditAmountDialog -> {
                _state.update { it.copy(showEditAmountDialog = true, incomeToEditAmount = intent.occurrence) }
            }
            is IncomeIntent.HideEditAmountDialog -> {
                _state.update { it.copy(showEditAmountDialog = false, incomeToEditAmount = null) }
            }
            is IncomeIntent.MarkIncomeAsReceived -> {
                viewModelScope.launch {
                    try {
                        repository.markIncomeAsReceived(
                            incomeId = intent.occurrence.income.id,
                            date = intent.occurrence.date,
                            accountId = intent.accountId,
                            amount = intent.occurrence.amount
                        )
                        _state.update { it.copy(showReceivedDialog = false, incomeToMarkReceived = null) }
                        loadFutureOccurrences()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is IncomeIntent.ShowReceivedDialog -> {
                _state.update { it.copy(showReceivedDialog = true, incomeToMarkReceived = intent.occurrence) }
            }
            is IncomeIntent.HideReceivedDialog -> {
                _state.update { it.copy(showReceivedDialog = false, incomeToMarkReceived = null) }
            }
        }
    }
}

