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
                    
                    repository.getAllActiveEnvelopes()
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect { envelopes ->
                            _state.update { it.copy(envelopes = envelopes) }
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
                        selectedEnvelopeId = null,
                        keyword = ""
                    )
                }
            }
            is CategorizationRulesIntent.HideAddDialog -> {
                _state.update {
                    it.copy(
                        showAddDialog = false,
                        editingRule = null,
                        selectedEnvelopeId = null,
                        keyword = ""
                    )
                }
            }
            is CategorizationRulesIntent.EditRule -> {
                _state.update {
                    it.copy(
                        showAddDialog = true,
                        editingRule = intent.rule,
                        selectedEnvelopeId = intent.rule.envelopeId,
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
                                selectedEnvelopeId = null,
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
            is CategorizationRulesIntent.SetSelectedEnvelope -> {
                _state.update { it.copy(selectedEnvelopeId = intent.envelopeId) }
            }
            is CategorizationRulesIntent.SetKeyword -> {
                _state.update { it.copy(keyword = intent.keyword) }
            }
        }
    }
}
