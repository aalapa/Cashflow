package com.cashflow.app.di

import android.content.Context
import androidx.room.Room
import com.cashflow.app.data.dao.*
import com.cashflow.app.data.database.CashFlowDatabase
import com.cashflow.app.data.repository.CashFlowRepositoryImpl
import com.cashflow.app.domain.repository.CashFlowRepository

object AppModule {
    private var database: CashFlowDatabase? = null
    private var repository: CashFlowRepository? = null

    fun provideDatabase(context: Context): CashFlowDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                CashFlowDatabase::class.java,
                "cashflow_database"
            )
                .fallbackToDestructiveMigration() // For development - remove in production and add proper migrations
                .build()
        }
        return database!!
    }

    fun provideRepository(context: Context): CashFlowRepository {
        if (repository == null) {
            val db = provideDatabase(context)
            repository = CashFlowRepositoryImpl(
                db.accountDao(),
                db.incomeDao(),
                db.billDao(),
                db.billPaymentDao(),
                db.transactionDao(),
                db.budgetDao(),
                db.budgetCategoryDao(),
                db.budgetCategoryAllocationDao(),
                db.budgetCategoryTransferDao(),
                db.categorizationRuleDao(),
                db
            )
        }
        return repository!!
    }
}

