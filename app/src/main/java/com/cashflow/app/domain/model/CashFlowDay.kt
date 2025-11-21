package com.cashflow.app.domain.model

import kotlinx.datetime.LocalDate

data class CashFlowDay(
    val date: LocalDate,
    val balance: Double,
    val isNegative: Boolean,
    val isWarning: Boolean, // Close to negative (e.g., < 100)
    val income: List<IncomeEvent> = emptyList(),
    val bills: List<BillEvent> = emptyList(),
    val transactions: List<Transaction> = emptyList()
)

data class IncomeEvent(
    val incomeId: Long,
    val name: String,
    val amount: Double,
    val accountId: Long
)

data class BillEvent(
    val billId: Long,
    val name: String,
    val amount: Double
)

