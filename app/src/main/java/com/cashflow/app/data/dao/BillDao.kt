package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.BillEntity
import com.cashflow.app.data.entity.BillOverrideEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface BillDao {
    @Query("SELECT * FROM bills WHERE isActive = 1 ORDER BY startDate")
    fun getAllActiveBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills ORDER BY startDate")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getBillById(id: Long): BillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    // Bill Overrides
    @Query("SELECT * FROM bill_overrides WHERE billId = :billId AND date = :date")
    suspend fun getOverride(billId: Long, date: LocalDate): BillOverrideEntity?

    @Query("SELECT * FROM bill_overrides WHERE billId = :billId")
    fun getOverridesForBill(billId: Long): Flow<List<BillOverrideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverride(override: BillOverrideEntity)

    @Delete
    suspend fun deleteOverride(override: BillOverrideEntity)
}

