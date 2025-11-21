package com.cashflow.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cashflow.app.data.model.RecurrenceType
import kotlinx.datetime.LocalDate

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val recurrenceType: RecurrenceType,
    val startDate: LocalDate,
    val endDate: LocalDate? = null, // Optional end date - bill stops recurring after this date
    val accountId: Long? = null, // Deprecated - kept for migration compatibility
    val isActive: Boolean = true,
    val reminderDaysBefore: Int = 3 // Days before due date to send reminder
)

