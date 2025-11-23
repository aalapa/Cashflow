package com.cashflow.app.ui.envelopes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.domain.model.CategorizationRule
import com.cashflow.app.domain.repository.CashFlowRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizationRulesScreen(
    repository: CashFlowRepository,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: CategorizationRulesViewModel = viewModel { CategorizationRulesViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto-Categorization Rules") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(CategorizationRulesIntent.ShowAddDialog) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Rule")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (state.rules.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Label,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No categorization rules",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add rules to automatically categorize transactions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.rules) { rule ->
                            RuleItem(
                                rule = rule,
                                envelope = state.envelopes.find { it.id == rule.envelopeId },
                                onEdit = { viewModel.handleIntent(CategorizationRulesIntent.EditRule(rule)) },
                                onDelete = { viewModel.handleIntent(CategorizationRulesIntent.DeleteRule(rule)) }
                            )
                        }
                    }
                }
            }
        }

        // Add/Edit Dialog
        if (state.showAddDialog) {
            RuleDialog(
                rule = state.editingRule,
                envelopes = state.envelopes,
                selectedEnvelopeId = state.selectedEnvelopeId,
                keyword = state.keyword,
                onEnvelopeSelected = { viewModel.handleIntent(CategorizationRulesIntent.SetSelectedEnvelope(it)) },
                onKeywordChange = { viewModel.handleIntent(CategorizationRulesIntent.SetKeyword(it)) },
                onSave = {
                    val envelopeId = state.selectedEnvelopeId
                    if (envelopeId != null && state.keyword.isNotBlank()) {
                        viewModel.handleIntent(
                            CategorizationRulesIntent.SaveRule(
                                CategorizationRule(
                                    id = state.editingRule?.id ?: 0,
                                    envelopeId = envelopeId,
                                    keyword = state.keyword,
                                    isActive = true
                                )
                            )
                        )
                    }
                },
                onDismiss = { viewModel.handleIntent(CategorizationRulesIntent.HideAddDialog) }
            )
        }
    }
}

@Composable
fun RuleItem(
    rule: CategorizationRule,
    envelope: com.cashflow.app.domain.model.Envelope?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                envelope?.let {
                    Icon(
                        imageVector = getIconForString(it.icon ?: "Folder"),
                        contentDescription = null,
                        tint = it.color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    Text(
                        text = "\"${rule.keyword}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = envelope?.name ?: "Unknown Envelope",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleDialog(
    rule: CategorizationRule?,
    envelopes: List<com.cashflow.app.domain.model.Envelope>,
    selectedEnvelopeId: Long?,
    keyword: String,
    onEnvelopeSelected: (Long) -> Unit,
    onKeywordChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var envelopeExpanded by remember { mutableStateOf(false) }
    val selectedEnvelope = envelopes.find { it.id == selectedEnvelopeId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (rule == null) "Add Rule" else "Edit Rule") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = keyword,
                    onValueChange = onKeywordChange,
                    label = { Text("Keyword") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., 'grocery', 'gas', 'amazon'") }
                )

                ExposedDropdownMenuBox(
                    expanded = envelopeExpanded,
                    onExpandedChange = { envelopeExpanded = !envelopeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEnvelope?.name ?: "Select Envelope",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Envelope") },
                        leadingIcon = {
                            selectedEnvelope?.let {
                                Icon(
                                    imageVector = getIconForString(it.icon ?: "Folder"),
                                    contentDescription = null,
                                    tint = it.color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = envelopeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = envelopeExpanded,
                        onDismissRequest = { envelopeExpanded = false }
                    ) {
                        envelopes.forEach { envelope ->
                            DropdownMenuItem(
                                text = { Text(envelope.name) },
                                onClick = {
                                    onEnvelopeSelected(envelope.id)
                                    envelopeExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getIconForString(envelope.icon ?: "Folder"),
                                        contentDescription = null,
                                        tint = envelope.color
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = selectedEnvelopeId != null && keyword.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

