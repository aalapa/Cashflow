package com.cashflow.app.ui.envelopes

import androidx.compose.ui.graphics.Color
import com.cashflow.app.data.model.RecurrenceType
import com.cashflow.app.domain.model.BudgetCategory

data class EnvelopeState(
    val categories: List<BudgetCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingCategory: BudgetCategory? = null,
    val selectedColor: Color = Color(0xFF7C3AED), // Purple
    val selectedIcon: String = "Folder" // Default icon
)

sealed class EnvelopeIntent {
    object LoadCategories : EnvelopeIntent()
    object ShowAddDialog : EnvelopeIntent()
    object HideAddDialog : EnvelopeIntent()
    data class EditCategory(val category: BudgetCategory) : EnvelopeIntent()
    data class SaveCategory(val category: BudgetCategory) : EnvelopeIntent()
    data class DeleteCategory(val category: BudgetCategory) : EnvelopeIntent()
    data class SetSelectedColor(val color: Color) : EnvelopeIntent()
    data class SetSelectedIcon(val icon: String) : EnvelopeIntent()
}
