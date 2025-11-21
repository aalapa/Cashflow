package com.cashflow.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cashflow.app.data.dao.AccountDao
import com.cashflow.app.data.dao.BillDao
import com.cashflow.app.data.dao.BillPaymentDao
import com.cashflow.app.data.dao.IncomeDao
import com.cashflow.app.data.dao.TransactionDao
import com.cashflow.app.data.entity.*

@Database(
    entities = [
        AccountEntity::class,
        IncomeEntity::class,
        IncomeOverrideEntity::class,
        BillEntity::class,
        BillOverrideEntity::class,
        BillPaymentEntity::class,
        TransactionEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CashFlowDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun incomeDao(): IncomeDao
    abstract fun billDao(): BillDao
    abstract fun billPaymentDao(): BillPaymentDao
    abstract fun transactionDao(): TransactionDao
}

