package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.Envelope
import com.cashflow.app.domain.repository.EnvelopePeriodHistory

data class EnvelopeHistoryState(
    val envelope: Envelope? = null,
    val history: List<EnvelopePeriodHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeHistoryIntent {
    data class LoadHistory(val envelopeId: Long) : EnvelopeHistoryIntent()
}

