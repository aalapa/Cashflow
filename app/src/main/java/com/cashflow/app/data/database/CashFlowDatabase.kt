package com.cashflow.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cashflow.app.data.dao.AccountDao
import com.cashflow.app.data.dao.BillDao
import com.cashflow.app.data.dao.BillPaymentDao
import com.cashflow.app.data.dao.IncomeDao
import com.cashflow.app.data.dao.TransactionDao
import com.cashflow.app.data.dao.BudgetDao
import com.cashflow.app.data.dao.BudgetCategoryDao
import com.cashflow.app.data.dao.BudgetCategoryAllocationDao
import com.cashflow.app.data.dao.BudgetCategoryTransferDao
import com.cashflow.app.data.dao.CategorizationRuleDao
import com.cashflow.app.data.entity.*

@Database(
    entities = [
        AccountEntity::class,
        IncomeEntity::class,
        IncomeOverrideEntity::class,
        BillEntity::class,
        BillOverrideEntity::class,
        BillPaymentEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        BudgetCategoryEntity::class,
        BudgetCategoryAllocationEntity::class,
        BudgetCategoryTransferEntity::class,
        CategorizationRuleEntity::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CashFlowDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun incomeDao(): IncomeDao
    abstract fun billDao(): BillDao
    abstract fun billPaymentDao(): BillPaymentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun budgetCategoryAllocationDao(): BudgetCategoryAllocationDao
    abstract fun budgetCategoryTransferDao(): BudgetCategoryTransferDao
    abstract fun categorizationRuleDao(): CategorizationRuleDao
}

