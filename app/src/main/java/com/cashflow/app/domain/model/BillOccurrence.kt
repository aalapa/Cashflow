package com.cashflow.app.domain.model

import kotlinx.datetime.LocalDate

data class BillOccurrence(
    val bill: Bill,
    val dueDate: LocalDate,
    val amount: Double,
    val isPaid: Boolean = false,
    val paymentDate: LocalDate? = null,
    val paidFromAccountId: Long? = null
)

