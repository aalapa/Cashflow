package com.cashflow.app.domain.repository

import com.cashflow.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface CashFlowRepository {
    // Accounts
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: Long): Account?
    suspend fun insertAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)

    // Income
    fun getAllActiveIncome(): Flow<List<Income>>
    suspend fun getIncomeById(id: Long): Income?
    suspend fun insertIncome(income: Income): Long
    suspend fun updateIncome(income: Income)
    suspend fun deleteIncome(income: Income)
    suspend fun getIncomeOverrides(incomeId: Long): Map<LocalDate, Double>
    suspend fun setIncomeOverride(incomeId: Long, date: LocalDate, amount: Double)
    suspend fun removeIncomeOverride(incomeId: Long, date: LocalDate)

    // Bills
    fun getAllActiveBills(): Flow<List<Bill>>
    suspend fun getBillById(id: Long): Bill?
    suspend fun insertBill(bill: Bill): Long
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(bill: Bill)
    suspend fun getBillOverrides(billId: Long): Map<LocalDate, Double>
    suspend fun setBillOverride(billId: Long, date: LocalDate, amount: Double)
    suspend fun removeBillOverride(billId: Long, date: LocalDate)
    
    // Bill Payments
    suspend fun markBillAsPaid(billId: Long, dueDate: LocalDate, accountId: Long, amount: Double): Long
    suspend fun isBillPaid(billId: Long, dueDate: LocalDate): Boolean
    fun getBillPayments(billId: Long): Flow<List<BillPayment>>
    
    // Income Received
    suspend fun markIncomeAsReceived(incomeId: Long, date: LocalDate, accountId: Long, amount: Double): Long
    
    // Future Occurrences
    suspend fun getFutureBillOccurrences(bill: Bill, startDate: LocalDate, endDate: LocalDate): List<BillOccurrence>
    suspend fun getFutureIncomeOccurrences(income: Income, startDate: LocalDate, endDate: LocalDate): List<IncomeOccurrence>

    // Transactions
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsForAccount(accountId: Long): Flow<List<Transaction>>
    fun getTransactionsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    // Cash Flow Calculation
    suspend fun calculateCashFlow(
        startDate: LocalDate,
        endDate: LocalDate,
        accounts: List<Account>
    ): List<CashFlowDay>
}

