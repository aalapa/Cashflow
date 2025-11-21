package com.cashflow.app.domain.model

import com.cashflow.app.data.model.RecurrenceType
import kotlinx.datetime.LocalDate

data class Income(
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val recurrenceType: RecurrenceType,
    val startDate: LocalDate,
    val accountId: Long,
    val isActive: Boolean = true
)

data class IncomeWithOverrides(
    val income: Income,
    val overrides: Map<LocalDate, Double> = emptyMap()
)

