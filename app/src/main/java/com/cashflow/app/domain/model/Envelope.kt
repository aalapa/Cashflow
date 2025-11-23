package com.cashflow.app.domain.model

import androidx.compose.ui.graphics.Color
import com.cashflow.app.data.model.RecurrenceType
import kotlinx.datetime.Instant

data class Envelope(
    val id: Long = 0,
    val name: String,
    val color: Color, // Converted from String hex
    val icon: String? = null,
    val budgetedAmount: Double,
    val periodType: RecurrenceType,
    val accountId: Long? = null,
    val carryOverEnabled: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Instant
)
