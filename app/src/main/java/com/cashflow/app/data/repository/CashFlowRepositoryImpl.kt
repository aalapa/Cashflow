package com.cashflow.app.data.repository

import com.cashflow.app.data.dao.*
import com.cashflow.app.data.database.CashFlowDatabase
import com.cashflow.app.data.entity.*
import com.cashflow.app.data.model.*
import com.cashflow.app.domain.model.*
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

class CashFlowRepositoryImpl(
    private val accountDao: AccountDao,
    private val incomeDao: IncomeDao,
    private val billDao: BillDao,
    private val billPaymentDao: BillPaymentDao,
    private val transactionDao: TransactionDao,
    private val database: CashFlowDatabase
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
        // insertTransaction automatically updates account balance
        val transactionId = insertTransaction(transaction)
        
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
        return billPaymentDao.insertPayment(payment)
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
        // insertTransaction automatically updates account balance
        return insertTransaction(transaction)
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
        
        // Get all transactions for this income to check if received
        val transactions = transactionDao.getTransactionsBetween(startDate, endDate).first().map { it.toDomain() }
        
        var currentDate = startDate
        while (currentDate <= endDate) {
            if (shouldOccurOnDate(income.startDate, null, income.recurrenceType, currentDate)) {
                val amount = incomeOverrides[currentDate] ?: income.amount
                
                // Check if this income occurrence has been received
                val receivedTransaction = transactions.find { 
                    it.type == com.cashflow.app.data.model.TransactionType.INCOME && 
                    it.relatedIncomeId == income.id && 
                    it.date == currentDate 
                }
                
                occurrences.add(
                    IncomeOccurrence(
                        income = income,
                        date = currentDate,
                        amount = amount,
                        isReceived = receivedTransaction != null,
                        receivedDate = receivedTransaction?.date,
                        receivedIntoAccountId = receivedTransaction?.accountId
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

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }
    
    override suspend fun insertTransaction(transaction: Transaction): Long {
        val transactionId = transactionDao.insertTransaction(transaction.toEntity())
        
        // Update account balance(s) based on transaction type
        when (transaction.type) {
            com.cashflow.app.data.model.TransactionType.TRANSFER -> {
                // Transfer: deduct from source account, add to destination account
                if (transaction.toAccountId != null) {
                    // Deduct from source
                    val fromAccount = accountDao.getAccountById(transaction.accountId)
                    fromAccount?.let {
                        accountDao.updateAccount(it.copy(currentBalance = it.currentBalance - transaction.amount))
                    }
                    // Add to destination
                    val toAccount = accountDao.getAccountById(transaction.toAccountId)
                    toAccount?.let {
                        accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + transaction.amount))
                    }
                }
            }
            else -> {
                // Other transaction types: only affect source account
                val account = accountDao.getAccountById(transaction.accountId)
                account?.let {
                    val balanceChange = when (transaction.type) {
                        com.cashflow.app.data.model.TransactionType.INCOME -> transaction.amount
                        com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                        com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> -transaction.amount
                        com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> transaction.amount
                        else -> 0.0
                    }
                    accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + balanceChange))
                }
            }
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
            reverseTransactionEffect(oldTransaction)
            
            // Apply the effect of the new transaction
            applyTransactionEffect(transaction)
        } else {
            // If old transaction not found, just apply the new one
            applyTransactionEffect(transaction)
        }
    }
    
    private suspend fun reverseTransactionEffect(transaction: Transaction) {
        when (transaction.type) {
            com.cashflow.app.data.model.TransactionType.TRANSFER -> {
                if (transaction.toAccountId != null) {
                    // Reverse transfer: add back to source, subtract from destination
                    val fromAccount = accountDao.getAccountById(transaction.accountId)
                    fromAccount?.let {
                        accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + transaction.amount))
                    }
                    val toAccount = accountDao.getAccountById(transaction.toAccountId)
                    toAccount?.let {
                        accountDao.updateAccount(it.copy(currentBalance = it.currentBalance - transaction.amount))
                    }
                }
            }
            else -> {
                val account = accountDao.getAccountById(transaction.accountId)
                account?.let {
                    val balanceChange = when (transaction.type) {
                        com.cashflow.app.data.model.TransactionType.INCOME -> -transaction.amount
                        com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                        com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> transaction.amount
                        com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> -transaction.amount
                        else -> 0.0
                    }
                    accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + balanceChange))
                }
            }
        }
    }
    
    private suspend fun applyTransactionEffect(transaction: Transaction) {
        when (transaction.type) {
            com.cashflow.app.data.model.TransactionType.TRANSFER -> {
                if (transaction.toAccountId != null) {
                    // Transfer: deduct from source, add to destination
                    val fromAccount = accountDao.getAccountById(transaction.accountId)
                    fromAccount?.let {
                        accountDao.updateAccount(it.copy(currentBalance = it.currentBalance - transaction.amount))
                    }
                    val toAccount = accountDao.getAccountById(transaction.toAccountId)
                    toAccount?.let {
                        accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + transaction.amount))
                    }
                }
            }
            else -> {
                val account = accountDao.getAccountById(transaction.accountId)
                account?.let {
                    val balanceChange = when (transaction.type) {
                        com.cashflow.app.data.model.TransactionType.INCOME -> transaction.amount
                        com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                        com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> -transaction.amount
                        com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> transaction.amount
                        else -> 0.0
                    }
                    accountDao.updateAccount(it.copy(currentBalance = it.currentBalance + balanceChange))
                }
            }
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        // Get the transaction before deleting to reverse its effect
        val transactionEntity = transactionDao.getTransactionById(transaction.id)
        val transactionToDelete = transactionEntity?.toDomain() ?: transaction
        
        // Delete the transaction
        transactionDao.deleteTransaction(transaction.toEntity())
        
        // Reverse the transaction's effect on account balance(s)
        reverseTransactionEffect(transactionToDelete)
    }
    
    override suspend fun clearAllData() = withContext(Dispatchers.IO) {
        // Clear all data from all tables in proper order (respecting foreign keys)
        billPaymentDao.deleteAllPayments()
        transactionDao.deleteAllTransactions()
        billDao.deleteAllOverrides()
        billDao.deleteAllBills()
        incomeDao.deleteAllOverrides()
        incomeDao.deleteAllIncome()
        accountDao.deleteAllAccounts()
    }
    
    override suspend fun exportData(): String = withContext(Dispatchers.IO) {
        val accounts = accountDao.getAllAccounts().first()
        val income = incomeDao.getAllActiveIncome().first()
        val bills = billDao.getAllActiveBills().first()
        val transactions = transactionDao.getAllTransactions().first()
        
        // Get all overrides
        val incomeOverrides = mutableListOf<com.cashflow.app.data.entity.IncomeOverrideEntity>()
        income.forEach { inc ->
            incomeOverrides.addAll(incomeDao.getOverridesForIncome(inc.id).first())
        }
        
        val billOverrides = mutableListOf<com.cashflow.app.data.entity.BillOverrideEntity>()
        bills.forEach { bill ->
            billOverrides.addAll(billDao.getOverridesForBill(bill.id).first())
        }
        
        // Get all bill payments
        val billPayments = mutableListOf<com.cashflow.app.data.entity.BillPaymentEntity>()
        bills.forEach { bill ->
            billPayments.addAll(billPaymentDao.getPaymentsForBill(bill.id).first())
        }
        
        val exportData = com.cashflow.app.data.model.ExportData(
            version = 1,
            exportDate = Clock.System.now().toString(),
            accounts = accounts.map { it.toSerializable() },
            income = income.map { it.toSerializable() },
            incomeOverrides = incomeOverrides.map { it.toSerializable() },
            bills = bills.map { it.toSerializable() },
            billOverrides = billOverrides.map { it.toSerializable() },
            billPayments = billPayments.map { it.toSerializable() },
            transactions = transactions.map { it.toSerializable() }
        )
        
        val json = kotlinx.serialization.json.Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        return@withContext json.encodeToString(
            com.cashflow.app.data.model.ExportData.serializer(),
            exportData
        )
    }
    
    override suspend fun importData(jsonData: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val json = kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
                val exportData = json.decodeFromString(
                    com.cashflow.app.data.model.ExportData.serializer(),
                    jsonData
                )
                
                // Clear existing data first (in proper order)
                billPaymentDao.deleteAllPayments()
                transactionDao.deleteAllTransactions()
                billDao.deleteAllOverrides()
                billDao.deleteAllBills()
                incomeDao.deleteAllOverrides()
                incomeDao.deleteAllIncome()
                accountDao.deleteAllAccounts()
                
                // Import accounts first (they're referenced by other entities)
                exportData.accounts.forEach { serializableAccount ->
                    accountDao.insertAccount(serializableAccount.toEntity())
                }
                
                // Import income
                exportData.income.forEach { serializableIncome ->
                    incomeDao.insertIncome(serializableIncome.toEntity())
                }
                
                // Import income overrides
                exportData.incomeOverrides.forEach { override ->
                    incomeDao.insertOverride(override.toEntity())
                }
                
                // Import bills
                exportData.bills.forEach { serializableBill ->
                    billDao.insertBill(serializableBill.toEntity())
                }
                
                // Import bill overrides
                exportData.billOverrides.forEach { override ->
                    billDao.insertOverride(override.toEntity())
                }
                
                // Import transactions
                exportData.transactions.forEach { serializableTransaction ->
                    transactionDao.insertTransaction(serializableTransaction.toEntity())
                }
                
                // Import bill payments
                exportData.billPayments.forEach { payment ->
                    billPaymentDao.insertPayment(payment.toEntity())
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
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

        // Calculate starting balance at startDate.
        // If startDate is today or in the future, use currentBalance as the starting point
        // (it already includes all transactions, including future-dated ones from marking bills as paid).
        // If startDate is in the past, calculate from startingBalance + transactions before startDate.
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        
        var currentBalance = if (startDate >= today) {
            // For today or future dates, use currentBalance as starting point
            // Only reverse transactions from startDate to today (not future ones)
            // because currentBalance already includes future transactions from marking bills as paid
            val allTransactions = transactionDao.getAllTransactions().first().map { it.toDomain() }
            // Only reverse transactions that are strictly before startDate (if startDate > today)
            // or transactions on startDate if startDate == today (to get balance at start of day)
            val transactionsToReverse = if (startDate > today) {
                // If startDate is in the future, reverse transactions from startDate to today
                allTransactions.filter { it.date >= startDate && it.date <= today }
            } else {
                // If startDate is today, reverse transactions on today to get balance at start of today
                allTransactions.filter { it.date == today }
            }
            
            var balance = accounts.sumOf { it.currentBalance }
            // Reverse only transactions from startDate to today (not future ones)
            for (transaction in transactionsToReverse) {
                when (transaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> balance -= transaction.amount
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> balance += transaction.amount
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> balance -= transaction.amount
                    com.cashflow.app.data.model.TransactionType.TRANSFER -> { /* No change to total */ }
                }
            }
            balance
        } else {
            // For past dates, calculate from startingBalance
            val allTransactions = transactionDao.getAllTransactions().first().map { it.toDomain() }
            val transactionsBeforeStart = allTransactions.filter { it.date < startDate }
            
            var balance = accounts.sumOf { it.startingBalance }
            // Apply transactions before startDate
            for (transaction in transactionsBeforeStart) {
                when (transaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> balance += transaction.amount
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> balance -= transaction.amount
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> balance += transaction.amount
                    com.cashflow.app.data.model.TransactionType.TRANSFER -> { /* No change to total */ }
                }
            }
            balance
        }

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
                        // Only add to projected income and balance if not yet received
                        val amount = incomeOverrides[income]?.get(currentDate) ?: income.amount
                        dayIncome.add(IncomeEvent(income.id, income.name, amount, income.accountId))
                        currentBalance += amount
                    }
                    // If received, it will show up in transactions instead, so don't add to dayIncome
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
            // Note: For future dates (after today), transactions are already reflected in currentBalance
            // because when bills are marked as paid, the balance is updated immediately.
            // So we only process transactions on or before today to avoid double-counting.
            for (transaction in transactionList) {
                if (transaction.date == currentDate) {
                    dayTransactions.add(transaction)
                    // Only process transactions that are on or before today
                    // Future transactions are already in currentBalance, so we skip processing them
                    if (currentDate <= today) {
                        when (transaction.type) {
                            com.cashflow.app.data.model.TransactionType.INCOME -> currentBalance += transaction.amount
                            com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                            com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> currentBalance -= transaction.amount
                            com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> {
                                // Manual adjustments can be positive or negative
                                // For simplicity, we'll treat amount as the change
                                currentBalance += transaction.amount
                            }
                            com.cashflow.app.data.model.TransactionType.TRANSFER -> {
                                // Transfers don't affect total cash balance (just move money between accounts)
                                // No change to currentBalance
                            }
                        }
                    }
                    // For future dates, the transaction is already reflected in currentBalance,
                    // so we don't process it again (it's just shown in the dayTransactions list for display)
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
        id, accountId, toAccountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id, accountId, toAccountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId
    )

    private fun BillPaymentEntity.toDomain() = BillPayment(
        id, billId, accountId, paymentDate, amount, timestamp, transactionId
    )
}

