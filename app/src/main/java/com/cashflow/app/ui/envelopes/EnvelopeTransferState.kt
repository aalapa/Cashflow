package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.BudgetCategory

data class EnvelopeTransferState(
    val categories: List<BudgetCategory> = emptyList(),
    val fromCategoryId: Long? = null,
    val toCategoryId: Long? = null,
    val amount: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeTransferIntent {
    object LoadCategories : EnvelopeTransferIntent()
    data class SetFromCategory(val categoryId: Long) : EnvelopeTransferIntent()
    data class SetToCategory(val categoryId: Long) : EnvelopeTransferIntent()
    data class SetAmount(val amount: String) : EnvelopeTransferIntent()
    data class SetDescription(val description: String) : EnvelopeTransferIntent()
    object SaveTransfer : EnvelopeTransferIntent()
}
