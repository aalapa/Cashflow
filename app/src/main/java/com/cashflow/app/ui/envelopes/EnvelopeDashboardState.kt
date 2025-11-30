package com.cashflow.app.ui.envelopes

import com.cashflow.app.domain.model.BudgetCategory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class EnvelopeBalance(
    val category: BudgetCategory,
    val allocated: Double,
    val spent: Double,
    val balance: Double,
    val periodStart: LocalDate,
    val periodEnd: LocalDate
)

data class EnvelopeDashboardState(
    val categories: List<BudgetCategory> = emptyList(),
    val balances: Map<Long, EnvelopeBalance> = emptyMap(),
    val selectedDate: LocalDate = kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EnvelopeDashboardIntent {
    object LoadDashboard : EnvelopeDashboardIntent()
    data class SetDate(val date: LocalDate) : EnvelopeDashboardIntent()
}
