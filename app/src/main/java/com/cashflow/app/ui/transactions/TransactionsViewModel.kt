package com.cashflow.app.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        handleIntent(TransactionsIntent.LoadTransactions)
        loadAccounts()
        loadEnvelopes()
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

    private fun loadEnvelopes() {
        viewModelScope.launch {
            repository.getAllActiveEnvelopes()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { envelopes ->
                    _state.update { it.copy(envelopes = envelopes) }
                }
        }
    }

    fun handleIntent(intent: TransactionsIntent) {
        when (intent) {
            is TransactionsIntent.LoadTransactions -> {
                viewModelScope.launch {
                    repository.getAllTransactions()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { transactions ->
                            _state.update { it.copy(transactions = transactions, isLoading = false) }
                        }
                }
            }
            is TransactionsIntent.ShowAddDialog -> {
                _state.update { it.copy(showAddDialog = true, editingTransaction = null) }
            }
            is TransactionsIntent.HideAddDialog -> {
                _state.update { it.copy(showAddDialog = false, editingTransaction = null) }
            }
            is TransactionsIntent.EditTransaction -> {
                _state.update { it.copy(showAddDialog = true, editingTransaction = intent.transaction) }
            }
            is TransactionsIntent.SaveTransaction -> {
                viewModelScope.launch {
                    try {
                        var transaction = intent.transaction
                        // Apply auto-categorization if no envelope is set
                        if (transaction.envelopeId == null) {
                            val envelopeId = repository.applyAutoCategorization(transaction)
                            if (envelopeId != null) {
                                transaction = transaction.copy(envelopeId = envelopeId)
                            }
                        }
                        
                        if (transaction.id == 0L) {
                            repository.insertTransaction(transaction)
                        } else {
                            repository.updateTransaction(transaction)
                        }
                        _state.update { it.copy(showAddDialog = false, editingTransaction = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TransactionsIntent.DeleteTransaction -> {
                viewModelScope.launch {
                    try {
                        repository.deleteTransaction(intent.transaction)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
        }
    }
}

