package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.BudgetCategoryTransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetCategoryTransferDao {
    @Query("SELECT * FROM budget_category_transfers WHERE fromCategoryId = :categoryId OR toCategoryId = :categoryId ORDER BY date DESC")
    fun getTransfersForCategory(categoryId: Long): Flow<List<BudgetCategoryTransferEntity>>

    @Query("SELECT * FROM budget_category_transfers ORDER BY date DESC")
    fun getAllTransfers(): Flow<List<BudgetCategoryTransferEntity>>

    @Query("SELECT * FROM budget_category_transfers WHERE id = :id")
    suspend fun getTransferById(id: Long): BudgetCategoryTransferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: BudgetCategoryTransferEntity): Long

    @Delete
    suspend fun deleteTransfer(transfer: BudgetCategoryTransferEntity)
    
    @Query("DELETE FROM budget_category_transfers")
    suspend fun deleteAllTransfers()
}

