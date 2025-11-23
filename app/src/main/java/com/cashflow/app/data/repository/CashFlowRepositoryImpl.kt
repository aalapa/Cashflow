package com.cashflow.app.data.repository

import com.cashflow.app.data.dao.*
import com.cashflow.app.data.database.CashFlowDatabase
import com.cashflow.app.data.entity.*
import com.cashflow.app.data.model.*
import com.cashflow.app.domain.model.*
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.domain.repository.EnvelopePeriodHistory
import com.cashflow.app.domain.repository.MonthlySpending
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.datetime.toLocalDateTime

class CashFlowRepositoryImpl(
    private val accountDao: AccountDao,
    private val incomeDao: IncomeDao,
    private val billDao: BillDao,
    private val billPaymentDao: BillPaymentDao,
    private val transactionDao: TransactionDao,
    private val envelopeDao: EnvelopeDao,
    private val envelopeAllocationDao: EnvelopeAllocationDao,
    private val envelopeTransferDao: EnvelopeTransferDao,
    private val categorizationRuleDao: CategorizationRuleDao,
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

    override suspend fun markBillAsPaid(billId: Long, dueDate: LocalDate, accountId: Long, amount: Double, envelopeId: Long?): Long {
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
            relatedBillId = billId,
            envelopeId = envelopeId
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
        envelopeTransferDao.deleteAllTransfers()
        categorizationRuleDao.deleteAllRules()
        envelopeAllocationDao.deleteAllAllocations()
        envelopeDao.deleteAllEnvelopes()
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
        
        // Get envelope data
        val envelopes = envelopeDao.getAllEnvelopes().first()
        val envelopeAllocations = mutableListOf<EnvelopeAllocationEntity>()
        envelopes.forEach { env ->
            envelopeAllocations.addAll(envelopeAllocationDao.getAllocationsForEnvelope(env.id).first())
        }
        val envelopeTransfers = envelopeTransferDao.getAllTransfers().first()
        val categorizationRules = categorizationRuleDao.getAllActiveRules().first()
        
        val exportData = com.cashflow.app.data.model.ExportData(
            version = 1,
            exportDate = Clock.System.now().toString(),
            accounts = accounts.map { it.toSerializable() },
            income = income.map { it.toSerializable() },
            incomeOverrides = incomeOverrides.map { it.toSerializable() },
            bills = bills.map { it.toSerializable() },
            billOverrides = billOverrides.map { it.toSerializable() },
            billPayments = billPayments.map { it.toSerializable() },
            transactions = transactions.map { it.toSerializable() },
            envelopes = envelopes.map { it.toSerializable() },
            envelopeAllocations = envelopeAllocations.map { it.toSerializable() },
            envelopeTransfers = envelopeTransfers.map { it.toSerializable() },
            categorizationRules = categorizationRules.map { it.toSerializable() }
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
                envelopeTransferDao.deleteAllTransfers()
                categorizationRuleDao.deleteAllRules()
                envelopeAllocationDao.deleteAllAllocations()
                envelopeDao.deleteAllEnvelopes()
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
                
                // Import envelopes
                exportData.envelopes.forEach { envelope ->
                    envelopeDao.insertEnvelope(envelope.toEntity())
                }
                
                // Import envelope allocations
                exportData.envelopeAllocations.forEach { allocation ->
                    envelopeAllocationDao.insertAllocation(allocation.toEntity())
                }
                
                // Import envelope transfers
                exportData.envelopeTransfers.forEach { transfer ->
                    envelopeTransferDao.insertTransfer(transfer.toEntity())
                }
                
                // Import categorization rules
                exportData.categorizationRules.forEach { rule ->
                    categorizationRuleDao.insertRule(rule.toEntity())
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

        // Calculate starting balance: use current account balances, but EXCLUDE
        // transactions in our date range (since we'll process them below)
        var currentBalance = accounts.sumOf { it.currentBalance }
        
        // Subtract transactions in our date range from the starting balance
        // since they're already included in currentBalance but we'll add them back below
        for (transaction in transactionList) {
            if (transaction.date >= startDate) {
                when (transaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> currentBalance -= transaction.amount
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> currentBalance += transaction.amount
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> currentBalance -= transaction.amount
                    com.cashflow.app.data.model.TransactionType.TRANSFER -> { /* No change to total */ }
                }
            }
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
                        com.cashflow.app.data.model.TransactionType.TRANSFER -> {
                            // Transfers don't affect total cash balance (just move money between accounts)
                            // No change to currentBalance
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
        id, accountId, toAccountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId, envelopeId
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id, accountId, toAccountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId, envelopeId
    )

    private fun BillPaymentEntity.toDomain() = BillPayment(
        id, billId, accountId, paymentDate, amount, timestamp, transactionId
    )

    // Envelope conversions
    private fun EnvelopeEntity.toDomain() = Envelope(
        id, name, 
        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color)), 
        icon, budgetedAmount, periodType, accountId, carryOverEnabled, isActive,
        createdAt.toInstant(TimeZone.currentSystemDefault())
    )

    private fun Envelope.toEntity(createdAt: LocalDateTime) = EnvelopeEntity(
        id, name, 
        String.format("#%08X", color.value.toLong() and 0xFFFFFFFF), 
        icon, budgetedAmount, periodType, accountId, carryOverEnabled, createdAt, isActive
    )

    private fun EnvelopeAllocationEntity.toDomain() = EnvelopeAllocation(
        id, envelopeId, amount, periodStart, periodEnd, incomeId,
        createdAt.toInstant(TimeZone.currentSystemDefault())
    )

    private fun EnvelopeAllocation.toEntity(createdAt: LocalDateTime) = EnvelopeAllocationEntity(
        id, envelopeId, amount, periodStart, periodEnd, incomeId, createdAt
    )

    private fun EnvelopeTransferEntity.toDomain() = EnvelopeTransfer(
        id, fromEnvelopeId, toEnvelopeId, amount, date, description, timestamp
    )

    private fun EnvelopeTransfer.toEntity() = EnvelopeTransferEntity(
        id, fromEnvelopeId, toEnvelopeId, amount, date, description, timestamp
    )

    private fun CategorizationRuleEntity.toDomain() = CategorizationRule(
        id, envelopeId, keyword, isActive
    )

    private fun CategorizationRule.toEntity(createdAt: LocalDateTime) = CategorizationRuleEntity(
        id, envelopeId, keyword, isActive, createdAt
    )

    // Envelope methods
    override fun getAllActiveEnvelopes(): Flow<List<Envelope>> =
        envelopeDao.getAllActiveEnvelopes().map { entities -> entities.map { it.toDomain() } }

    override fun getAllEnvelopes(): Flow<List<Envelope>> =
        envelopeDao.getAllEnvelopes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getEnvelopeById(id: Long): Envelope? =
        envelopeDao.getEnvelopeById(id)?.toDomain()

    override suspend fun insertEnvelope(envelope: Envelope): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return envelopeDao.insertEnvelope(envelope.toEntity(timestamp))
    }

    override suspend fun updateEnvelope(envelope: Envelope) {
        val existing = envelopeDao.getEnvelopeById(envelope.id)
        if (existing != null) {
            envelopeDao.updateEnvelope(envelope.toEntity(existing.createdAt))
        } else {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            envelopeDao.updateEnvelope(envelope.toEntity(timestamp))
        }
    }

    override suspend fun deleteEnvelope(envelope: Envelope) {
        val existing = envelopeDao.getEnvelopeById(envelope.id)
        existing?.let { envelopeDao.deleteEnvelope(it) }
    }

    // Envelope Allocation methods
    override fun getAllocationsForEnvelope(envelopeId: Long): Flow<List<EnvelopeAllocation>> =
        envelopeAllocationDao.getAllocationsForEnvelope(envelopeId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAllocationForPeriod(envelopeId: Long, date: LocalDate): EnvelopeAllocation? =
        envelopeAllocationDao.getAllocationForPeriod(envelopeId, date)?.toDomain()

    override suspend fun insertAllocation(allocation: EnvelopeAllocation): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return envelopeAllocationDao.insertAllocation(allocation.toEntity(timestamp))
    }

    override suspend fun updateAllocation(allocation: EnvelopeAllocation) {
        val existing = envelopeAllocationDao.getAllocationById(allocation.id)
        if (existing != null) {
            envelopeAllocationDao.updateAllocation(allocation.toEntity(existing.createdAt))
        } else {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            envelopeAllocationDao.updateAllocation(allocation.toEntity(timestamp))
        }
    }

    override suspend fun deleteAllocation(allocation: EnvelopeAllocation) {
        val existing = envelopeAllocationDao.getAllocationById(allocation.id)
        existing?.let { envelopeAllocationDao.deleteAllocation(it) }
    }

    // Envelope Balance Calculation
    override suspend fun getEnvelopeBalance(envelopeId: Long, date: LocalDate): Double {
        val allocations = envelopeAllocationDao.getAllocationsInRange(envelopeId, LocalDate(2000, 1, 1), date).first()
        val totalAllocated = allocations.sumOf { it.amount }

        val transactions = transactionDao.getTransactionsBetween(LocalDate(2000, 1, 1), date).first()
        val envelopeTransactions = transactions.filter { it.envelopeId == envelopeId }
        val totalSpent = envelopeTransactions.sumOf {
            when (it.type) {
                TransactionType.BILL_PAYMENT, TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                else -> 0.0
            }
        }

        return totalAllocated - totalSpent
    }

    override fun getEnvelopeTransactions(envelopeId: Long): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { entities ->
            entities.filter { it.envelopeId == envelopeId }
                .map { it.toDomain() }
        }

    // Period Management
    override suspend fun resetEnvelopePeriod(envelopeId: Long, newPeriodStart: LocalDate, carryOverAmount: Double) {
        // Period reset logic - can be implemented later if needed
    }

    override suspend fun getEnvelopeHistory(envelopeId: Long, startDate: LocalDate, endDate: LocalDate): List<EnvelopePeriodHistory> {
        val allocations = envelopeAllocationDao.getAllocationsInRange(envelopeId, startDate, endDate).first()
        val transactions = transactionDao.getTransactionsBetween(startDate, endDate).first()
            .filter { it.envelopeId == envelopeId }

        val history = mutableListOf<EnvelopePeriodHistory>()

        for (allocation in allocations.sortedBy { it.periodStart }) {
            val periodTransactions = transactions.filter {
                it.date >= allocation.periodStart && it.date <= allocation.periodEnd
            }

            val spent = periodTransactions.sumOf {
                when (it.type) {
                    TransactionType.BILL_PAYMENT, TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                    else -> 0.0
                }
            }

            val balance = allocation.amount - spent

            history.add(
                EnvelopePeriodHistory(
                    periodStart = allocation.periodStart,
                    periodEnd = allocation.periodEnd,
                    allocated = allocation.amount,
                    spent = spent,
                    balance = balance,
                    carriedOver = 0.0
                )
            )
        }

        // Calculate carry-over amounts if enabled
        val envelope = envelopeDao.getEnvelopeById(envelopeId)
        if (envelope != null && envelope.carryOverEnabled) {
            for (i in 0 until history.size - 1) {
                val currentPeriod = history[i]
                val nextPeriod = history[i + 1]
                if (currentPeriod.balance > 0) {
                    history[i + 1] = nextPeriod.copy(carriedOver = currentPeriod.balance)
                }
            }
        }

        return history
    }

    // Envelope Transfers
    override fun getEnvelopeTransfers(envelopeId: Long): Flow<List<EnvelopeTransfer>> =
        envelopeTransferDao.getTransfersForEnvelope(envelopeId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun transferBetweenEnvelopes(
        fromEnvelopeId: Long,
        toEnvelopeId: Long,
        amount: Double,
        date: LocalDate,
        description: String?
    ): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())

        val fromAllocation = getAllocationForPeriod(fromEnvelopeId, date)
        val toAllocation = getAllocationForPeriod(toEnvelopeId, date)

        val transfer = EnvelopeTransfer(
            id = 0,
            fromEnvelopeId = fromEnvelopeId,
            toEnvelopeId = toEnvelopeId,
            amount = amount,
            date = date,
            description = description,
            timestamp = timestamp
        )

        val transferId = envelopeTransferDao.insertTransfer(transfer.toEntity())

        // Adjust allocations
        val fromAllocationEntity = fromAllocation?.let {
            envelopeAllocationDao.getAllocationById(it.id)
        }
        val toAllocationEntity = toAllocation?.let {
            envelopeAllocationDao.getAllocationById(it.id)
        }

        if (fromAllocationEntity != null) {
            val updated = fromAllocationEntity.copy(amount = fromAllocationEntity.amount - amount)
            envelopeAllocationDao.updateAllocation(updated)
        }

        if (toAllocationEntity != null) {
            val updated = toAllocationEntity.copy(amount = toAllocationEntity.amount + amount)
            envelopeAllocationDao.updateAllocation(updated)
        } else {
            val toEnvelope = envelopeDao.getEnvelopeById(toEnvelopeId)
            val (periodStart, periodEnd) = calculatePeriodDates(date, toEnvelope?.periodType ?: RecurrenceType.MONTHLY)
            val timeZone = TimeZone.currentSystemDefault()
            val now = Clock.System.now()
            val newAllocation = EnvelopeAllocation(
                id = 0,
                envelopeId = toEnvelopeId,
                amount = amount,
                periodStart = periodStart,
                periodEnd = periodEnd,
                incomeId = null,
                createdAt = now
            )
            insertAllocation(newAllocation)
        }

        return transferId
    }

    private fun calculatePeriodDates(date: LocalDate, periodType: RecurrenceType): Pair<LocalDate, LocalDate> {
        return when (periodType) {
            RecurrenceType.MONTHLY -> {
                val start = LocalDate(date.year, date.monthNumber, 1)
                val end = if (date.monthNumber == 12) {
                    LocalDate(date.year + 1, 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                } else {
                    LocalDate(date.year, date.monthNumber + 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                }
                Pair(start, end)
            }
            RecurrenceType.BI_WEEKLY -> {
                val start = date
                val end = LocalDate.fromEpochDays(date.toEpochDays() + 13)
                Pair(start, end)
            }
            RecurrenceType.WEEKLY -> {
                val start = date
                val end = LocalDate.fromEpochDays(date.toEpochDays() + 6)
                Pair(start, end)
            }
            else -> {
                val start = LocalDate(date.year, date.monthNumber, 1)
                val end = if (date.monthNumber == 12) {
                    LocalDate(date.year + 1, 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                } else {
                    LocalDate(date.year, date.monthNumber + 1, 1).let { LocalDate.fromEpochDays(it.toEpochDays() - 1) }
                }
                Pair(start, end)
            }
        }
    }

    override suspend fun deleteTransfer(transfer: EnvelopeTransfer) {
        val existing = envelopeTransferDao.getTransferById(transfer.id)
        existing?.let { envelopeTransferDao.deleteTransfer(it) }
    }

    // Auto-Categorization Rules
    override fun getAllCategorizationRules(): Flow<List<CategorizationRule>> =
        categorizationRuleDao.getAllActiveRules().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRulesForEnvelope(envelopeId: Long): Flow<List<CategorizationRule>> =
        categorizationRuleDao.getRulesForEnvelope(envelopeId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertCategorizationRule(rule: CategorizationRule): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return categorizationRuleDao.insertRule(rule.toEntity(timestamp))
    }

    override suspend fun updateCategorizationRule(rule: CategorizationRule) {
        val existing = categorizationRuleDao.getRuleById(rule.id)
        if (existing != null) {
            categorizationRuleDao.updateRule(rule.toEntity(existing.createdAt))
        } else {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            categorizationRuleDao.updateRule(rule.toEntity(timestamp))
        }
    }

    override suspend fun deleteCategorizationRule(rule: CategorizationRule) {
        val existing = categorizationRuleDao.getRuleById(rule.id)
        existing?.let { categorizationRuleDao.deleteRule(it) }
    }

    override suspend fun applyAutoCategorization(transaction: Transaction): Long? {
        val rules = categorizationRuleDao.getAllActiveRules().first()
        val matchingRule = rules.find { rule ->
            transaction.description.contains(rule.keyword, ignoreCase = true)
        }
        return matchingRule?.envelopeId
    }

    // Analytics
    override suspend fun getEnvelopeSpendingTrend(envelopeId: Long, months: Int): List<MonthlySpending> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val startDate = LocalDate(today.year, today.monthNumber, 1)
        val monthsAgo = startDate.let {
            var date = it
            repeat(months) {
                date = if (date.monthNumber == 1) {
                    LocalDate(date.year - 1, 12, 1)
                } else {
                    LocalDate(date.year, date.monthNumber - 1, 1)
                }
            }
            date
        }

        val transactions = transactionDao.getTransactionsBetween(monthsAgo, today).first()
            .filter { it.envelopeId == envelopeId }

        val monthlyMap = mutableMapOf<String, Double>()
        transactions.forEach { transaction ->
            val monthKey = "${transaction.date.year}-${transaction.date.monthNumber.toString().padStart(2, '0')}"
            val amount = when (transaction.type) {
                TransactionType.BILL_PAYMENT, TransactionType.CREDIT_CARD_PAYMENT -> transaction.amount
                else -> 0.0
            }
            monthlyMap[monthKey] = (monthlyMap[monthKey] ?: 0.0) + amount
        }

        return monthlyMap.map { (month, amount) ->
            MonthlySpending(month, amount)
        }.sortedBy { it.month }
    }

    override suspend fun getTotalSpendingByEnvelope(startDate: LocalDate, endDate: LocalDate): Map<Long, Double> {
        val transactions = transactionDao.getTransactionsBetween(startDate, endDate).first()
            .filter { it.envelopeId != null }

        return transactions.groupBy { it.envelopeId!! }
            .mapValues { (_, trans) ->
                trans.sumOf {
                    when (it.type) {
                        TransactionType.BILL_PAYMENT, TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                        else -> 0.0
                    }
                }
            }
    }
}

