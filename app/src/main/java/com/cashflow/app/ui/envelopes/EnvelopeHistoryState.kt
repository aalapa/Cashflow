package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.BudgetCategory
import com.cashflow.app.domain.repository.CategoryPeriodHistory

data class EnvelopeHistoryState(
    val category: BudgetCategory? = null,
    val history: List<CategoryPeriodHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeHistoryIntent {
    data class LoadHistory(val categoryId: Long) : EnvelopeHistoryIntent()
}

