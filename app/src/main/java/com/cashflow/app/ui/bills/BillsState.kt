package com.cashflow.app.ui.bills

import com.cashflow.app.domain.model.Bill
import com.cashflow.app.domain.model.BillOccurrence

enum class BillsViewMode {
    DEFAULT,
    DATE_SORTED
}

data class BillsState(
    val bills: List<Bill> = emptyList(),
    val billOccurrences: Map<Long, List<BillOccurrence>> = emptyMap(),
    val accounts: List<com.cashflow.app.domain.model.Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingBill: Bill? = null,
    val showMarkPaidDialog: Boolean = false,
    val billToMarkPaid: BillOccurrence? = null,
    val viewMode: BillsViewMode = BillsViewMode.DEFAULT
)

sealed class BillsIntent {
    object LoadBills : BillsIntent()
    object ShowAddDialog : BillsIntent()
    object HideAddDialog : BillsIntent()
    data class EditBill(val bill: Bill) : BillsIntent()
    data class SaveBill(val bill: Bill) : BillsIntent()
    data class DeleteBill(val bill: Bill) : BillsIntent()
    data class MarkBillAsPaid(val occurrence: BillOccurrence, val accountId: Long) : BillsIntent()
    object HideMarkPaidDialog : BillsIntent()
    data class ShowMarkPaidDialog(val occurrence: BillOccurrence) : BillsIntent()
    data class SetViewMode(val mode: BillsViewMode) : BillsIntent()
}

