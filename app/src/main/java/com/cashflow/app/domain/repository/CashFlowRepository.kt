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
    suspend fun markBillAsPaid(billId: Long, dueDate: LocalDate, accountId: Long, amount: Double, envelopeId: Long? = null): Long
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
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    
    // Data Management
    suspend fun clearAllData()
    suspend fun exportData(): String
    suspend fun importData(jsonData: String): Result<Unit>

    // Cash Flow Calculation
    suspend fun calculateCashFlow(
        startDate: LocalDate,
        endDate: LocalDate,
        accounts: List<Account>
    ): List<CashFlowDay>
    
    // Envelopes
    fun getAllActiveEnvelopes(): Flow<List<Envelope>>
    fun getAllEnvelopes(): Flow<List<Envelope>>
    suspend fun getEnvelopeById(id: Long): Envelope?
    suspend fun insertEnvelope(envelope: Envelope): Long
    suspend fun updateEnvelope(envelope: Envelope)
    suspend fun deleteEnvelope(envelope: Envelope)
    
    // Envelope Allocations
    fun getAllocationsForEnvelope(envelopeId: Long): Flow<List<EnvelopeAllocation>>
    suspend fun getAllocationForPeriod(envelopeId: Long, date: LocalDate): EnvelopeAllocation?
    suspend fun insertAllocation(allocation: EnvelopeAllocation): Long
    suspend fun updateAllocation(allocation: EnvelopeAllocation)
    suspend fun deleteAllocation(allocation: EnvelopeAllocation)
    
    // Envelope Balance Calculation
    suspend fun getEnvelopeBalance(envelopeId: Long, date: LocalDate): Double
    fun getEnvelopeTransactions(envelopeId: Long): Flow<List<Transaction>>
    
    // Period Management
    suspend fun resetEnvelopePeriod(envelopeId: Long, newPeriodStart: LocalDate, carryOverAmount: Double = 0.0)
    suspend fun getEnvelopeHistory(envelopeId: Long, startDate: LocalDate, endDate: LocalDate): List<EnvelopePeriodHistory>
    
    // Envelope Transfers
    fun getEnvelopeTransfers(envelopeId: Long): Flow<List<EnvelopeTransfer>>
    suspend fun transferBetweenEnvelopes(fromEnvelopeId: Long, toEnvelopeId: Long, amount: Double, date: LocalDate, description: String?): Long
    suspend fun deleteTransfer(transfer: EnvelopeTransfer)
    
    // Auto-Categorization Rules
    fun getAllCategorizationRules(): Flow<List<CategorizationRule>>
    fun getRulesForEnvelope(envelopeId: Long): Flow<List<CategorizationRule>>
    suspend fun insertCategorizationRule(rule: CategorizationRule): Long
    suspend fun updateCategorizationRule(rule: CategorizationRule)
    suspend fun deleteCategorizationRule(rule: CategorizationRule)
    suspend fun applyAutoCategorization(transaction: Transaction): Long? // Returns envelopeId if matched
    
    // Analytics
    suspend fun getEnvelopeSpendingTrend(envelopeId: Long, months: Int): List<MonthlySpending>
    suspend fun getTotalSpendingByEnvelope(startDate: LocalDate, endDate: LocalDate): Map<Long, Double>
}

data class EnvelopePeriodHistory(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val allocated: Double,
    val spent: Double,
    val balance: Double,
    val carriedOver: Double = 0.0
)

data class MonthlySpending(
    val month: String, // "2024-01"
    val amount: Double
)

