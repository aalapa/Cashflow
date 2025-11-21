package com.cashflow.app.ui.transactions

import com.cashflow.app.domain.model.Transaction

data class TransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val accounts: List<com.cashflow.app.domain.model.Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingTransaction: Transaction? = null
)

sealed class TransactionsIntent {
    object LoadTransactions : TransactionsIntent()
    object ShowAddDialog : TransactionsIntent()
    object HideAddDialog : TransactionsIntent()
    data class EditTransaction(val transaction: Transaction) : TransactionsIntent()
    data class SaveTransaction(val transaction: Transaction) : TransactionsIntent()
    data class DeleteTransaction(val transaction: Transaction) : TransactionsIntent()
}

