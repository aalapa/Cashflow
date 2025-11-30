package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "budget_category_transfers",
    foreignKeys = [
        ForeignKey(
            entity = BudgetCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["fromCategoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BudgetCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["toCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetCategoryTransferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromCategoryId: Long,
    val toCategoryId: Long,
    val amount: Double,
    val date: LocalDate,
    val description: String? = null,
    val timestamp: LocalDateTime
)

