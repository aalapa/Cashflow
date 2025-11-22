package com.cashflow.app.data.repository

import com.cashflow.app.data.model.TransactionType
import com.cashflow.app.domain.model.*
import com.cashflow.app.domain.repository.CashFlowRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDate

/**
 * Firebase sync repository that works alongside Room repository.
 * Handles syncing data to/from Firestore for multi-user access.
 */
class FirebaseSyncRepository(
    private val localRepository: CashFlowRepository
) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    }
    
    private fun getHouseholdPath(): String {
        return "households/${getUserId()}"
    }
    
    // Sync methods - upload local data to Firebase
    suspend fun syncAllToFirebase() {
        val householdPath = getHouseholdPath()
        
        // Sync accounts
        val accounts = localRepository.getAllAccounts().first()
        accounts.forEach { account ->
            db.collection("$householdPath/accounts")
                .document(account.id.toString())
                .set(accountToMap(account))
                .await()
        }
        
        // Sync income
        val incomeList = localRepository.getAllActiveIncome().first()
        incomeList.forEach { income ->
            db.collection("$householdPath/income")
                .document(income.id.toString())
                .set(incomeToMap(income))
                .await()
        }
        
        // Sync bills
        val bills = localRepository.getAllActiveBills().first()
        bills.forEach { bill ->
            db.collection("$householdPath/bills")
                .document(bill.id.toString())
                .set(billToMap(bill))
                .await()
        }
        
        // Sync transactions
        val transactions = localRepository.getAllTransactions().first()
        transactions.forEach { transaction ->
            db.collection("$householdPath/transactions")
                .document(transaction.id.toString())
                .set(transactionToMap(transaction))
                .await()
        }
    }
    
    // Sync methods - download Firebase data to local
    suspend fun syncAllFromFirebase() {
        val householdPath = getHouseholdPath()
        
        // Download accounts
        val accountsSnapshot = db.collection("$householdPath/accounts").get().await()
        accountsSnapshot.documents.forEach { doc ->
            val account = mapToAccount(doc.data)
            localRepository.saveAccount(account)
        }
        
        // Download income
        val incomeSnapshot = db.collection("$householdPath/income").get().await()
        incomeSnapshot.documents.forEach { doc ->
            val income = mapToIncome(doc.data)
            localRepository.saveIncome(income)
        }
        
        // Download bills
        val billsSnapshot = db.collection("$householdPath/bills").get().await()
        billsSnapshot.documents.forEach { doc ->
            val bill = mapToBill(doc.data)
            localRepository.saveBill(bill)
        }
        
        // Download transactions
        val transactionsSnapshot = db.collection("$householdPath/transactions").get().await()
        transactionsSnapshot.documents.forEach { doc ->
            val transaction = mapToTransaction(doc.data)
            localRepository.addTransaction(transaction)
        }
    }
    
    // Real-time listeners for multi-user sync
    fun observeAccounts(): Flow<List<Account>> = flow {
        val householdPath = getHouseholdPath()
        db.collection("$householdPath/accounts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.documents?.let { docs ->
                    val accounts = docs.map { mapToAccount(it.data) }
                    // Update local repository
                    accounts.forEach { account ->
                        kotlinx.coroutines.runBlocking {
                            localRepository.saveAccount(account)
                        }
                    }
                }
            }
    }
    
    // Helper conversion functions
    private fun accountToMap(account: Account): Map<String, Any> {
        return mapOf(
            "id" to account.id,
            "name" to account.name,
            "type" to account.type.name,
            "startingBalance" to account.startingBalance,
            "currentBalance" to account.currentBalance,
            "lastModified" to System.currentTimeMillis()
        )
    }
    
    private fun mapToAccount(data: Map<String, Any>): Account {
        return Account(
            id = (data["id"] as? Long) ?: 0L,
            name = data["name"] as? String ?: "",
            type = AccountType.valueOf(data["type"] as? String ?: "CHECKING"),
            startingBalance = (data["startingBalance"] as? Number)?.toDouble() ?: 0.0,
            currentBalance = (data["currentBalance"] as? Number)?.toDouble() ?: 0.0
        )
    }
    
    private fun incomeToMap(income: Income): Map<String, Any> {
        return mapOf(
            "id" to income.id,
            "name" to income.name,
            "amount" to income.amount,
            "recurrenceType" to income.recurrenceType.name,
            "startDate" to income.startDate.toString(),
            "lastModified" to System.currentTimeMillis()
        )
    }
    
    private fun mapToIncome(data: Map<String, Any>): Income {
        return Income(
            id = (data["id"] as? Long) ?: 0L,
            name = data["name"] as? String ?: "",
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            recurrenceType = RecurrenceType.valueOf(data["recurrenceType"] as? String ?: "MONTHLY"),
            startDate = LocalDate.parse(data["startDate"] as? String ?: "")
        )
    }
    
    private fun billToMap(bill: Bill): Map<String, Any> {
        return mapOf(
            "id" to bill.id,
            "name" to bill.name,
            "amount" to bill.amount,
            "recurrenceType" to bill.recurrenceType.name,
            "dueDay" to bill.dueDay,
            "startDate" to bill.startDate.toString(),
            "endDate" to (bill.endDate?.toString() ?: ""),
            "lastModified" to System.currentTimeMillis()
        )
    }
    
    private fun mapToBill(data: Map<String, Any>): Bill {
        return Bill(
            id = (data["id"] as? Long) ?: 0L,
            name = data["name"] as? String ?: "",
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            recurrenceType = RecurrenceType.valueOf(data["recurrenceType"] as? String ?: "MONTHLY"),
            dueDay = (data["dueDay"] as? Number)?.toInt() ?: 1,
            startDate = LocalDate.parse(data["startDate"] as? String ?: ""),
            endDate = (data["endDate"] as? String)?.takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }
        )
    }
    
    private fun transactionToMap(transaction: Transaction): Map<String, Any> {
        return mapOf(
            "id" to transaction.id,
            "accountId" to transaction.accountId,
            "toAccountId" to (transaction.toAccountId ?: ""),
            "type" to transaction.type.name,
            "amount" to transaction.amount,
            "description" to transaction.description,
            "date" to transaction.date.toString(),
            "relatedIncomeId" to (transaction.relatedIncomeId ?: ""),
            "lastModified" to System.currentTimeMillis()
        )
    }
    
    private fun mapToTransaction(data: Map<String, Any>): Transaction {
        return Transaction(
            id = (data["id"] as? Long) ?: 0L,
            accountId = (data["accountId"] as? Long) ?: 0L,
            toAccountId = (data["toAccountId"] as? String)?.takeIf { it.isNotEmpty() }?.toLongOrNull(),
            type = TransactionType.valueOf(data["type"] as? String ?: "INCOME"),
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            description = data["description"] as? String ?: "",
            date = LocalDate.parse(data["date"] as? String ?: ""),
            relatedIncomeId = (data["relatedIncomeId"] as? String)?.takeIf { it.isNotEmpty() }?.toLongOrNull()
        )
    }
}

