package com.cashflow.app.ui.bills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
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
import com.cashflow.app.domain.model.Bill
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.domain.model.BillOccurrence
import com.cashflow.app.ui.timeline.formatCurrency
import com.cashflow.app.ui.timeline.formatDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun BillsScreen(repository: CashFlowRepository) {
    val viewModel: BillsViewModel = viewModel { BillsViewModel(repository) }
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
                text = "Bills",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FloatingActionButton(
                onClick = { viewModel.handleIntent(BillsIntent.ShowAddDialog) },
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // View mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.viewMode == BillsViewMode.DEFAULT,
                onClick = { viewModel.handleIntent(BillsIntent.SetViewMode(BillsViewMode.DEFAULT)) },
                label = { Text("Default", style = MaterialTheme.typography.labelSmall) }
            )
            FilterChip(
                selected = state.viewMode == BillsViewMode.DATE_SORTED,
                onClick = { viewModel.handleIntent(BillsIntent.SetViewMode(BillsViewMode.DATE_SORTED)) },
                label = { Text("By Date", style = MaterialTheme.typography.labelSmall) }
            )
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
            when (state.viewMode) {
                BillsViewMode.DEFAULT -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.bills) { bill ->
                            val occurrences = state.billOccurrences[bill.id] ?: emptyList()
                            BillItem(
                                bill = bill,
                                occurrences = occurrences,
                                onEdit = { viewModel.handleIntent(BillsIntent.EditBill(bill)) },
                                onDelete = { viewModel.handleIntent(BillsIntent.DeleteBill(bill)) },
                                onMarkPaid = { occurrence ->
                                    viewModel.handleIntent(BillsIntent.ShowMarkPaidDialog(occurrence))
                                }
                            )
                        }
                    }
                }
                BillsViewMode.DATE_SORTED -> {
                    DateSortedBillsView(
                        billOccurrences = state.billOccurrences,
                        bills = state.bills,
                        onEdit = { bill -> viewModel.handleIntent(BillsIntent.EditBill(bill)) },
                        onDelete = { bill -> viewModel.handleIntent(BillsIntent.DeleteBill(bill)) },
                        onMarkPaid = { occurrence ->
                            viewModel.handleIntent(BillsIntent.ShowMarkPaidDialog(occurrence))
                        }
                    )
                }
            }
        }

        if (state.showAddDialog) {
            BillDialog(
                bill = state.editingBill,
                onDismiss = { viewModel.handleIntent(BillsIntent.HideAddDialog) },
                onSave = { bill ->
                    viewModel.handleIntent(BillsIntent.SaveBill(bill))
                }
            )
        }

        val billToMarkPaid = state.billToMarkPaid
        if (state.showMarkPaidDialog && billToMarkPaid != null) {
            MarkPaidDialog(
                occurrence = billToMarkPaid,
                accounts = state.accounts,
                onDismiss = { viewModel.handleIntent(BillsIntent.HideMarkPaidDialog) },
                onMarkPaid = { accountId ->
                    viewModel.handleIntent(BillsIntent.MarkBillAsPaid(billToMarkPaid, accountId))
                }
            )
        }
    }
}

