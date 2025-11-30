package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.cashflow.app.data.model.RecurrenceType
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "budget_categories",
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class BudgetCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val budgetId: Long, // Required: every category belongs to a budget
    val name: String,
    val color: String, // Hex color code (e.g., "#FF5733")
    val icon: String? = null, // Icon name/resource identifier
    val budgetedAmount: Double, // Budgeted amount per period
    val periodType: RecurrenceType, // MONTHLY, BI_WEEKLY, etc.
    val accountId: Long? = null, // Optional: tie to specific account (null = global)
    val carryOverEnabled: Boolean = false, // Allow unused funds to roll over to next period
    val createdAt: LocalDateTime,
    val isActive: Boolean = true
)

