package com.cashflow.app.domain.model

import kotlinx.datetime.Instant

data class Budget(
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Instant,
    val isActive: Boolean = true
)

