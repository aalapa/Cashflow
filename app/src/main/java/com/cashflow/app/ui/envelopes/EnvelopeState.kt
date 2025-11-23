package com.cashflow.app.ui.envelopes

import androidx.compose.ui.graphics.Color
import com.cashflow.app.data.model.RecurrenceType
import com.cashflow.app.domain.model.Envelope

data class EnvelopeState(
    val envelopes: List<Envelope> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingEnvelope: Envelope? = null,
    val selectedColor: Color = Color(0xFF7C3AED), // Purple
    val selectedIcon: String = "Folder" // Default icon
)

sealed class EnvelopeIntent {
    object LoadEnvelopes : EnvelopeIntent()
    object ShowAddDialog : EnvelopeIntent()
    object HideAddDialog : EnvelopeIntent()
    data class EditEnvelope(val envelope: Envelope) : EnvelopeIntent()
    data class SaveEnvelope(val envelope: Envelope) : EnvelopeIntent()
    data class DeleteEnvelope(val envelope: Envelope) : EnvelopeIntent()
    data class SetSelectedColor(val color: Color) : EnvelopeIntent()
    data class SetSelectedIcon(val icon: String) : EnvelopeIntent()
}
