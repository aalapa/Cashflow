package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "envelope_allocations",
    foreignKeys = [
        ForeignKey(
            entity = EnvelopeEntity::class,
            parentColumns = ["id"],
            childColumns = ["envelopeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IncomeEntity::class,
            parentColumns = ["id"],
            childColumns = ["incomeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["envelopeId", "periodStart"], unique = true)]
)
data class EnvelopeAllocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val envelopeId: Long,
    val amount: Double,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val incomeId: Long? = null, // Which income source funded this allocation
    val createdAt: LocalDateTime
)
