package com.cashflow.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cashflow.app.data.dao.AccountDao
import com.cashflow.app.data.dao.BillDao
import com.cashflow.app.data.dao.BillPaymentDao
import com.cashflow.app.data.dao.IncomeDao
import com.cashflow.app.data.dao.TransactionDao
import com.cashflow.app.data.dao.EnvelopeDao
import com.cashflow.app.data.dao.EnvelopeAllocationDao
import com.cashflow.app.data.dao.EnvelopeTransferDao
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
        EnvelopeEntity::class,
        EnvelopeAllocationEntity::class,
        EnvelopeTransferEntity::class,
        CategorizationRuleEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CashFlowDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun incomeDao(): IncomeDao
    abstract fun billDao(): BillDao
    abstract fun billPaymentDao(): BillPaymentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun envelopeDao(): EnvelopeDao
    abstract fun envelopeAllocationDao(): EnvelopeAllocationDao
    abstract fun envelopeTransferDao(): EnvelopeTransferDao
    abstract fun categorizationRuleDao(): CategorizationRuleDao
}

