package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.Envelope
import com.cashflow.app.domain.repository.MonthlySpending

data class EnvelopeAnalyticsState(
    val envelopes: List<Envelope> = emptyList(),
    val spendingTrends: Map<Long, List<MonthlySpending>> = emptyMap(),
    val spendingByEnvelope: Map<Long, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeAnalyticsIntent {
    object LoadAnalytics : EnvelopeAnalyticsIntent()
}
