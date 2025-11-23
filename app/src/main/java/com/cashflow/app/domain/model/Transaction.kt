package com.cashflow.app.domain.model

import com.cashflow.app.data.model.TransactionType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val accountId: Long, // Source account (from)
    val toAccountId: Long? = null, // Destination account (to) - only for TRANSFER type
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val timestamp: LocalDateTime,
    val description: String,
    val relatedBillId: Long? = null,
    val relatedIncomeId: Long? = null,
    val envelopeId: Long? = null // Link to an envelope for budgeting
)

