package com.cashflow.app.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.data.model.TransactionType
import com.cashflow.app.domain.model.Transaction
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.ui.timeline.formatCurrency
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TransactionsScreen(repository: CashFlowRepository) {
    val viewModel: TransactionsViewModel = viewModel { TransactionsViewModel(repository) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(TransactionsIntent.ShowAddDialog) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.transactions) { transaction ->
                    val category = state.categories.find { it.id == transaction.categoryId }
                    TransactionItem(
                        transaction = transaction,
                        category = category,
                        onEdit = { viewModel.handleIntent(TransactionsIntent.EditTransaction(transaction)) },
                        onDelete = { viewModel.handleIntent(TransactionsIntent.DeleteTransaction(transaction)) }
                    )
                }
            }
        }

        if (state.showAddDialog) {
            TransactionDialog(
                transaction = state.editingTransaction,
                accounts = state.accounts,
                categories = state.categories,
                onDismiss = { viewModel.handleIntent(TransactionsIntent.HideAddDialog) },
                onSave = { transaction ->
                    viewModel.handleIntent(TransactionsIntent.SaveTransaction(transaction))
                }
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    category: com.cashflow.app.domain.model.BudgetCategory? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    if (category != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = getIconForString(category.icon ?: "Folder"),
                            contentDescription = category.name,
                            tint = category.color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(transaction.amount),
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionDialog(
    transaction: Transaction?,
    accounts: List<com.cashflow.app.domain.model.Account>,
    categories: List<com.cashflow.app.domain.model.BudgetCategory> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "0.0") }
    var type by remember { mutableStateOf(transaction?.type ?: TransactionType.MANUAL_ADJUSTMENT) }
    var selectedAccountId by remember { mutableStateOf(transaction?.accountId ?: (accounts.firstOrNull()?.id ?: 0L)) }
    var selectedToAccountId by remember { mutableStateOf(transaction?.toAccountId ?: (accounts.getOrNull(1)?.id)) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(transaction?.categoryId) }
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val now = Clock.System.now().toLocalDateTime(timeZone)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (transaction == null) "Add Transaction" else "Edit Transaction") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = type.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TransactionType.values().forEach { transactionType ->
                            DropdownMenuItem(
                                text = { Text(transactionType.name.replace("_", " ")) },
                                onClick = {
                                    type = transactionType
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                var accountExpanded by remember { mutableStateOf(false) }
                val selectedAccount = accounts.find { it.id == selectedAccountId }
                ExposedDropdownMenuBox(
                    expanded = accountExpanded,
                    onExpandedChange = { accountExpanded = !accountExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAccount?.name ?: "Select Account",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (type == TransactionType.TRANSFER) "From Account" else "Account") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = accountExpanded,
                        onDismissRequest = { accountExpanded = false }
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                text = { Text(account.name) },
                                onClick = {
                                    selectedAccountId = account.id
                                    accountExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Show "To Account" dropdown only for TRANSFER type
                if (type == TransactionType.TRANSFER) {
                    var toAccountExpanded by remember { mutableStateOf(false) }
                    val selectedToAccount = accounts.find { it.id == selectedToAccountId }
                    ExposedDropdownMenuBox(
                        expanded = toAccountExpanded,
                        onExpandedChange = { toAccountExpanded = !toAccountExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedToAccount?.name ?: "Select Account",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("To Account") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toAccountExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = toAccountExpanded,
                            onDismissRequest = { toAccountExpanded = false }
                        ) {
                            accounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(account.name) },
                                    onClick = {
                                        selectedToAccountId = account.id
                                        toAccountExpanded = false
                                    },
                                    enabled = account.id != selectedAccountId // Can't transfer to same account
                                )
                            }
                        }
                    }
                }
                
                // Category picker (optional)
                if (categories.isNotEmpty()) {
                    var categoryExpanded by remember { mutableStateOf(false) }
                    val selectedCategory = categories.find { it.id == selectedCategoryId }
                    
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Select Category (Optional)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Budget Category") },
                            leadingIcon = {
                                selectedCategory?.let {
                                    Icon(
                                        imageVector = getIconForString(it.icon ?: "Folder"),
                                        contentDescription = null,
                                        tint = it.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    selectedCategoryId = null
                                    categoryExpanded = false
                                }
                            )
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        categoryExpanded = false
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
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    onSave(
                        Transaction(
                            id = transaction?.id ?: 0L,
                            accountId = selectedAccountId,
                            toAccountId = if (type == TransactionType.TRANSFER) selectedToAccountId else null,
                            type = type,
                            amount = amountValue,
                            date = transaction?.date ?: today,
                            timestamp = transaction?.timestamp ?: now,
                            description = description,
                            categoryId = selectedCategoryId
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

fun formatDate(date: kotlinx.datetime.LocalDate): String {
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
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

