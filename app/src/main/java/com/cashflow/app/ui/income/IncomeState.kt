package com.cashflow.app.ui.income

import com.cashflow.app.domain.model.Income
import com.cashflow.app.domain.model.IncomeOccurrence

data class IncomeState(
    val incomeList: List<Income> = emptyList(),
    val incomeOccurrences: Map<Long, List<IncomeOccurrence>> = emptyMap(),
    val accounts: List<com.cashflow.app.domain.model.Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingIncome: Income? = null,
    val showEditAmountDialog: Boolean = false,
    val incomeToEditAmount: IncomeOccurrence? = null,
    val showReceivedDialog: Boolean = false,
    val incomeToMarkReceived: IncomeOccurrence? = null
)

sealed class IncomeIntent {
    object LoadIncome : IncomeIntent()
    object ShowAddDialog : IncomeIntent()
    object HideAddDialog : IncomeIntent()
    data class EditIncome(val income: Income) : IncomeIntent()
    data class SaveIncome(val income: Income) : IncomeIntent()
    data class DeleteIncome(val income: Income) : IncomeIntent()
    data class EditIncomeAmount(val occurrence: IncomeOccurrence, val newAmount: Double) : IncomeIntent()
    object HideEditAmountDialog : IncomeIntent()
    data class ShowEditAmountDialog(val occurrence: IncomeOccurrence) : IncomeIntent()
    data class MarkIncomeAsReceived(val occurrence: IncomeOccurrence, val accountId: Long) : IncomeIntent()
    object HideReceivedDialog : IncomeIntent()
    data class ShowReceivedDialog(val occurrence: IncomeOccurrence) : IncomeIntent()
}

