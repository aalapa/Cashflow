package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.BillPaymentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface BillPaymentDao {
    @Query("SELECT * FROM bill_payments WHERE billId = :billId AND paymentDate = :date")
    suspend fun getPayment(billId: Long, date: LocalDate): BillPaymentEntity?

    @Query("SELECT * FROM bill_payments WHERE billId = :billId")
    fun getPaymentsForBill(billId: Long): Flow<List<BillPaymentEntity>>

    @Query("SELECT * FROM bill_payments WHERE paymentDate = :date")
    fun getPaymentsForDate(date: LocalDate): Flow<List<BillPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: BillPaymentEntity): Long

    @Delete
    suspend fun deletePayment(payment: BillPaymentEntity)
    
    @Query("DELETE FROM bill_payments")
    suspend fun deleteAllPayments()
}

