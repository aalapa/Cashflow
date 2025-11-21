package com.cashflow.app.ui.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime

class BillsViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BillsState())
    val state: StateFlow<BillsState> = _state.asStateFlow()

    init {
        handleIntent(BillsIntent.LoadBills)
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
            repository.getAllActiveBills()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { bills ->
                    val timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
                    val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(timeZone).date
                    val endDate = kotlinx.datetime.LocalDate.fromEpochDays(today.toEpochDays() + 365) // Next year
                    
                    val occurrencesMap = bills.associateWith { bill ->
                        // Get future occurrences and filter out already paid ones
                        repository.getFutureBillOccurrences(bill, today, endDate)
                            .filter { !it.isPaid }
                    }.mapKeys { it.key.id }
                    
                    _state.update { it.copy(billOccurrences = occurrencesMap) }
                }
        }
    }

    fun handleIntent(intent: BillsIntent) {
        when (intent) {
            is BillsIntent.LoadBills -> {
                viewModelScope.launch {
                    repository.getAllActiveBills()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { bills ->
                            _state.update { it.copy(bills = bills, isLoading = false) }
                            loadFutureOccurrences()
                        }
                }
            }
            is BillsIntent.ShowAddDialog -> {
                _state.update { it.copy(showAddDialog = true, editingBill = null) }
            }
            is BillsIntent.HideAddDialog -> {
                _state.update { it.copy(showAddDialog = false, editingBill = null) }
            }
            is BillsIntent.EditBill -> {
                _state.update { it.copy(showAddDialog = true, editingBill = intent.bill) }
            }
            is BillsIntent.SaveBill -> {
                viewModelScope.launch {
                    try {
                        if (intent.bill.id == 0L) {
                            repository.insertBill(intent.bill)
                        } else {
                            repository.updateBill(intent.bill)
                        }
                        _state.update { it.copy(showAddDialog = false, editingBill = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is BillsIntent.DeleteBill -> {
                viewModelScope.launch {
                    try {
                        repository.deleteBill(intent.bill)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is BillsIntent.MarkBillAsPaid -> {
                viewModelScope.launch {
                    try {
                        repository.markBillAsPaid(
                            billId = intent.occurrence.bill.id,
                            dueDate = intent.occurrence.dueDate,
                            accountId = intent.accountId,
                            amount = intent.occurrence.amount
                        )
                        _state.update { it.copy(showMarkPaidDialog = false, billToMarkPaid = null) }
                        loadFutureOccurrences()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is BillsIntent.ShowMarkPaidDialog -> {
                _state.update { it.copy(showMarkPaidDialog = true, billToMarkPaid = intent.occurrence) }
            }
            is BillsIntent.HideMarkPaidDialog -> {
                _state.update { it.copy(showMarkPaidDialog = false, billToMarkPaid = null) }
            }
            is BillsIntent.SetViewMode -> {
                _state.update { it.copy(viewMode = intent.mode) }
            }
            is BillsIntent.EditBillAmount -> {
                viewModelScope.launch {
                    try {
                        repository.setBillOverride(
                            billId = intent.occurrence.bill.id,
                            date = intent.occurrence.dueDate,
                            amount = intent.newAmount
                        )
                        _state.update { it.copy(showEditAmountDialog = false, billToEditAmount = null) }
                        loadFutureOccurrences()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is BillsIntent.ShowEditAmountDialog -> {
                _state.update { it.copy(showEditAmountDialog = true, billToEditAmount = intent.occurrence) }
            }
            is BillsIntent.HideEditAmountDialog -> {
                _state.update { it.copy(showEditAmountDialog = false, billToEditAmount = null) }
            }
        }
    }
}

