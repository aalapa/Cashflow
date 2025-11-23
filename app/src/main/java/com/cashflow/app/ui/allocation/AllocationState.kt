package com.cashflow.app.ui.allocation

import com.cashflow.app.domain.model.Envelope
import com.cashflow.app.domain.model.IncomeOccurrence

data class AllocationState(
    val incomeOccurrences: List<IncomeOccurrence> = emptyList(),
    val envelopes: List<Envelope> = emptyList(),
    val allocations: Map<Long, Double> = emptyMap(), // envelopeId -> amount
    val selectedIncomeOccurrence: IncomeOccurrence? = null,
    val showAllocationDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AllocationIntent {
    object LoadData : AllocationIntent()
    data class SelectIncome(val occurrence: IncomeOccurrence) : AllocationIntent()
    object HideAllocationDialog : AllocationIntent()
    data class SetAllocation(val envelopeId: Long, val amount: Double) : AllocationIntent()
    object SaveAllocations : AllocationIntent()
}
