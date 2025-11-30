package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.BudgetCategoryAllocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface BudgetCategoryAllocationDao {
    @Query("SELECT * FROM budget_category_allocations WHERE categoryId = :categoryId ORDER BY periodStart DESC")
    fun getAllocationsForCategory(categoryId: Long): Flow<List<BudgetCategoryAllocationEntity>>

    @Query("SELECT * FROM budget_category_allocations WHERE categoryId = :categoryId AND periodStart <= :date AND periodEnd >= :date LIMIT 1")
    suspend fun getAllocationForCategoryAndDate(categoryId: Long, date: LocalDate): BudgetCategoryAllocationEntity?

    @Query("SELECT * FROM budget_category_allocations WHERE categoryId = :categoryId AND periodStart <= :date AND periodEnd >= :date")
    suspend fun getAllocationForPeriod(categoryId: Long, date: LocalDate): BudgetCategoryAllocationEntity?

    @Query("SELECT * FROM budget_category_allocations WHERE id = :id")
    suspend fun getAllocationById(id: Long): BudgetCategoryAllocationEntity?

    @Query("SELECT * FROM budget_category_allocations WHERE categoryId = :categoryId AND periodStart >= :startDate AND periodEnd <= :endDate ORDER BY periodStart")
    fun getAllocationsInRange(categoryId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<BudgetCategoryAllocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: BudgetCategoryAllocationEntity): Long

    @Update
    suspend fun updateAllocation(allocation: BudgetCategoryAllocationEntity)

    @Delete
    suspend fun deleteAllocation(allocation: BudgetCategoryAllocationEntity)
    
    @Query("DELETE FROM budget_category_allocations WHERE categoryId = :categoryId")
    suspend fun deleteAllocationsForCategory(categoryId: Long)
    
    @Query("DELETE FROM budget_category_allocations")
    suspend fun deleteAllAllocations()
}

