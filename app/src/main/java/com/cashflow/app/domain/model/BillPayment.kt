package com.cashflow.app.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class BillPayment(
    val id: Long = 0,
    val billId: Long,
    val accountId: Long,
    val paymentDate: LocalDate,
    val amount: Double,
    val timestamp: LocalDateTime,
    val transactionId: Long? = null
)

