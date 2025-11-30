package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.BudgetCategory
import com.cashflow.app.domain.repository.MonthlySpending

data class EnvelopeAnalyticsState(
    val categories: List<BudgetCategory> = emptyList(),
    val spendingTrends: Map<Long, List<MonthlySpending>> = emptyMap(),
    val spendingByCategory: Map<Long, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeAnalyticsIntent {
    object LoadAnalytics : EnvelopeAnalyticsIntent()
}
