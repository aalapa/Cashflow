package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.cashflow.app.data.model.TransactionType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val timestamp: LocalDateTime,
    val description: String,
    val relatedBillId: Long? = null, // If this transaction is related to a bill
    val relatedIncomeId: Long? = null // If this transaction is related to income
)

