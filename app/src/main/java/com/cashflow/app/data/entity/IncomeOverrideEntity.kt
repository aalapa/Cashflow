package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "income_overrides",
    foreignKeys = [
        ForeignKey(
            entity = IncomeEntity::class,
            parentColumns = ["id"],
            childColumns = ["incomeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IncomeOverrideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val incomeId: Long,
    val date: LocalDate,
    val amount: Double
)