@Composable
fun BillItem(
    bill: Bill,
    occurrences: List<BillOccurrence>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkPaid: (BillOccurrence) -> Unit
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
                        text = bill.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bill.recurrenceType.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatCurrency(bill.amount),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
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
                    text = "Upcoming Due Dates",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                occurrences.take(6).forEach { occurrence ->
                    BillOccurrenceItem(
                        occurrence = occurrence,
                        onMarkPaid = { onMarkPaid(occurrence) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
fun BillOccurrenceItem(
    occurrence: BillOccurrence,
    onMarkPaid: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatDate(occurrence.dueDate),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (occurrence.isPaid) FontWeight.Normal else FontWeight.SemiBold,
                color = if (occurrence.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            if (occurrence.isPaid) {
                Text(
                    text = "✓ Paid",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF10B981)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatCurrency(occurrence.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (occurrence.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFEF4444)
            )
            if (!occurrence.isPaid) {
                Button(
                    onClick = onMarkPaid,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Paid", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun MarkPaidDialog(
    occurrence: BillOccurrence,
    accounts: List<com.cashflow.app.domain.model.Account>,
    onDismiss: () -> Unit,
    onMarkPaid: (Long) -> Unit
) {
    var selectedAccountId by remember { 
        mutableStateOf(accounts.firstOrNull()?.id ?: 0L) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mark Bill as Paid") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "${occurrence.bill.name} - ${formatDate(occurrence.dueDate)}",
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
                        label = { Text("Pay From Account") },
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
                onClick = { onMarkPaid(selectedAccountId) }
            ) {
                Text("Paid")
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
fun BillDialog(
    bill: Bill?,
    onDismiss: () -> Unit,
    onSave: (Bill) -> Unit
) {
    var name by remember { mutableStateOf(bill?.name ?: "") }
    var amount by remember { mutableStateOf(bill?.amount?.toString() ?: "0.0") }
    var recurrenceType by remember { mutableStateOf(bill?.recurrenceType ?: RecurrenceType.MONTHLY) }
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    var startDate by remember { mutableStateOf(bill?.startDate ?: today) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var hasEndDate by remember { mutableStateOf(bill?.endDate != null) }
    var endDate by remember { mutableStateOf(bill?.endDate) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var dueDayOfMonth by remember { 
        mutableStateOf(bill?.startDate?.dayOfMonth ?: 1) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (bill == null) "Add Bill" else "Edit Bill") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Bill Name") },
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
                // Start Date Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = formatDate(startDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Start Date") },
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Select Start Date"
                                )
                            }
                        }
                    )
                }
                
                // Due Day of Month (for monthly bills) - updates start date day
                if (recurrenceType == RecurrenceType.MONTHLY) {
                    var dayExpanded by remember { mutableStateOf(false) }
                    val daysInMonth = (1..31).toList()
                    ExposedDropdownMenuBox(
                        expanded = dayExpanded,
                        onExpandedChange = { dayExpanded = !dayExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${dueDayOfMonth}${getDaySuffix(dueDayOfMonth)}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Due Day of Month") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = dayExpanded,
                            onDismissRequest = { dayExpanded = false }
                        ) {
                            daysInMonth.forEach { day ->
                                DropdownMenuItem(
                                    text = { Text("${day}${getDaySuffix(day)}") },
                                    onClick = {
                                        dueDayOfMonth = day
                                        // Update start date with new day of month
                                        try {
                                            startDate = kotlinx.datetime.LocalDate(startDate.year, startDate.month, day)
                                        } catch (e: Exception) {
                                            // If day doesn't exist, use last day of month
                                            var lastDay = 28
                                            for (d in 28..31) {
                                                try {
                                                    kotlinx.datetime.LocalDate(startDate.year, startDate.month, d)
                                                    lastDay = d
                                                } catch (ex: Exception) {
                                                    break
                                                }
                                            }
                                            startDate = kotlinx.datetime.LocalDate(startDate.year, startDate.month, minOf(day, lastDay))
                                        }
                                        dayExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // End Date (Optional)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = hasEndDate,
                            onCheckedChange = { 
                                hasEndDate = it
                                if (!it) {
                                    endDate = null
                                } else if (endDate == null) {
                                    // Set default end date to 1 year from start date
                                    endDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + 365)
                                }
                            }
                        )
                        Text("Has End Date")
                    }
                    if (hasEndDate) {
                        val currentEndDate = endDate
                        if (currentEndDate != null) {
                            OutlinedTextField(
                                value = formatDate(currentEndDate),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("End Date") },
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    IconButton(onClick = { showEndDatePicker = true }) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = "Select End Date"
                                        )
                                    }
                                }
                            )
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
                        Bill(
                            id = bill?.id ?: 0L,
                            name = name,
                            amount = amountValue,
                            recurrenceType = recurrenceType,
                            startDate = startDate,
                            endDate = if (hasEndDate) endDate else null,
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
    
    // Start Date Picker Dialog
    if (showStartDatePicker) {
        BillDatePickerDialog(
            onDateSelected = { selectedDate ->
                startDate = selectedDate
                // Update due day of month if monthly
                if (recurrenceType == RecurrenceType.MONTHLY) {
                    dueDayOfMonth = selectedDate.dayOfMonth
                }
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false },
            initialDate = startDate
        )
    }
    
    // End Date Picker Dialog
    if (showEndDatePicker) {
        val currentEndDate = endDate
        if (currentEndDate != null) {
            BillDatePickerDialog(
                onDateSelected = { selectedDate ->
                    endDate = selectedDate
                    showEndDatePicker = false
                },
                onDismiss = { showEndDatePicker = false },
                initialDate = currentEndDate
            )
        }
    }
}

@Composable
fun BillDatePickerDialog(
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

@Composable
fun DateSortedBillsView(
    billOccurrences: Map<Long, List<BillOccurrence>>,
    bills: List<Bill>,
    onEdit: (Bill) -> Unit,
    onDelete: (Bill) -> Unit,
    onMarkPaid: (BillOccurrence) -> Unit
) {
    // Collect all occurrences and sort by date
    val allOccurrences = billOccurrences.values.flatten()
        .sortedBy { it.dueDate }
    
    // Create a map of bill ID to bill for quick lookup
    val billsMap = bills.associateBy { it.id }
    
    if (allOccurrences.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No upcoming bills",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(allOccurrences) { occurrence ->
                val bill = billsMap[occurrence.bill.id]
                if (bill != null) {
                    DateSortedBillOccurrenceItem(
                        occurrence = occurrence,
                        bill = bill,
                        onEdit = { onEdit(bill) },
                        onDelete = { onDelete(bill) },
                        onMarkPaid = { onMarkPaid(occurrence) }
                    )
                }
            }
        }
    }
}

@Composable
fun DateSortedBillOccurrenceItem(
    occurrence: BillOccurrence,
    bill: Bill,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkPaid: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    text = bill.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(occurrence.dueDate),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (occurrence.isPaid) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (occurrence.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    if (occurrence.isPaid) {
                        Text(
                            text = "• ✓ Paid",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatCurrency(occurrence.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (occurrence.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFEF4444)
                    )
                    if (!occurrence.isPaid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                            Button(
                                onClick = onMarkPaid,
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Paid", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getDaySuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}

