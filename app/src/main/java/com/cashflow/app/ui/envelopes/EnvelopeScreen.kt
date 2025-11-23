package com.cashflow.app.ui.envelopes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.data.model.RecurrenceType
import com.cashflow.app.domain.model.Envelope
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.ui.timeline.formatCurrency
import kotlinx.datetime.Clock

@Composable
fun EnvelopeScreen(
    repository: CashFlowRepository,
    onNavigateToAllocation: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToTransfers: () -> Unit = {},
    onNavigateToRules: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val viewModel: EnvelopeViewModel = viewModel { EnvelopeViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Envelopes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    FilledTonalButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Dashboard") },
                            onClick = {
                                showMenu = false
                                onNavigateToDashboard()
                            },
                            leadingIcon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Allocate") },
                            onClick = {
                                showMenu = false
                                onNavigateToAllocation()
                            },
                            leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Transfers") },
                            onClick = {
                                showMenu = false
                                onNavigateToTransfers()
                            },
                            leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Auto-Categorize") },
                            onClick = {
                                showMenu = false
                                onNavigateToRules()
                            },
                            leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Analytics") },
                            onClick = {
                                showMenu = false
                                onNavigateToAnalytics()
                            },
                            leadingIcon = { Icon(Icons.Default.QueryStats, contentDescription = null) }
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { viewModel.handleIntent(EnvelopeIntent.ShowAddDialog) },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Envelope",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.envelopes) { envelope ->
                    EnvelopeItem(
                        envelope = envelope,
                        onEdit = { viewModel.handleIntent(EnvelopeIntent.EditEnvelope(envelope)) },
                        onDelete = { viewModel.handleIntent(EnvelopeIntent.DeleteEnvelope(envelope)) }
                    )
                }
            }
        }

        if (state.showAddDialog) {
            EnvelopeDialog(
                envelope = state.editingEnvelope,
                onDismiss = { viewModel.handleIntent(EnvelopeIntent.HideAddDialog) },
                onSave = { envelope ->
                    viewModel.handleIntent(EnvelopeIntent.SaveEnvelope(envelope))
                },
                selectedColor = state.selectedColor,
                onColorSelected = { viewModel.handleIntent(EnvelopeIntent.SetSelectedColor(it)) },
                selectedIcon = state.selectedIcon,
                onIconSelected = { viewModel.handleIntent(EnvelopeIntent.SetSelectedIcon(it)) }
            )
        }
    }
}

@Composable
fun EnvelopeItem(
    envelope: Envelope,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = { expanded = !expanded }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (expanded) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = getIconForString(envelope.icon ?: "Folder"),
                        contentDescription = envelope.icon,
                        tint = envelope.color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = envelope.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Budgeted: ${formatCurrency(envelope.budgetedAmount)} / ${envelope.periodType.name.replace("_", " ").lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (envelope.carryOverEnabled) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Carry-over enabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Envelope", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Envelope", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopeDialog(
    envelope: Envelope?,
    onDismiss: () -> Unit,
    onSave: (Envelope) -> Unit,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    var name by remember { mutableStateOf(envelope?.name ?: "") }
    var budgetedAmount by remember { mutableStateOf(envelope?.budgetedAmount?.toString() ?: "0.0") }
    var periodType by remember { mutableStateOf(envelope?.periodType ?: RecurrenceType.MONTHLY) }
    var carryOverEnabled by remember { mutableStateOf(envelope?.carryOverEnabled ?: false) }
    var isActive by remember { mutableStateOf(envelope?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (envelope == null) "Add Envelope" else "Edit Envelope") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Envelope Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = budgetedAmount,
                    onValueChange = { budgetedAmount = it },
                    label = { Text("Budgeted Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                var periodExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = periodExpanded,
                    onExpandedChange = { periodExpanded = !periodExpanded }
                ) {
                    OutlinedTextField(
                        value = periodType.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Budget Period") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = periodExpanded,
                        onDismissRequest = { periodExpanded = false }
                    ) {
                        RecurrenceType.values().filter { it != RecurrenceType.CUSTOM }.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replace("_", " ")) },
                                onClick = {
                                    periodType = type
                                    periodExpanded = false
                                }
                            )
                        }
                    }
                }

                Text("Select Color", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(predefinedColors) { color ->
                        ColorDot(color = color, isSelected = color == selectedColor) {
                            onColorSelected(color)
                        }
                    }
                }

                Text("Select Icon", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(predefinedIcons) { iconName ->
                        IconOption(iconName = iconName, isSelected = iconName == selectedIcon) {
                            onIconSelected(iconName)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = carryOverEnabled,
                        onCheckedChange = { carryOverEnabled = it }
                    )
                    Text("Carry Over Unused Funds", style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text("Active", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = budgetedAmount.toDoubleOrNull() ?: 0.0
                    onSave(
                        Envelope(
                            id = envelope?.id ?: 0,
                            name = name,
                            color = selectedColor,
                            icon = selectedIcon,
                            budgetedAmount = amountValue,
                            periodType = periodType,
                            accountId = envelope?.accountId,
                            carryOverEnabled = carryOverEnabled,
                            isActive = isActive,
                            createdAt = envelope?.createdAt ?: Clock.System.now()
                        )
                    )
                }
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

@Composable
fun ColorDot(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color, CircleShape)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
    )
}

@Composable
fun IconOption(iconName: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconForString(iconName),
                contentDescription = iconName,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

