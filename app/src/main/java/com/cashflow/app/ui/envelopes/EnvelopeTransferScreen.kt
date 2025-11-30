package com.cashflow.app.ui.envelopes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.domain.repository.CashFlowRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopeTransferScreen(
    repository: CashFlowRepository,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: EnvelopeTransferViewModel = viewModel { EnvelopeTransferViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Funds") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // From Category
                var fromExpanded by remember { mutableStateOf(false) }
                val selectedFrom = state.categories.find { it.id == state.fromCategoryId }
                
                ExposedDropdownMenuBox(
                    expanded = fromExpanded,
                    onExpandedChange = { fromExpanded = !fromExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFrom?.name ?: "Select Source Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("From Category") },
                        leadingIcon = {
                            selectedFrom?.let {
                                Icon(
                                    imageVector = getIconForString(it.icon ?: "Folder"),
                                    contentDescription = null,
                                    tint = it.color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = fromExpanded,
                        onDismissRequest = { fromExpanded = false }
                    ) {
                        state.categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.handleIntent(EnvelopeTransferIntent.SetFromCategory(category.id))
                                    fromExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getIconForString(category.icon ?: "Folder"),
                                        contentDescription = null,
                                        tint = category.color
                                    )
                                }
                            )
                        }
                    }
                }

                // To Category
                var toExpanded by remember { mutableStateOf(false) }
                val selectedTo = state.categories.find { it.id == state.toCategoryId }
                
                ExposedDropdownMenuBox(
                    expanded = toExpanded,
                    onExpandedChange = { toExpanded = !toExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTo?.name ?: "Select Destination Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("To Category") },
                        leadingIcon = {
                            selectedTo?.let {
                                Icon(
                                    imageVector = getIconForString(it.icon ?: "Folder"),
                                    contentDescription = null,
                                    tint = it.color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = toExpanded,
                        onDismissRequest = { toExpanded = false }
                    ) {
                        state.categories.filter { it.id != state.fromCategoryId }.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.handleIntent(EnvelopeTransferIntent.SetToCategory(category.id))
                                    toExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getIconForString(category.icon ?: "Folder"),
                                        contentDescription = null,
                                        tint = category.color
                                    )
                                }
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { viewModel.handleIntent(EnvelopeTransferIntent.SetAmount(it)) },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Description
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.handleIntent(EnvelopeTransferIntent.SetDescription(it)) },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                // Error message
                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Save Button
                Button(
                    onClick = { viewModel.handleIntent(EnvelopeTransferIntent.SaveTransfer) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.fromCategoryId != null && state.toCategoryId != null && state.amount.toDoubleOrNull() != null
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Transfer")
                }
            }
        }
    }
}

