package com.cashflow.app.ui.allocation

import androidx.compose.foundation.clickable
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
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.ui.timeline.formatCurrency
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationScreen(
    repository: CashFlowRepository,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: AllocationViewModel = viewModel { AllocationViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Allocate Income") },
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
                Text(
                    text = "Upcoming Income",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (state.incomeOccurrences.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No upcoming income",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.incomeOccurrences) { occurrence ->
                            IncomeOccurrenceCard(
                                occurrence = occurrence,
                                onClick = { viewModel.handleIntent(AllocationIntent.SelectIncome(occurrence)) }
                            )
                        }
                    }
                }
            }
        }
        
        // Allocation Dialog
        val selectedOccurrence = state.selectedIncomeOccurrence
        if (state.showAllocationDialog && selectedOccurrence != null) {
            AllocationDialog(
                occurrence = selectedOccurrence,
                categories = state.categories,
                allocations = state.allocations,
                onAllocationChange = { categoryId, amount ->
                    viewModel.handleIntent(AllocationIntent.SetAllocation(categoryId, amount))
                },
                onSave = {
                    viewModel.handleIntent(AllocationIntent.SaveAllocations)
                },
                onDismiss = {
                    viewModel.handleIntent(AllocationIntent.HideAllocationDialog)
                }
            )
        }
    }
}

@Composable
fun IncomeOccurrenceCard(
    occurrence: com.cashflow.app.domain.model.IncomeOccurrence,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = occurrence.income.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = occurrence.date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCurrency(occurrence.amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationDialog(
    occurrence: com.cashflow.app.domain.model.IncomeOccurrence,
    categories: List<com.cashflow.app.domain.model.BudgetCategory>,
    allocations: Map<Long, Double>,
    onAllocationChange: (Long, Double) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val totalAllocated = allocations.values.sum()
    val remaining = occurrence.amount - totalAllocated
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Allocate ${formatCurrency(occurrence.amount)}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${occurrence.income.name} - ${occurrence.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                categories.forEach { category ->
                    var amountText by remember { mutableStateOf(allocations[category.id]?.toString() ?: "") }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getIconForString(category.icon ?: "Folder"),
                            contentDescription = null,
                            tint = category.color,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = {
                                amountText = it
                                val amount = it.toDoubleOrNull() ?: 0.0
                                onAllocationChange(category.id, amount)
                            },
                            modifier = Modifier.width(100.dp),
                            label = { Text("$") },
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Allocated:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatCurrency(totalAllocated),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (totalAllocated > occurrence.amount) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Remaining:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatCurrency(remaining),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = totalAllocated <= occurrence.amount && totalAllocated > 0
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

fun getIconForString(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "Folder" -> Icons.Default.Folder
        "Fastfood" -> Icons.Default.Fastfood
        "Home" -> Icons.Default.Home
        "Car" -> Icons.Default.DirectionsCar
        "School" -> Icons.Default.School
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "LocalGasStation" -> Icons.Default.LocalGasStation
        "Movie" -> Icons.Default.Movie
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "HealthAndSafety" -> Icons.Default.HealthAndSafety
        else -> Icons.Default.Folder
    }
}
