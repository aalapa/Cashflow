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
    val transactions: List<SerializableTransaction>,
    val envelopes: List<SerializableEnvelope> = emptyList(),
    val envelopeAllocations: List<SerializableEnvelopeAllocation> = emptyList(),
    val envelopeTransfers: List<SerializableEnvelopeTransfer> = emptyList(),
    val categorizationRules: List<SerializableCategorizationRule> = emptyList()
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
    val toAccountId: Long?,
    val envelopeId: Long? = null
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
    toAccountId = toAccountId,
    envelopeId = envelopeId
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
    toAccountId = toAccountId,
    envelopeId = envelopeId
)

@Serializable
data class SerializableEnvelope(
    val id: Long,
    val name: String,
    val color: String,
    val icon: String?,
    val budgetedAmount: Double,
    val periodType: String,
    val accountId: Long?,
    val carryOverEnabled: Boolean,
    val isActive: Boolean,
    val createdAt: String
)

@Serializable
data class SerializableEnvelopeAllocation(
    val id: Long,
    val envelopeId: Long,
    val amount: Double,
    val periodStart: String,
    val periodEnd: String,
    val incomeId: Long?,
    val createdAt: String
)

@Serializable
data class SerializableEnvelopeTransfer(
    val id: Long,
    val fromEnvelopeId: Long,
    val toEnvelopeId: Long,
    val amount: Double,
    val date: String,
    val description: String?,
    val timestamp: String
)

@Serializable
data class SerializableCategorizationRule(
    val id: Long,
    val envelopeId: Long,
    val keyword: String,
    val isActive: Boolean,
    val createdAt: String
)

// Extension functions for envelope serialization
fun EnvelopeEntity.toSerializable() = SerializableEnvelope(
    id = id,
    name = name,
    color = color,
    icon = icon,
    budgetedAmount = budgetedAmount,
    periodType = periodType.name,
    accountId = accountId,
    carryOverEnabled = carryOverEnabled,
    isActive = isActive,
    createdAt = createdAt.toString()
)

fun EnvelopeAllocationEntity.toSerializable() = SerializableEnvelopeAllocation(
    id = id,
    envelopeId = envelopeId,
    amount = amount,
    periodStart = periodStart.toString(),
    periodEnd = periodEnd.toString(),
    incomeId = incomeId,
    createdAt = createdAt.toString()
)

fun EnvelopeTransferEntity.toSerializable() = SerializableEnvelopeTransfer(
    id = id,
    fromEnvelopeId = fromEnvelopeId,
    toEnvelopeId = toEnvelopeId,
    amount = amount,
    date = date.toString(),
    description = description,
    timestamp = timestamp.toString()
)

fun CategorizationRuleEntity.toSerializable() = SerializableCategorizationRule(
    id = id,
    envelopeId = envelopeId,
    keyword = keyword,
    isActive = isActive,
    createdAt = createdAt.toString()
)

fun SerializableEnvelope.toEntity() = EnvelopeEntity(
    id = id,
    name = name,
    color = color,
    icon = icon,
    budgetedAmount = budgetedAmount,
    periodType = RecurrenceType.valueOf(periodType),
    accountId = accountId,
    carryOverEnabled = carryOverEnabled,
    createdAt = LocalDateTime.parse(createdAt),
    isActive = isActive
)

fun SerializableEnvelopeAllocation.toEntity() = EnvelopeAllocationEntity(
    id = id,
    envelopeId = envelopeId,
    amount = amount,
    periodStart = LocalDate.parse(periodStart),
    periodEnd = LocalDate.parse(periodEnd),
    incomeId = incomeId,
    createdAt = LocalDateTime.parse(createdAt)
)

fun SerializableEnvelopeTransfer.toEntity() = EnvelopeTransferEntity(
    id = id,
    fromEnvelopeId = fromEnvelopeId,
    toEnvelopeId = toEnvelopeId,
    amount = amount,
    date = LocalDate.parse(date),
    description = description,
    timestamp = LocalDateTime.parse(timestamp)
)

fun SerializableCategorizationRule.toEntity() = CategorizationRuleEntity(
    id = id,
    envelopeId = envelopeId,
    keyword = keyword,
    isActive = isActive,
    createdAt = LocalDateTime.parse(createdAt)
)

