package com.cashflow.app.domain.model

import com.cashflow.app.data.model.RecurrenceType
import kotlinx.datetime.LocalDate

data class Bill(
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val recurrenceType: RecurrenceType,
    val startDate: LocalDate,
    val endDate: LocalDate? = null, // Optional end date - bill stops recurring after this date
    val isActive: Boolean = true,
    val reminderDaysBefore: Int = 3
)

data class BillWithOverrides(
    val bill: Bill,
    val overrides: Map<LocalDate, Double> = emptyMap()
)

