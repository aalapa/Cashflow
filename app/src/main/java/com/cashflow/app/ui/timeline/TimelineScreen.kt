package com.cashflow.app.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TimelineScreen(repository: CashFlowRepository) {
    val viewModel: TimelineViewModel = viewModel { TimelineViewModel(repository) }
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Time Period Selector
            TimePeriodSelector(
                selectedPeriod = state.selectedTimePeriod,
                onPeriodSelected = { period ->
                    viewModel.handleIntent(TimelineIntent.SetTimePeriod(period))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (state.error != null) {
            item {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (state.cashFlowDays.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No accounts or data yet.\nAdd an account to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Chart View as Accordion
            if (state.cashFlowDays.isNotEmpty()) {
                item {
                    var expanded by remember { mutableStateOf(false) }
                    var showBarChart by remember { mutableStateOf(true) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cash Flow Chart",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (expanded) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            FilterChip(
                                                selected = showBarChart,
                                                onClick = { showBarChart = true },
                                                label = { Text("Bar", style = MaterialTheme.typography.labelSmall) }
                                            )
                                            FilterChip(
                                                selected = !showBarChart,
                                                onClick = { showBarChart = false },
                                                label = { Text("Line", style = MaterialTheme.typography.labelSmall) }
                                            )
                                        }
                                    }
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = if (expanded) "Collapse" else "Expand"
                                        )
                                    }
                                }
                            }
                            
                            if (expanded) {
                                Spacer(modifier = Modifier.height(12.dp))
                                CashFlowChart(
                                    cashFlowDays = state.cashFlowDays,
                                    showBarChart = showBarChart,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Calendar content - group by month and display
            val daysByMonth = state.cashFlowDays.groupBy { 
                "${it.date.year}-${it.date.monthNumber.toString().padStart(2, '0')}"
            }.toSortedMap() // Sort months chronologically
            
            daysByMonth.forEach { (monthKey, days) ->
                item {
                    CalendarMonthView(
                        monthKey = monthKey,
                        days = days,
                        modifier = Modifier.fillMaxWidth(),
                        onDayClick = { day ->
                            viewModel.handleIntent(TimelineIntent.ShowDayDetail(day))
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    
    // Day Detail Dialog
    if (state.showDayDetailDialog) {
        val selectedDay = state.selectedDay
        if (selectedDay != null) {
            DayDetailDialog(
                selectedDay = selectedDay,
                previousMonthDay = state.selectedDayPreviousMonth,
                lastYearDay = state.selectedDayLastYear,
                onDismiss = { viewModel.handleIntent(TimelineIntent.HideDayDetail) }
            )
        }
    }
}

@Composable
fun DayDetailDialog(
    selectedDay: com.cashflow.app.domain.model.CashFlowDay,
    previousMonthDay: com.cashflow.app.domain.model.CashFlowDay?,
    lastYearDay: com.cashflow.app.domain.model.CashFlowDay?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = formatDateLong(selectedDay.date),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Current Day Balance
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            selectedDay.isNegative -> Color(0xFFEF4444).copy(alpha = 0.1f)
                            selectedDay.isWarning -> Color(0xFFFFB020).copy(alpha = 0.1f)
                            else -> Color(0xFF10B981).copy(alpha = 0.1f)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("$%.2f", selectedDay.balance),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                selectedDay.isNegative -> Color(0xFFEF4444)
                                selectedDay.isWarning -> Color(0xFFFFB020)
                                else -> Color(0xFF10B981)
                            }
                        )
                    }
                }
                
                Divider()
                
                // Historical Comparison
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Historical Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    // Previous Month
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Same date last month:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (previousMonthDay != null) {
                                // Show arrow if comparison is possible
                                val isHigher = selectedDay.balance > previousMonthDay.balance
                                Icon(
                                    imageVector = if (isHigher) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = if (isHigher) "Higher" else "Lower",
                                    tint = if (isHigher) Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = if (previousMonthDay != null) {
                                    String.format("$%.2f", previousMonthDay.balance)
                                } else {
                                    "N/A"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (previousMonthDay != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                    
                    // Last Year
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Same date last year:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (lastYearDay != null) {
                                // Show arrow if comparison is possible
                                val isHigher = selectedDay.balance > lastYearDay.balance
                                Icon(
                                    imageVector = if (isHigher) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = if (isHigher) "Higher" else "Lower",
                                    tint = if (isHigher) Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = if (lastYearDay != null) {
                                    String.format("$%.2f", lastYearDay.balance)
                                } else {
                                    "N/A"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (lastYearDay != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun formatDateLong(date: kotlinx.datetime.LocalDate): String {
    val monthName = when (date.monthNumber) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> ""
    }
    val daySuffix = when (date.dayOfMonth) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
    return "$monthName ${date.dayOfMonth}$daySuffix, ${date.year}"
}

@Composable
fun TimePeriodSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimePeriod.values().forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { 
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${period.days}D",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun TimelineContent(cashFlowDays: List<com.cashflow.app.domain.model.CashFlowDay>) {
    if (cashFlowDays.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data to display",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // Group days by month
    val daysByMonth = cashFlowDays.groupBy { 
        "${it.date.year}-${it.date.monthNumber.toString().padStart(2, '0')}"
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        daysByMonth.forEach { (monthKey, days) ->
            item {
                CalendarMonthView(
                    monthKey = monthKey,
                    days = days,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CalendarMonthView(
    monthKey: String,
    days: List<com.cashflow.app.domain.model.CashFlowDay>,
    modifier: Modifier = Modifier,
    onDayClick: (com.cashflow.app.domain.model.CashFlowDay) -> Unit = {}
) {
    // Get the first day of the month from the first day in the list
    val firstDataDay = days.first().date
    val year = firstDataDay.year
    val month = firstDataDay.month
    
    // Create the first day of the month
    val firstDayOfMonth = try {
        kotlinx.datetime.LocalDate(year, month, 1)
    } catch (e: Exception) {
        firstDataDay
    }
    
    val monthName = when (firstDayOfMonth.monthNumber) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> ""
    }

    Column(modifier = modifier) {
        // Month header
        Text(
            text = "$monthName ${firstDayOfMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { dayName ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid - create a map of all days in the month
        val daysMap = days.associateBy { it.date.dayOfMonth }
        val firstDayOfWeek = getDayOfWeek(firstDayOfMonth)
        val daysInMonth = getDaysInMonth(firstDayOfMonth.year, firstDayOfMonth.monthNumber)
        
        // Create calendar grid (6 weeks max)
        val weeks = mutableListOf<List<Int?>>()
        var currentWeek = mutableListOf<Int?>()
        
        // Add empty cells for days before the first day of the month
        repeat(firstDayOfWeek) {
            currentWeek.add(null)
        }
        
        // Add all days of the month
        for (day in 1..daysInMonth) {
            currentWeek.add(day)
            if (currentWeek.size == 7) {
                weeks.add(currentWeek)
                currentWeek = mutableListOf()
            }
        }
        
        // Fill remaining week if needed
        while (currentWeek.size < 7 && currentWeek.isNotEmpty()) {
            currentWeek.add(null)
        }
        if (currentWeek.isNotEmpty()) {
            weeks.add(currentWeek)
        }

        // Render calendar grid
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { dayNum ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                    ) {
                        if (dayNum != null) {
                            val dayData = daysMap[dayNum]
                            val dayDate = kotlinx.datetime.LocalDate(year, month, dayNum)
                            val timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
                            val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(timeZone).date
                            val isPastDate = dayDate < today
                            
                            if (dayData != null) {
                                // Day has data - show it with balance
                                CalendarDayCell(
                                    day = dayNum,
                                    balance = dayData.balance,
                                    isNegative = dayData.isNegative,
                                    isWarning = dayData.isWarning,
                                    hasUnpaidBills = dayData.bills.isNotEmpty(),
                                    isPastDate = isPastDate,
                                    onClick = { onDayClick(dayData) }
                                )
                            } else if (isPastDate) {
                                // Past day without data - show it grayed out but still clickable
                                // Create a placeholder CashFlowDay for display
                                val placeholderDay = com.cashflow.app.domain.model.CashFlowDay(
                                    date = dayDate,
                                    balance = 0.0,
                                    isNegative = false,
                                    isWarning = false
                                )
                                CalendarDayCell(
                                    day = dayNum,
                                    balance = 0.0,
                                    isNegative = false,
                                    isWarning = false,
                                    hasUnpaidBills = false,
                                    isPastDate = true,
                                    onClick = { onDayClick(placeholderDay) }
                                )
                            }
                            // Future days without data are not shown (they'll be calculated when we get there)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    balance: Double,
    isNegative: Boolean,
    isWarning: Boolean,
    hasUnpaidBills: Boolean,
    isPastDate: Boolean = false,
    onClick: () -> Unit = {}
) {
    // If there are unpaid bills and balance is positive, use light green background with red text
    val hasUnpaidBillsAndPositive = hasUnpaidBills && balance > 0 && !isNegative && !isWarning
    
    val backgroundColor = when {
        isPastDate -> Color(0xFF9E9E9E).copy(alpha = 0.3f) // Gray for past dates
        isNegative -> Color(0xFFEF4444) // Red
        isWarning -> Color(0xFFFFB020) // Amber
        hasUnpaidBillsAndPositive -> Color(0xFF10B981).copy(alpha = 0.3f) // Light green
        balance > 0 -> Color(0xFF10B981) // Green
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isPastDate -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) // Gray text for past dates
        hasUnpaidBillsAndPositive -> Color(0xFFEF4444) // Red text for unpaid bills
        isNegative || isWarning || balance > 0 -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Date with alert icon overlay (if unpaid bills)
                if (hasUnpaidBillsAndPositive) {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Unpaid bills",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                } else {
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatCurrencyCompact(balance),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = textColor
                )
            }
        }
    }
}

fun getDayOfWeek(date: kotlinx.datetime.LocalDate): Int {
    // kotlinx.datetime doesn't have a direct dayOfWeek, so we'll calculate it
    // Using Zeller's congruence or a simpler approach
    val javaDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
    return javaDate.dayOfWeek.value % 7 // Convert to 0-6 (Sunday = 0)
}

fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

fun formatCurrencyCompact(amount: Double): String {
    return if (amount < 0) {
        "-$${String.format("%.0f", -amount)}"
    } else {
        "$${String.format("%.0f", amount)}"
    }
}

@Composable
fun CashFlowDayItem(day: com.cashflow.app.domain.model.CashFlowDay) {
    val borderColor = when {
        day.isNegative -> Color(0xFFEF4444)
        day.isWarning -> Color(0xFFFFB020)
        else -> Color.Transparent
    }
    
    val backgroundColor = when {
        day.isNegative -> Color(0xFFFFF5F5) // Very light red
        day.isWarning -> Color(0xFFFFFBF0) // Very light amber
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (day.isNegative || day.isWarning) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = if (borderColor != Color.Transparent) {
            androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        } else {
            null
        }
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
                Column {
                    Text(
                        text = formatDate(day.date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (day.isNegative || day.isWarning) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (day.isNegative) "⚠️ Negative Balance" else "⚠️ Low Balance",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (day.isNegative) Color(0xFFEF4444) else Color(0xFFFFB020)
                        )
                    }
                }
                Text(
                    text = formatCurrency(day.balance),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        day.isNegative -> Color(0xFFEF4444)
                        day.isWarning -> Color(0xFFFFB020)
                        else -> Color(0xFF10B981)
                    }
                )
            }

            if (day.income.isNotEmpty() || day.bills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                day.income.forEach { income ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "↑",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = income.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = formatCurrency(income.amount),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                day.bills.forEach { bill ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "↓",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFEF4444)
                            )
                            Text(
                                text = bill.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = formatCurrency(bill.amount),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}

fun formatDate(date: kotlinx.datetime.LocalDate): String {
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
}

fun formatCurrency(amount: Double): String {
    return if (amount < 0) {
        "-$${String.format("%.2f", -amount)}"
    } else {
        "$${String.format("%.2f", amount)}"
    }
}

