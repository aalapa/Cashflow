package com.cashflow.app.data.repository

import com.cashflow.app.data.dao.*
import com.cashflow.app.data.database.CashFlowDatabase
import com.cashflow.app.data.entity.*
import com.cashflow.app.data.model.*
import com.cashflow.app.domain.model.*
import com.cashflow.app.domain.repository.CashFlowRepository
import com.cashflow.app.domain.repository.CategoryPeriodHistory
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
    private val budgetDao: BudgetDao,
    private val budgetCategoryDao: BudgetCategoryDao,
    private val budgetCategoryAllocationDao: BudgetCategoryAllocationDao,
    private val budgetCategoryTransferDao: BudgetCategoryTransferDao,
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

    override suspend fun markBillAsPaid(billId: Long, dueDate: LocalDate, accountId: Long, amount: Double, categoryId: Long?): Long {
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
            categoryId = categoryId
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
        budgetCategoryTransferDao.deleteAllTransfers()
        categorizationRuleDao.deleteAllRules()
        budgetCategoryAllocationDao.deleteAllAllocations()
        budgetCategoryDao.deleteAllCategories()
        budgetDao.deleteAllBudgets()
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
        
        // Get budget/category data
        val budgets = budgetDao.getAllBudgets().first()
        val categories = budgetCategoryDao.getAllCategories().first()
        val categoryAllocations = mutableListOf<BudgetCategoryAllocationEntity>()
        categories.forEach { cat ->
            categoryAllocations.addAll(budgetCategoryAllocationDao.getAllocationsForCategory(cat.id).first())
        }
        val categoryTransfers = budgetCategoryTransferDao.getAllTransfers().first()
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
            budgets = budgets.map { it.toSerializable() },
            categories = categories.map { it.toSerializable() },
            categoryAllocations = categoryAllocations.map { it.toSerializable() },
            categoryTransfers = categoryTransfers.map { it.toSerializable() },
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
                budgetCategoryTransferDao.deleteAllTransfers()
                categorizationRuleDao.deleteAllRules()
                budgetCategoryAllocationDao.deleteAllAllocations()
                budgetCategoryDao.deleteAllCategories()
                budgetDao.deleteAllBudgets()
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
                
                // Import budgets
                exportData.budgets.forEach { budget ->
                    budgetDao.insertBudget(budget.toEntity())
                }
                
                // Import categories
                exportData.categories.forEach { category ->
                    budgetCategoryDao.insertCategory(category.toEntity())
                }
                
                // Import category allocations
                exportData.categoryAllocations.forEach { allocation ->
                    budgetCategoryAllocationDao.insertAllocation(allocation.toEntity())
                }
                
                // Import category transfers
                exportData.categoryTransfers.forEach { transfer ->
                    budgetCategoryTransferDao.insertTransfer(transfer.toEntity())
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
        id, accountId, toAccountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId, categoryId
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id, accountId, toAccountId, type, amount, date, timestamp, description, relatedBillId, relatedIncomeId, categoryId
    )

    private fun BillPaymentEntity.toDomain() = BillPayment(
        id, billId, accountId, paymentDate, amount, timestamp, transactionId
    )

    // Budget conversions
    private fun BudgetEntity.toDomain() = Budget(
        id, name, isDefault, createdAt.toInstant(TimeZone.currentSystemDefault()), isActive
    )

    private fun Budget.toEntity(createdAt: LocalDateTime) = BudgetEntity(
        id, name, isDefault, createdAt, isActive
    )

    // Budget Category conversions
    private fun BudgetCategoryEntity.toDomain() = BudgetCategory(
        id, budgetId, name, 
        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color)), 
        icon, budgetedAmount, periodType, accountId, carryOverEnabled, isActive,
        createdAt.toInstant(TimeZone.currentSystemDefault())
    )

    private fun BudgetCategory.toEntity(createdAt: LocalDateTime) = BudgetCategoryEntity(
        id, budgetId, name, 
        String.format("#%08X", color.value.toLong() and 0xFFFFFFFF), 
        icon, budgetedAmount, periodType, accountId, carryOverEnabled, createdAt, isActive
    )

    private fun BudgetCategoryAllocationEntity.toDomain() = BudgetCategoryAllocation(
        id, categoryId, amount, periodStart, periodEnd, incomeId,
        createdAt.toInstant(TimeZone.currentSystemDefault())
    )

    private fun BudgetCategoryAllocation.toEntity(createdAt: LocalDateTime) = BudgetCategoryAllocationEntity(
        id, categoryId, amount, periodStart, periodEnd, incomeId, createdAt
    )

    private fun BudgetCategoryTransferEntity.toDomain() = BudgetCategoryTransfer(
        id, fromCategoryId, toCategoryId, amount, date, description, timestamp
    )

    private fun BudgetCategoryTransfer.toEntity() = BudgetCategoryTransferEntity(
        id, fromCategoryId, toCategoryId, amount, date, description, timestamp
    )

    private fun CategorizationRuleEntity.toDomain() = CategorizationRule(
        id, categoryId, keyword, isActive
    )

    private fun CategorizationRule.toEntity(createdAt: LocalDateTime) = CategorizationRuleEntity(
        id, categoryId, keyword, isActive, createdAt
    )

    // Budget methods
    override fun getAllActiveBudgets(): Flow<List<Budget>> =
        budgetDao.getAllActiveBudgets().map { entities -> entities.map { it.toDomain() } }

    override fun getAllBudgets(): Flow<List<Budget>> =
        budgetDao.getAllBudgets().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getBudgetById(id: Long): Budget? =
        budgetDao.getBudgetById(id)?.toDomain()

    override suspend fun getDefaultBudget(): Budget? =
        budgetDao.getDefaultBudget()?.toDomain()

    override suspend fun insertBudget(budget: Budget): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return budgetDao.insertBudget(budget.toEntity(timestamp))
    }

    override suspend fun updateBudget(budget: Budget) {
        val existing = budgetDao.getBudgetById(budget.id)
        if (existing != null) {
            budgetDao.updateBudget(budget.toEntity(existing.createdAt))
        } else {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            budgetDao.updateBudget(budget.toEntity(timestamp))
        }
    }

    override suspend fun deleteBudget(budget: Budget) {
        val existing = budgetDao.getBudgetById(budget.id)
        existing?.let { budgetDao.deleteBudget(it) }
    }

    override suspend fun setDefaultBudget(budgetId: Long) {
        budgetDao.clearDefaultBudget()
        val budget = budgetDao.getBudgetById(budgetId)
        budget?.let {
            budgetDao.updateBudget(it.copy(isDefault = true))
        }
    }

    // Budget Category methods
    override fun getAllActiveCategories(): Flow<List<BudgetCategory>> =
        budgetCategoryDao.getAllActiveCategories().map { entities -> entities.map { it.toDomain() } }

    override fun getAllCategories(): Flow<List<BudgetCategory>> =
        budgetCategoryDao.getAllCategories().map { entities -> entities.map { it.toDomain() } }

    override fun getCategoriesForBudget(budgetId: Long): Flow<List<BudgetCategory>> =
        budgetCategoryDao.getCategoriesForBudget(budgetId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Long): BudgetCategory? =
        budgetCategoryDao.getCategoryById(id)?.toDomain()

    override suspend fun insertCategory(category: BudgetCategory): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return budgetCategoryDao.insertCategory(category.toEntity(timestamp))
    }

    override suspend fun updateCategory(category: BudgetCategory) {
        val existing = budgetCategoryDao.getCategoryById(category.id)
        if (existing != null) {
            budgetCategoryDao.updateCategory(category.toEntity(existing.createdAt))
        } else {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            budgetCategoryDao.updateCategory(category.toEntity(timestamp))
        }
    }

    override suspend fun deleteCategory(category: BudgetCategory) {
        val existing = budgetCategoryDao.getCategoryById(category.id)
        existing?.let { budgetCategoryDao.deleteCategory(it) }
    }

    // Budget Category Allocation methods
    override fun getAllocationsForCategory(categoryId: Long): Flow<List<BudgetCategoryAllocation>> =
        budgetCategoryAllocationDao.getAllocationsForCategory(categoryId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAllocationForPeriod(categoryId: Long, date: LocalDate): BudgetCategoryAllocation? =
        budgetCategoryAllocationDao.getAllocationForPeriod(categoryId, date)?.toDomain()

    override suspend fun insertAllocation(allocation: BudgetCategoryAllocation): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return budgetCategoryAllocationDao.insertAllocation(allocation.toEntity(timestamp))
    }

    override suspend fun updateAllocation(allocation: BudgetCategoryAllocation) {
        val existing = budgetCategoryAllocationDao.getAllocationById(allocation.id)
        if (existing != null) {
            budgetCategoryAllocationDao.updateAllocation(allocation.toEntity(existing.createdAt))
        } else {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            budgetCategoryAllocationDao.updateAllocation(allocation.toEntity(timestamp))
        }
    }

    override suspend fun deleteAllocation(allocation: BudgetCategoryAllocation) {
        val existing = budgetCategoryAllocationDao.getAllocationById(allocation.id)
        existing?.let { budgetCategoryAllocationDao.deleteAllocation(it) }
    }

    // Budget Category Balance Calculation
    override suspend fun getCategoryBalance(categoryId: Long, date: LocalDate): Double {
        val allocations = budgetCategoryAllocationDao.getAllocationsInRange(categoryId, LocalDate(2000, 1, 1), date).first()
        val totalAllocated = allocations.sumOf { it.amount }

        val transactions = transactionDao.getTransactionsBetween(LocalDate(2000, 1, 1), date).first()
        val categoryTransactions = transactions.filter { it.categoryId == categoryId }
        val totalSpent = categoryTransactions.sumOf {
            when (it.type) {
                TransactionType.BILL_PAYMENT, TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                else -> 0.0
            }
        }

        return totalAllocated - totalSpent
    }

    override fun getCategoryTransactions(categoryId: Long): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { entities ->
            entities.filter { it.categoryId == categoryId }
                .map { it.toDomain() }
        }

    // Period Management
    override suspend fun resetCategoryPeriod(categoryId: Long, newPeriodStart: LocalDate, carryOverAmount: Double) {
        // Period reset logic - can be implemented later if needed
    }

    override suspend fun getCategoryHistory(categoryId: Long, startDate: LocalDate, endDate: LocalDate): List<CategoryPeriodHistory> {
        val allocations = budgetCategoryAllocationDao.getAllocationsInRange(categoryId, startDate, endDate).first()
        val transactions = transactionDao.getTransactionsBetween(startDate, endDate).first()
            .filter { it.categoryId == categoryId }

        val history = mutableListOf<CategoryPeriodHistory>()

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
                CategoryPeriodHistory(
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
        val category = budgetCategoryDao.getCategoryById(categoryId)
        if (category != null && category.carryOverEnabled) {
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

    // Budget Category Transfers
    override fun getCategoryTransfers(categoryId: Long): Flow<List<BudgetCategoryTransfer>> =
        budgetCategoryTransferDao.getTransfersForCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun transferBetweenCategories(
        fromCategoryId: Long,
        toCategoryId: Long,
        amount: Double,
        date: LocalDate,
        description: String?
    ): Long {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())

        val fromAllocation = getAllocationForPeriod(fromCategoryId, date)
        val toAllocation = getAllocationForPeriod(toCategoryId, date)

        val transfer = BudgetCategoryTransfer(
            id = 0,
            fromCategoryId = fromCategoryId,
            toCategoryId = toCategoryId,
            amount = amount,
            date = date,
            description = description,
            timestamp = timestamp
        )

        val transferId = budgetCategoryTransferDao.insertTransfer(transfer.toEntity())

        // Adjust allocations
        val fromAllocationEntity = fromAllocation?.let {
            budgetCategoryAllocationDao.getAllocationById(it.id)
        }
        val toAllocationEntity = toAllocation?.let {
            budgetCategoryAllocationDao.getAllocationById(it.id)
        }

        if (fromAllocationEntity != null) {
            val updated = fromAllocationEntity.copy(amount = fromAllocationEntity.amount - amount)
            budgetCategoryAllocationDao.updateAllocation(updated)
        }

        if (toAllocationEntity != null) {
            val updated = toAllocationEntity.copy(amount = toAllocationEntity.amount + amount)
            budgetCategoryAllocationDao.updateAllocation(updated)
        } else {
            val toCategory = budgetCategoryDao.getCategoryById(toCategoryId)
            val (periodStart, periodEnd) = calculatePeriodDates(date, toCategory?.periodType ?: RecurrenceType.MONTHLY)
            val timeZone = TimeZone.currentSystemDefault()
            val now = Clock.System.now()
            val newAllocation = BudgetCategoryAllocation(
                id = 0,
                categoryId = toCategoryId,
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

    override suspend fun deleteTransfer(transfer: BudgetCategoryTransfer) {
        val existing = budgetCategoryTransferDao.getTransferById(transfer.id)
        existing?.let { budgetCategoryTransferDao.deleteTransfer(it) }
    }

    // Auto-Categorization Rules
    override fun getAllCategorizationRules(): Flow<List<CategorizationRule>> =
        categorizationRuleDao.getAllActiveRules().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRulesForCategory(categoryId: Long): Flow<List<CategorizationRule>> =
        categorizationRuleDao.getRulesForCategory(categoryId).map { entities ->
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
        return matchingRule?.categoryId
    }

    // Analytics
    override suspend fun getCategorySpendingTrend(categoryId: Long, months: Int): List<MonthlySpending> {
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
            .filter { it.categoryId == categoryId }

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

    override suspend fun getTotalSpendingByCategory(startDate: LocalDate, endDate: LocalDate): Map<Long, Double> {
        val transactions = transactionDao.getTransactionsBetween(startDate, endDate).first()
            .filter { it.categoryId != null }

        return transactions.groupBy { it.categoryId!! }
            .mapValues { (_, trans) ->
                trans.sumOf {
                    when (it.type) {
                        TransactionType.BILL_PAYMENT, TransactionType.CREDIT_CARD_PAYMENT -> it.amount
                        else -> 0.0
                    }
                }
            }
    }

    // Default Budget Setup
    override suspend fun initializeDefaultBudgetIfNeeded() {
        val existingDefault = budgetDao.getDefaultBudget()
        if (existingDefault == null) {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
            
            // Create default budget
            val defaultBudget = BudgetEntity(
                id = 0,
                name = "My Budget",
                isDefault = true,
                createdAt = timestamp,
                isActive = true
            )
            val budgetId = budgetDao.insertBudget(defaultBudget)
            
            // Create default categories
            val defaultCategories = listOf(
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Groceries",
                    color = "#4CAF50",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Transportation",
                    color = "#2196F3",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Entertainment",
                    color = "#9C27B0",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Dining Out",
                    color = "#FF9800",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Utilities",
                    color = "#F44336",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Shopping",
                    color = "#E91E63",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Healthcare",
                    color = "#00BCD4",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                ),
                BudgetCategoryEntity(
                    id = 0,
                    budgetId = budgetId,
                    name = "Other",
                    color = "#9E9E9E",
                    icon = null,
                    budgetedAmount = 0.0,
                    periodType = RecurrenceType.MONTHLY,
                    accountId = null,
                    carryOverEnabled = false,
                    createdAt = timestamp,
                    isActive = true
                )
            )
            
            defaultCategories.forEach { category ->
                budgetCategoryDao.insertCategory(category)
            }
        }
    }
}

