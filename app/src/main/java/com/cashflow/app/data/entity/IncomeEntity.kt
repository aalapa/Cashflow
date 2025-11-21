package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cashflow.app.data.model.RecurrenceType
import kotlinx.datetime.LocalDate

@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val recurrenceType: RecurrenceType,
    val startDate: LocalDate,
    val accountId: Long, // Which account receives the income
    val isActive: Boolean = true
)

