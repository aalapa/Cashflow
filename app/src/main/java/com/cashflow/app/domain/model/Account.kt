package com.cashflow.app.domain.model

import com.cashflow.app.data.model.AccountType

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val startingBalance: Double,
    val currentBalance: Double
)

