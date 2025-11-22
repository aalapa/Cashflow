package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.IncomeEntity
import com.cashflow.app.data.entity.IncomeOverrideEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income WHERE isActive = 1 ORDER BY startDate")
    fun getAllActiveIncome(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income ORDER BY startDate")
    fun getAllIncome(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income WHERE id = :id")
    suspend fun getIncomeById(id: Long): IncomeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity): Long

    @Update
    suspend fun updateIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    // Income Overrides
    @Query("SELECT * FROM income_overrides WHERE incomeId = :incomeId AND date = :date")
    suspend fun getOverride(incomeId: Long, date: LocalDate): IncomeOverrideEntity?

    @Query("SELECT * FROM income_overrides WHERE incomeId = :incomeId")
    fun getOverridesForIncome(incomeId: Long): Flow<List<IncomeOverrideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverride(override: IncomeOverrideEntity)

    @Delete
    suspend fun deleteOverride(override: IncomeOverrideEntity)
    
    @Query("DELETE FROM income")
    suspend fun deleteAllIncome()
    
    @Query("DELETE FROM income_overrides")
    suspend fun deleteAllOverrides()
}

