package com.cashflow.app.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AccountsState())
    val state: StateFlow<AccountsState> = _state.asStateFlow()

    init {
        handleIntent(AccountsIntent.LoadAccounts)
    }

    fun handleIntent(intent: AccountsIntent) {
        when (intent) {
            is AccountsIntent.LoadAccounts -> {
                viewModelScope.launch {
                    repository.getAllAccounts()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { accounts ->
                            _state.update { it.copy(accounts = accounts, isLoading = false) }
                        }
                }
            }
            is AccountsIntent.ShowAddDialog -> {
                _state.update { it.copy(showAddDialog = true, editingAccount = null) }
            }
            is AccountsIntent.HideAddDialog -> {
                _state.update { it.copy(showAddDialog = false, editingAccount = null) }
            }
            is AccountsIntent.EditAccount -> {
                _state.update { it.copy(showAddDialog = true, editingAccount = intent.account) }
            }
            is AccountsIntent.SaveAccount -> {
                viewModelScope.launch {
                    try {
                        if (intent.account.id == 0L) {
                            repository.insertAccount(intent.account)
                        } else {
                            repository.updateAccount(intent.account)
                        }
                        _state.update { it.copy(showAddDialog = false, editingAccount = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is AccountsIntent.DeleteAccount -> {
                viewModelScope.launch {
                    try {
                        repository.deleteAccount(intent.account)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
        }
    }
}

