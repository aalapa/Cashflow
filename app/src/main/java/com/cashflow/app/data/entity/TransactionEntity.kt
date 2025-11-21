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
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long, // Source account (from)
    val toAccountId: Long? = null, // Destination account (to) - only for TRANSFER type
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val timestamp: LocalDateTime,
    val description: String,
    val relatedBillId: Long? = null, // If this transaction is related to a bill
    val relatedIncomeId: Long? = null // If this transaction is related to income
)

