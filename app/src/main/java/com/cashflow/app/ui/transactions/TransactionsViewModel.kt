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
        loadCategories()
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

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllActiveCategories()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { categories ->
                    _state.update { it.copy(categories = categories) }
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
                        // Apply auto-categorization if no category is set
                        if (transaction.categoryId == null) {
                            val categoryId = repository.applyAutoCategorization(transaction)
                            if (categoryId != null) {
                                transaction = transaction.copy(categoryId = categoryId)
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

