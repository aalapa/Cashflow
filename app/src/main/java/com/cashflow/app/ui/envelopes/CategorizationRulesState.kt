package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.CategorizationRule
import com.cashflow.app.domain.model.BudgetCategory

data class CategorizationRulesState(
    val rules: List<CategorizationRule> = emptyList(),
    val categories: List<BudgetCategory> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingRule: CategorizationRule? = null,
    val selectedCategoryId: Long? = null,
    val keyword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CategorizationRulesIntent {
    object LoadRules : CategorizationRulesIntent()
    object ShowAddDialog : CategorizationRulesIntent()
    object HideAddDialog : CategorizationRulesIntent()
    data class EditRule(val rule: CategorizationRule) : CategorizationRulesIntent()
    data class SaveRule(val rule: CategorizationRule) : CategorizationRulesIntent()
    data class DeleteRule(val rule: CategorizationRule) : CategorizationRulesIntent()
    data class SetSelectedCategory(val categoryId: Long) : CategorizationRulesIntent()
    data class SetKeyword(val keyword: String) : CategorizationRulesIntent()
}
