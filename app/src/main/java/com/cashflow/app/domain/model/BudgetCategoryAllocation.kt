package com.cashflow.app.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class BudgetCategoryAllocation(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val incomeId: Long? = null,
    val createdAt: Instant
)

