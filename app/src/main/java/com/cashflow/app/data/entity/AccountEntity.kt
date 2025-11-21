package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cashflow.app.data.model.AccountType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val startingBalance: Double,
    val currentBalance: Double
)

