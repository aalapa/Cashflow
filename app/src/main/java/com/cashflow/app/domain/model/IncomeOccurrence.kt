package com.cashflow.app.domain.model

import kotlinx.datetime.LocalDate

data class IncomeOccurrence(
    val income: Income,
    val date: LocalDate,
    val amount: Double
)

