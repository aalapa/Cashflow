package com.cashflow.app.ui.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.model.CategorizationRule
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategorizationRulesViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategorizationRulesState())
    val state: StateFlow<CategorizationRulesState> = _state.asStateFlow()

    init {
        handleIntent(CategorizationRulesIntent.LoadRules)
    }

    fun handleIntent(intent: CategorizationRulesIntent) {
        when (intent) {
            is CategorizationRulesIntent.LoadRules -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    
                    repository.getAllActiveCategories()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { categories ->
                            _state.update { it.copy(categories = categories) }
                        }
                    
                    repository.getAllCategorizationRules()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { rules ->
                            _state.update { it.copy(rules = rules, isLoading = false) }
                        }
                }
            }
            is CategorizationRulesIntent.ShowAddDialog -> {
                _state.update {
                    it.copy(
                        showAddDialog = true,
                        editingRule = null,
                        selectedCategoryId = null,
                        keyword = ""
                    )
                }
            }
            is CategorizationRulesIntent.HideAddDialog -> {
                _state.update {
                    it.copy(
                        showAddDialog = false,
                        editingRule = null,
                        selectedCategoryId = null,
                        keyword = ""
                    )
                }
            }
            is CategorizationRulesIntent.EditRule -> {
                _state.update {
                    it.copy(
                        showAddDialog = true,
                        editingRule = intent.rule,
                        selectedCategoryId = intent.rule.categoryId,
                        keyword = intent.rule.keyword
                    )
                }
            }
            is CategorizationRulesIntent.SaveRule -> {
                viewModelScope.launch {
                    try {
                        if (intent.rule.id == 0L) {
                            repository.insertCategorizationRule(intent.rule)
                        } else {
                            repository.updateCategorizationRule(intent.rule)
                        }
                        _state.update {
                            it.copy(
                                showAddDialog = false,
                                editingRule = null,
                                selectedCategoryId = null,
                                keyword = ""
                            )
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is CategorizationRulesIntent.DeleteRule -> {
                viewModelScope.launch {
                    try {
                        repository.deleteCategorizationRule(intent.rule)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is CategorizationRulesIntent.SetSelectedCategory -> {
                _state.update { it.copy(selectedCategoryId = intent.categoryId) }
            }
            is CategorizationRulesIntent.SetKeyword -> {
                _state.update { it.copy(keyword = intent.keyword) }
            }
        }
    }
}
