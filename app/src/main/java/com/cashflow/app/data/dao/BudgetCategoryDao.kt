package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.BudgetCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetCategoryDao {
    @Query("SELECT * FROM budget_categories WHERE isActive = 1 ORDER BY name")
    fun getAllActiveCategories(): Flow<List<BudgetCategoryEntity>>

    @Query("SELECT * FROM budget_categories ORDER BY name")
    fun getAllCategories(): Flow<List<BudgetCategoryEntity>>

    @Query("SELECT * FROM budget_categories WHERE budgetId = :budgetId AND isActive = 1 ORDER BY name")
    fun getCategoriesForBudget(budgetId: Long): Flow<List<BudgetCategoryEntity>>

    @Query("SELECT * FROM budget_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): BudgetCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: BudgetCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: BudgetCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: BudgetCategoryEntity)

    @Query("DELETE FROM budget_categories")
    suspend fun deleteAllCategories()
}

