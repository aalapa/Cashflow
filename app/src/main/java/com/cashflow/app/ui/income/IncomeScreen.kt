package com.cashflow.app.ui.income

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.data.model.RecurrenceType
import com.cashflow.app.domain.model.Income
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.domain.model.IncomeOccurrence
import com.cashflow.app.ui.timeline.formatCurrency
import com.cashflow.app.ui.timeline.formatDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun IncomeScreen(repository: CashFlowRepository) {
    val viewModel: IncomeViewModel = viewModel { IncomeViewModel(repository) }
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
                text = "Income",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FloatingActionButton(
                onClick = { viewModel.handleIntent(IncomeIntent.ShowAddDialog) },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Income",
                    modifier = Modifier.size(24.dp)
                )
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
                items(state.incomeList) { income ->
                    val occurrences = state.incomeOccurrences[income.id] ?: emptyList()
                    IncomeItem(
                        income = income,
                        occurrences = occurrences,
                        onEdit = { viewModel.handleIntent(IncomeIntent.EditIncome(income)) },
                        onDelete = { viewModel.handleIntent(IncomeIntent.DeleteIncome(income)) },
                        onEditAmount = { occurrence ->
                            viewModel.handleIntent(IncomeIntent.ShowEditAmountDialog(occurrence))
                        },
                        onMarkReceived = { occurrence ->
                            viewModel.handleIntent(IncomeIntent.ShowReceivedDialog(occurrence))
                        }
                    )
                }
            }
        }

        if (state.showAddDialog) {
            IncomeDialog(
                income = state.editingIncome,
                accounts = state.accounts,
                onDismiss = { viewModel.handleIntent(IncomeIntent.HideAddDialog) },
                onSave = { income ->
                    viewModel.handleIntent(IncomeIntent.SaveIncome(income))
                }
            )
        }

        val incomeToEditAmount = state.incomeToEditAmount
        if (state.showEditAmountDialog && incomeToEditAmount != null) {
            EditIncomeAmountDialog(
                occurrence = incomeToEditAmount,
                onDismiss = { viewModel.handleIntent(IncomeIntent.HideEditAmountDialog) },
                onSave = { newAmount ->
                    viewModel.handleIntent(IncomeIntent.EditIncomeAmount(incomeToEditAmount, newAmount))
                }
            )
        }

        val incomeToMarkReceived = state.incomeToMarkReceived
        if (state.showReceivedDialog && incomeToMarkReceived != null) {
            MarkReceivedDialog(
                occurrence = incomeToMarkReceived,
                accounts = state.accounts,
                onDismiss = { viewModel.handleIntent(IncomeIntent.HideReceivedDialog) },
                onMarkReceived = { accountId ->
                    viewModel.handleIntent(IncomeIntent.MarkIncomeAsReceived(incomeToMarkReceived, accountId))
                }
            )
        }
    }
}

@Composable
fun IncomeItem(
    income: Income,
    occurrences: List<IncomeOccurrence>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEditAmount: (IncomeOccurrence) -> Unit,
    onMarkReceived: (IncomeOccurrence) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = income.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = income.recurrenceType.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatCurrency(income.amount),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Show future occurrences
            if (occurrences.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Upcoming Income",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val topOccurrences = occurrences.take(6)
                for (index in topOccurrences.indices) {
                    val occurrence = topOccurrences[index]
                    IncomeOccurrenceItem(
                        occurrence = occurrence,
                        onEditAmount = { onEditAmount(occurrence) },
                        onMarkReceived = { onMarkReceived(occurrence) }
                    )
                    if (index < topOccurrences.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (occurrences.size > 6) {
                    Text(
                        text = "... and ${occurrences.size - 6} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeOccurrenceItem(
    occurrence: IncomeOccurrence,
    onEditAmount: () -> Unit,
    onMarkReceived: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatDate(occurrence.date),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatCurrency(occurrence.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF10B981)
            )
            IconButton(
                onClick = onMarkReceived,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Mark as Received",
                    tint = Color(0xFF10B981)
                )
            }
            IconButton(
                onClick = onEditAmount,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Amount",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MarkReceivedDialog(
    occurrence: IncomeOccurrence,
    accounts: List<com.cashflow.app.domain.model.Account>,
    onDismiss: () -> Unit,
    onMarkReceived: (Long) -> Unit
) {
    var selectedAccountId by remember { 
        mutableStateOf(accounts.firstOrNull()?.id ?: 0L) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mark Income as Received") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "${occurrence.income.name} - ${formatDate(occurrence.date)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Amount: ${formatCurrency(occurrence.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
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
                        label = { Text("Deposit To Account") },
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onMarkReceived(selectedAccountId) }
            ) {
                Text("Mark Received")
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
fun EditIncomeAmountDialog(
    occurrence: IncomeOccurrence,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { 
        mutableStateOf(occurrence.amount.toString()) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Income Amount") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "${occurrence.income.name} - ${formatDate(occurrence.date)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: occurrence.amount
                    onSave(amount)
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
fun IncomeDialog(
    income: Income?,
    accounts: List<com.cashflow.app.domain.model.Account>,
    onDismiss: () -> Unit,
    onSave: (Income) -> Unit
) {
    var name by remember { mutableStateOf(income?.name ?: "") }
    var amount by remember { mutableStateOf(income?.amount?.toString() ?: "0.0") }
    var recurrenceType by remember { mutableStateOf(income?.recurrenceType ?: RecurrenceType.BI_WEEKLY) }
    var selectedAccountId by remember { mutableStateOf(income?.accountId ?: (accounts.firstOrNull()?.id ?: 0L)) }
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    var startDate by remember { mutableStateOf(income?.startDate ?: today) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (income == null) "Add Income" else "Edit Income") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Income Name") },
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
                        value = recurrenceType.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Recurrence") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        RecurrenceType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replace("_", " ")) },
                                onClick = {
                                    recurrenceType = type
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
                        label = { Text("Account") },
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
                // Start Date Picker
                OutlinedTextField(
                    value = formatDate(startDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    onSave(
                        Income(
                            id = income?.id ?: 0L,
                            name = name,
                            amount = amountValue,
                            recurrenceType = recurrenceType,
                            startDate = startDate,
                            accountId = selectedAccountId,
                            isActive = true
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
    
    if (showDatePicker) {
        IncomeDatePickerDialog(
            onDateSelected = { date ->
                startDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = startDate
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    initialDate: LocalDate
) {
    // Convert LocalDate to milliseconds since epoch (using UTC to avoid timezone issues)
    val initialMillis = try {
        java.time.LocalDate.of(
            initialDate.year,
            initialDate.monthNumber,
            initialDate.dayOfMonth
        ).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // DatePicker returns milliseconds at midnight UTC, so we use UTC for conversion
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val localDate = instant.atZone(java.time.ZoneOffset.UTC).toLocalDate()
                        // Convert to kotlinx.datetime.LocalDate using string format
                        val dateString = "${localDate.year}-${localDate.monthValue.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                        val selectedDate = kotlinx.datetime.LocalDate.parse(dateString)
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun formatDate(date: LocalDate): String {
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
}

