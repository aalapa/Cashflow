package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "envelope_transfers",
    foreignKeys = [
        ForeignKey(
            entity = EnvelopeEntity::class,
            parentColumns = ["id"],
            childColumns = ["fromEnvelopeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EnvelopeEntity::class,
            parentColumns = ["id"],
            childColumns = ["toEnvelopeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EnvelopeTransferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromEnvelopeId: Long,
    val toEnvelopeId: Long,
    val amount: Double,
    val date: LocalDate,
    val description: String? = null,
    val timestamp: LocalDateTime
)
