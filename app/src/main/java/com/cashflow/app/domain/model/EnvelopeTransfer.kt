package com.cashflow.app.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class EnvelopeTransfer(
    val id: Long = 0,
    val fromEnvelopeId: Long,
    val toEnvelopeId: Long,
    val amount: Double,
    val date: LocalDate,
    val description: String? = null,
    val timestamp: LocalDateTime
)
