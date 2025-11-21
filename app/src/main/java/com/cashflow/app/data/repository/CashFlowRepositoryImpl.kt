package com.cashflow.app.data.repository

import com.cashflow.app.data.dao.*
import com.cashflow.app.data.entity.*
import com.cashflow.app.data.model.RecurrenceType
import com.cashflow.app.domain.model.*
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*

class CashFlowRepositoryImpl(
    private val accountDao: AccountDao,
    private val incomeDao: IncomeDao,
    private val billDao: BillDao,
    private val billPaymentDao: BillPaymentDao,
    private val transactionDao: TransactionDao
) : CashFlowRepository {

    override fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getAccountById(id: Long): Account? =
        accountDao.getAccountById(id)?.toDomain()

    override suspend fun insertAccount(account: Account): Long =
        accountDao.insertAccount(account.toEntity())

    override suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account.toEntity())
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account.toEntity())
    }

    override fun getAllActiveIncome(): Flow<List<Income>> =
        incomeDao.getAllActiveIncome().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getIncomeById(id: Long): Income? =
        incomeDao.getIncomeById(id)?.toDomain()

    override suspend fun insertIncome(income: Income): Long =
        incomeDao.insertIncome(income.toEntity())

    override suspend fun updateIncome(income: Income) {
        incomeDao.updateIncome(income.toEntity())
    }

    override suspend fun deleteIncome(income: Income) {
        incomeDao.deleteIncome(income.toEntity())
    }

    override suspend fun getIncomeOverrides(incomeId: Long): Map<LocalDate, Double> {
        val overrides = incomeDao.getOverridesForIncome(incomeId).first()
        return overrides.associate { it.date to it.amount }
    }

    override suspend fun setIncomeOverride(incomeId: Long, date: LocalDate, amount: Double) {
        incomeDao.insertOverride(IncomeOverrideEntity(0, incomeId, date, amount))
    }

    override suspend fun removeIncomeOverride(incomeId: Long, date: LocalDate) {
        incomeDao.getOverride(incomeId, date)?.let {
            incomeDao.deleteOverride(it)
        }
    }

    override fun getAllActiveBills(): Flow<List<Bill>> =
        billDao.getAllActiveBills().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getBillById(id: Long): Bill? =
        billDao.getBillById(id)?.toDomain()

    override suspend fun insertBill(bill: Bill): Long =
        billDao.insertBill(bill.toEntity())

    override suspend fun updateBill(bill: Bill) {
        billDao.updateBill(bill.toEntity())
    }

    override suspend fun deleteBill(bill: Bill) {
        billDao.deleteBill(bill.toEntity())
    }

    override suspend fun getBillOverrides(billId: Long): Map<LocalDate, Double> {
        val overrides = billDao.getOverridesForBill(billId).first()
        return overrides.associate { it.date to it.amount }
    }

    override suspend fun setBillOverride(billId: Long, date: LocalDate, amount: Double) {
        billDao.insertOverride(BillOverrideEntity(0, billId, date, amount))
    }

    override suspend fun removeBillOverride(billId: Long, date: LocalDate) {
        billDao.getOverride(billId, date)?.let {
            billDao.deleteOverride(it)
        }
    }

    override suspend fun markBillAsPaid(billId: Long, dueDate: LocalDate, accountId: Long, amount: Double): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Create transaction for the payment
        val transaction = Transaction(
            id = 0,
            accountId = accountId,
            type = com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
            amount = amount,
            date = dueDate,
            timestamp = timestamp,
            description = "Bill payment",
            relatedBillId = billId
        )
        val transactionId = transactionDao.insertTransaction(transaction.toEntity())
        
        // Create bill payment record
        val payment = BillPaymentEntity(
            id = 0,
            billId = billId,
            accountId = accountId,
            paymentDate = dueDate,
            amount = amount,
            timestamp = timestamp,
            transactionId = transactionId
        )
        billPaymentDao.insertPayment(payment)
        
        // Update account balance
        val account = accountDao.getAccountById(accountId)
        account?.let {
            accountDao.updateAccount(it.copy(currentBalance = it.currentBalance - amount))
        }
        
        return payment.id
    }

    override suspend fun markIncomeAsReceived(incomeId: Long, date: LocalDate, accountId: Long, amount: Double): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Create transaction for the income
        val transaction = Transaction(
            id = 0,
            accountId = accountId,
            type = com.cashflow.app.data.model.TransactionType.INCOME,
            amount = amount,
            date = date,
            timestamp = timestamp,
            description = "Income received",
            relatedIncomeId = incomeId
        )
        val transactionId = transactionDao.insertTransaction(transaction.toEntity())
        
        // Update account balance
        val account = accountDao.getAccountById(accountId)
        account?.let {
            accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + amount))
        }
        
        return transactionId
    }

    override suspend fun isBillPaid(billId: Long, dueDate: LocalDate): Boolean {
        return billPaymentDao.getPayment(billId, dueDate) != null
    }

    override fun getBillPayments(billId: Long): Flow<List<BillPayment>> =
        billPaymentDao.getPaymentsForBill(billId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getFutureBillOccurrences(bill: Bill, startDate: LocalDate, endDate: LocalDate): List<BillOccurrence> {
        val occurrences = mutableListOf<BillOccurrence>()
        val billOverrides = getBillOverrides(bill.id)
        val payments = billPaymentDao.getPaymentsForBill(bill.id).first()
        val paymentMap = payments.associateBy { it.paymentDate }
        
        var currentDate = startDate
        while (currentDate <= endDate) {
            if (shouldOccurOnDate(bill.startDate, bill.endDate, bill.recurrenceType, currentDate)) {
                val amount = billOverrides[currentDate] ?: bill.amount
                val payment = paymentMap[currentDate]
                occurrences.add(
                    BillOccurrence(
                        bill = bill,
                        dueDate = currentDate,
                        amount = amount,
                        isPaid = payment != null,
                        paymentDate = payment?.paymentDate,
                        paidFromAccountId = payment?.accountId
                    )
                )
            }
            currentDate = LocalDate.fromEpochDays(currentDate.toEpochDays() + 1)
        }
        return occurrences
    }

    override suspend fun getFutureIncomeOccurrences(income: Income, startDate: LocalDate, endDate: LocalDate): List<IncomeOccurrence> {
        val occurrences = mutableListOf<IncomeOccurrence>()
        val incomeOverrides = getIncomeOverrides(income.id)
        
        var currentDate = startDate
        while (currentDate <= endDate) {
            if (shouldOccurOnDate(income.startDate, null, income.recurrenceType, currentDate)) {
                val amount = incomeOverrides[currentDate] ?: income.amount
                occurrences.add(
                    IncomeOccurrence(
                        income = income,
                        date = currentDate,
                        amount = amount
                    )
                )
            }
            currentDate = LocalDate.fromEpochDays(currentDate.toEpochDays() + 1)
        }
        return occurrences
    }

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getTransactionsForAccount(accountId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsForAccount(accountId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getTransactionsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> =
        transactionDao.getTransactionsBetween(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        val transactionId = transactionDao.insertTransaction(transaction.toEntity())
        
        // Update account balance based on transaction type
        val account = accountDao.getAccountById(transaction.accountId)
        account?.let {
            val balanceChange = when (transaction.type) {
                com.cashflow.app.data.model.TransactionType.INCOME -> transaction.amount
                com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> -transaction.amount
                com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> transaction.amount // Can be positive or negative
            }
            accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + balanceChange))
        }
        
        return transactionId
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        // Get the old transaction to calculate balance difference
        val oldTransactionEntity = transactionDao.getTransactionById(transaction.id)
        val oldTransaction = oldTransactionEntity?.toDomain()
        
        // Update the transaction
        transactionDao.updateTransaction(transaction.toEntity())
        
        // Adjust account balances
        if (oldTransaction != null) {
            // Reverse the effect of the old transaction
            val oldAccount = accountDao.getAccountById(oldTransaction.accountId)
            oldAccount?.let {
                val oldBalanceChange = when (oldTransaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> -oldTransaction.amount // Reverse: subtract
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> oldTransaction.amount // Reverse: add back
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> -oldTransaction.amount // Reverse: subtract
                }
                accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + oldBalanceChange))
            }
            
            // Apply the effect of the new transaction
            val newAccount = accountDao.getAccountById(transaction.accountId)
            newAccount?.let {
                val newBalanceChange = when (transaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> transaction.amount
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> -transaction.amount
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> transaction.amount
                }
                accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + newBalanceChange))
            }
        } else {
            // If old transaction not found, just apply the new one (shouldn't happen, but handle gracefully)
            val account = accountDao.getAccountById(transaction.accountId)
            account?.let {
                val balanceChange = when (transaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> transaction.amount
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> -transaction.amount
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> transaction.amount
                }
                accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + balanceChange))
            }
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        // Get the transaction before deleting to reverse its effect
        val transactionEntity = transactionDao.getTransactionById(transaction.id)
        val transactionToDelete = transactionEntity?.toDomain() ?: transaction
        
        // Delete the transaction
        transactionDao.deleteTransaction(transaction.toEntity())
        
        // Reverse the effect on account balance
        val account = accountDao.getAccountById(transactionToDelete.accountId)
        account?.let {
            val balanceChange = when (transactionToDelete.type) {
                com.cashflow.app.data.model.TransactionType.INCOME -> -transactionToDelete.amount // Reverse: subtract
                com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> transactionToDelete.amount // Reverse: add back
                com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> -transactionToDelete.amount // Reverse: subtract
            }
            accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + balanceChange))
        }
    }

    override suspend fun calculateCashFlow(
        startDate: LocalDate,
        endDate: LocalDate,
        accounts: List<Account>
    ): List<CashFlowDay> {
        val incomeEntities = incomeDao.getAllActiveIncome().first()
        val billEntities = billDao.getAllActiveBills().first()
        val transactionEntities = transactionDao.getTransactionsBetween(startDate, endDate).first()
        
        val incomeList = incomeEntities.map { it.toDomain() }
        val billList = billEntities.map { it.toDomain() }
        val transactionList = transactionEntities.map { it.toDomain() }

        // Get all overrides - map by domain model for easier lookup
        val incomeOverrides = incomeList.associateWith { income ->
            val overrides = incomeDao.getOverridesForIncome(income.id).first()
            overrides.associate { it.date to it.amount }
        }

        val billOverrides = billList.associateWith { bill ->
            val overrides = billDao.getOverridesForBill(bill.id).first()
            overrides.associate { it.date to it.amount }
        }

        // Calculate starting balance (sum of all account balances)
        var currentBalance = accounts.sumOf { it.currentBalance }

        val cashFlowDays = mutableListOf<CashFlowDay>()
        var currentDate = startDate

        while (currentDate <= endDate) {
            val dayIncome = mutableListOf<IncomeEvent>()
            val dayBills = mutableListOf<BillEvent>()
            val dayTransactions = mutableListOf<Transaction>()

            // Process income (skip if already received)
            for (income in incomeList) {
                if (shouldOccurOnDate(income.startDate, null, income.recurrenceType, currentDate)) {
                    // Check if income was already received (has transaction with relatedIncomeId)
                    val isReceived = transactionList.any { 
                        it.type == com.cashflow.app.data.model.TransactionType.INCOME && 
                        it.relatedIncomeId == income.id && 
                        it.date == currentDate 
                    }
                    if (!isReceived) {
                        val amount = incomeOverrides[income]?.get(currentDate) ?: income.amount
                        dayIncome.add(IncomeEvent(income.id, income.name, amount, income.accountId))
                        currentBalance += amount
                    }
                }
            }

            // Process bills (skip if already paid)
            for (bill in billList) {
                if (shouldOccurOnDate(bill.startDate, bill.endDate, bill.recurrenceType, currentDate)) {
                    // Check if bill is paid for this date
                    val isPaid = billPaymentDao.getPayment(bill.id, currentDate) != null
                    if (!isPaid) {
                        val amount = billOverrides[bill]?.get(currentDate) ?: bill.amount
                        dayBills.add(BillEvent(bill.id, bill.name, amount))
                        currentBalance -= amount
                    }
                }
            }

            // Process transactions
            for (transaction in transactionList) {
                if (transaction.date == currentDate) {
                    dayTransactions.add(transaction)
                    when (transaction.type) {
                        com.cashflow.app.data.model.TransactionType.INCOME -> currentBalance += transaction.amount
                        com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                        com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> currentBalance -= transaction.amount
                        com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> {
                            // Manual adjustments can be positive or negative
                            // For simplicity, we'll treat amount as the change
                            currentBalance += transaction.amount
                        }
                    }
                }
            }

            val isNegative = currentBalance < 0
            val isWarning = currentBalance >= 0 && currentBalance < 100

            cashFlowDays.add(
                CashFlowDay(
                    date = currentDate,
                    balance = currentBalance,
                    isNegative = isNegative,
                    isWarning = isWarning,
                    income = dayIncome,
                    bills = dayBills,
                    transactions = dayTransactions
                )
            )

            currentDate = LocalDate.fromEpochDays(currentDate.toEpochDays() + 1)
        }

        return cashFlowDays
    }

    private fun shouldOccurOnDate(startDate: LocalDate, endDate: LocalDate?, recurrenceType: RecurrenceType, checkDate: LocalDate): Boolean {
        if (checkDate < startDate) return false
        if (endDate != null && checkDate > endDate) return false

        return when (recurrenceType) {
            RecurrenceType.BI_WEEKLY -> {
                val daysBetween = (checkDate.toEpochDays() - startDate.toEpochDays())
                daysBetween >= 0 && daysBetween % 14 == 0
            }
            RecurrenceType.WEEKLY -> {
                val daysBetween = (checkDate.toEpochDays() - startDate.toEpochDays())
                daysBetween >= 0 && daysBetween % 7 == 0
            }
            RecurrenceType.MONTHLY -> {
                checkDate.dayOfMonth == startDate.dayOfMonth && checkDate >= startDate
            }
            RecurrenceType.CUSTOM -> false // Handle custom recurrence separately if needed
        }
    }

    // Extension functions for entity conversion
    private fun AccountEntity.toDomain() = Account(id, name, type, startingBalance, currentBalance)
    private fun Account.toEntity() = AccountEntity(id, name, type, startingBalance, currentBalance)

    private fun IncomeEntity.toDomain() = Income(id, name, amount, recurrenceType, startDate, accountId, isActive)
    private fun Income.toEntity() = IncomeEntity(id, name, amount, recurrenceType, startDate, accountId, isActive)

    private fun BillEntity.toDomain() = Bill(id, name, amount, recurrenceType, startDate, endDate, isActive, reminderDaysBefore)
    private fun Bill.toEntity() = BillEntity(id, name, amount, recurrenceType, startDate, endDate, null, isActive, reminderDaysBefore)

    private fun TransactionEntity.toDomain() = Transaction(
        id, accountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id, accountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId
    )

    private fun BillPaymentEntity.toDomain() = BillPayment(
        id, billId, accountId, paymentDate, amount, timestamp, transactionId
    )
}

