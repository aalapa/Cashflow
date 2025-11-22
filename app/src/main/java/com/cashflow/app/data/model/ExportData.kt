package com.cashflow.app.data.model

import com.cashflow.app.data.entity.*
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Serializable
data class ExportData(
    val version: Int = 1,
    val exportDate: String,
    val accounts: List<SerializableAccount>,
    val income: List<SerializableIncome>,
    val incomeOverrides: List<SerializableIncomeOverride>,
    val bills: List<SerializableBill>,
    val billOverrides: List<SerializableBillOverride>,
    val billPayments: List<SerializableBillPayment>,
    val transactions: List<SerializableTransaction>
)

@Serializable
data class SerializableAccount(
    val id: Long,
    val name: String,
    val type: String,
    val startingBalance: Double,
    val currentBalance: Double
)

@Serializable
data class SerializableIncome(
    val id: Long,
    val name: String,
    val amount: Double,
    val recurrenceType: String,
    val startDate: String,
    val accountId: Long,
    val isActive: Boolean
)

@Serializable
data class SerializableIncomeOverride(
    val id: Long,
    val incomeId: Long,
    val date: String,
    val amount: Double
)

@Serializable
data class SerializableBill(
    val id: Long,
    val name: String,
    val amount: Double,
    val recurrenceType: String,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean
)

@Serializable
data class SerializableBillOverride(
    val id: Long,
    val billId: Long,
    val date: String,
    val amount: Double
)

@Serializable
data class SerializableBillPayment(
    val id: Long,
    val billId: Long,
    val accountId: Long,
    val paymentDate: String,
    val amount: Double,
    val timestamp: String,
    val transactionId: Long?
)

@Serializable
data class SerializableTransaction(
    val id: Long,
    val accountId: Long,
    val type: String,
    val amount: Double,
    val date: String,
    val timestamp: String,
    val description: String,
    val relatedBillId: Long?,
    val relatedIncomeId: Long?,
    val toAccountId: Long?
)

// Extension functions to convert entities to serializable format
fun AccountEntity.toSerializable() = SerializableAccount(
    id = id,
    name = name,
    type = type.name,
    startingBalance = startingBalance,
    currentBalance = currentBalance
)

fun IncomeEntity.toSerializable() = SerializableIncome(
    id = id,
    name = name,
    amount = amount,
    recurrenceType = recurrenceType.name,
    startDate = startDate.toString(),
    accountId = accountId,
    isActive = isActive
)

fun IncomeOverrideEntity.toSerializable() = SerializableIncomeOverride(
    id = id,
    incomeId = incomeId,
    date = date.toString(),
    amount = amount
)

fun BillEntity.toSerializable() = SerializableBill(
    id = id,
    name = name,
    amount = amount,
    recurrenceType = recurrenceType.name,
    startDate = startDate.toString(),
    endDate = endDate?.toString(),
    isActive = isActive
)

fun BillOverrideEntity.toSerializable() = SerializableBillOverride(
    id = id,
    billId = billId,
    date = date.toString(),
    amount = amount
)

fun BillPaymentEntity.toSerializable() = SerializableBillPayment(
    id = id,
    billId = billId,
    accountId = accountId,
    paymentDate = paymentDate.toString(),
    amount = amount,
    timestamp = timestamp.toString(),
    transactionId = transactionId
)

fun TransactionEntity.toSerializable() = SerializableTransaction(
    id = id,
    accountId = accountId,
    type = type.name,
    amount = amount,
    date = date.toString(),
    timestamp = timestamp.toString(),
    description = description,
    relatedBillId = relatedBillId,
    relatedIncomeId = relatedIncomeId,
    toAccountId = toAccountId
)

// Extension functions to convert serializable format back to entities
fun SerializableAccount.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = AccountType.valueOf(type),
    startingBalance = startingBalance,
    currentBalance = currentBalance
)

fun SerializableIncome.toEntity() = IncomeEntity(
    id = id,
    name = name,
    amount = amount,
    recurrenceType = RecurrenceType.valueOf(recurrenceType),
    startDate = LocalDate.parse(startDate),
    accountId = accountId,
    isActive = isActive
)

fun SerializableIncomeOverride.toEntity() = IncomeOverrideEntity(
    id = id,
    incomeId = incomeId,
    date = LocalDate.parse(date),
    amount = amount
)

fun SerializableBill.toEntity() = BillEntity(
    id = id,
    name = name,
    amount = amount,
    recurrenceType = RecurrenceType.valueOf(recurrenceType),
    startDate = LocalDate.parse(startDate),
    endDate = endDate?.let { LocalDate.parse(it) },
    accountId = null,
    isActive = isActive
)

fun SerializableBillOverride.toEntity() = BillOverrideEntity(
    id = id,
    billId = billId,
    date = LocalDate.parse(date),
    amount = amount
)

fun SerializableBillPayment.toEntity() = BillPaymentEntity(
    id = id,
    billId = billId,
    accountId = accountId,
    paymentDate = LocalDate.parse(paymentDate),
    amount = amount,
    timestamp = LocalDateTime.parse(timestamp),
    transactionId = transactionId
)

fun SerializableTransaction.toEntity() = TransactionEntity(
    id = id,
    accountId = accountId,
    type = TransactionType.valueOf(type),
    amount = amount,
    date = LocalDate.parse(date),
    timestamp = LocalDateTime.parse(timestamp),
    description = description,
    relatedBillId = relatedBillId,
    relatedIncomeId = relatedIncomeId,
    toAccountId = toAccountId
)

