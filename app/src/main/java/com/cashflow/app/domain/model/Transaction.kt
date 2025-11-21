package com.cashflow.app.domain.model

import com.cashflow.app.data.model.TransactionType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val accountId: Long,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val timestamp: LocalDateTime,
    val description: String,
    val relatedBillId: Long? = null,
    val relatedIncomeId: Long? = null
)

