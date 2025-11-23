package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.Envelope

data class EnvelopeTransferState(
    val envelopes: List<Envelope> = emptyList(),
    val fromEnvelopeId: Long? = null,
    val toEnvelopeId: Long? = null,
    val amount: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeTransferIntent {
    object LoadEnvelopes : EnvelopeTransferIntent()
    data class SetFromEnvelope(val envelopeId: Long) : EnvelopeTransferIntent()
    data class SetToEnvelope(val envelopeId: Long) : EnvelopeTransferIntent()
    data class SetAmount(val amount: String) : EnvelopeTransferIntent()
    data class SetDescription(val description: String) : EnvelopeTransferIntent()
    object SaveTransfer : EnvelopeTransferIntent()
}
