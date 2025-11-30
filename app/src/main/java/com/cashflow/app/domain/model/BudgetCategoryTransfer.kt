package com.cashflow.app.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class BudgetCategoryTransfer(
    val id: Long = 0,
    val fromCategoryId: Long,
    val toCategoryId: Long,
    val amount: Double,
    val date: LocalDate,
    val description: String? = null,
    val timestamp: LocalDateTime
)

