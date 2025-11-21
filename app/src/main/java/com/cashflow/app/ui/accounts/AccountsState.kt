package com.cashflow.app.ui.accounts

import com.cashflow.app.domain.model.Account

data class AccountsState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingAccount: Account? = null
)

sealed class AccountsIntent {
    object LoadAccounts : AccountsIntent()
    object ShowAddDialog : AccountsIntent()
    object HideAddDialog : AccountsIntent()
    data class EditAccount(val account: Account) : AccountsIntent()
    data class SaveAccount(val account: Account) : AccountsIntent()
    data class DeleteAccount(val account: Account) : AccountsIntent()
}

