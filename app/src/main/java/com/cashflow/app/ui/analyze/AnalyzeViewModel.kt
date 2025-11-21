package com.cashflow.app.ui.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashflow.app.domain.model.Bill
import com.cashflow.app.domain.repository.CashFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class CreditCardRecommendation(
    val bill: Bill,
    val originalAmount: Double,
    val recommendedAmount: Double,
    val savings: Double
)

data class AnalyzeState(
    val isAnalyzing: Boolean = false,
    val isComplete: Boolean = false,
    val recommendations: List<CreditCardRecommendation> = emptyList(),
    val totalSavings: Double = 0.0,
    val error: String? = null,
    val analysisTimePeriodDays: Int = 90
)

sealed class AnalyzeIntent {
    object StartAnalysis : AnalyzeIntent()
    object Reset : AnalyzeIntent()
    data class SetTimePeriod(val days: Int) : AnalyzeIntent()
}

class AnalyzeViewModel(
    private val repository: CashFlowRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyzeState())
    val state: StateFlow<AnalyzeState> = _state.asStateFlow()

    fun handleIntent(intent: AnalyzeIntent) {
        when (intent) {
            is AnalyzeIntent.StartAnalysis -> startAnalysis()
            is AnalyzeIntent.Reset -> {
                _state.update { AnalyzeState(analysisTimePeriodDays = it.analysisTimePeriodDays) }
            }
            is AnalyzeIntent.SetTimePeriod -> {
                _state.update { it.copy(analysisTimePeriodDays = intent.days) }
            }
        }
    }

    private fun startAnalysis() {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, error = null, isComplete = false) }

            try {
                // Get all accounts, bills, and income
                val accounts = repository.getAllAccounts().first()
                val allBills = repository.getAllActiveBills().first()
                
                // Filter to only credit card bills (bills with negative starting balance accounts)
                // For now, we'll identify credit card bills by checking if the bill name contains "card" or "credit"
                // or if it's associated with a credit card account type
                val creditCardBills = allBills.filter { bill ->
                    bill.name.contains("card", ignoreCase = true) || 
                    bill.name.contains("credit", ignoreCase = true)
                }

                if (creditCardBills.isEmpty()) {
                    _state.update { 
                        it.copy(
                            isAnalyzing = false, 
                            error = "No credit card bills found. Please add credit card bills to analyze."
                        ) 
                    }
                    return@launch
                }

                // Calculate date range
                val timeZone = TimeZone.currentSystemDefault()
                val today = Clock.System.now().toLocalDateTime(timeZone).date
                val endDate = LocalDate.fromEpochDays(today.toEpochDays() + _state.value.analysisTimePeriodDays)

                // Start with original amounts
                var currentAmounts = creditCardBills.associate { it.id to it.amount }.toMutableMap()
                var hasNegativeBalance = true
                val reductionStep = 5.0

                // Iteratively reduce amounts until no negative cash flow
                // Use round-robin: reduce one card at a time, cycling through all cards
                var iterations = 0
                val maxIterations = 1000 // Prevent infinite loop (1000 reductions across all cards)
                var currentCardIndex = 0

                while (hasNegativeBalance && iterations < maxIterations) {
                    // Create temporary bills with reduced amounts
                    val modifiedBills = allBills.map { bill ->
                        if (creditCardBills.any { it.id == bill.id }) {
                            val newAmount = currentAmounts[bill.id] ?: bill.amount
                            bill.copy(amount = newAmount.coerceAtLeast(0.0))
                        } else {
                            bill
                        }
                    }

                    // Calculate cash flow with modified bills
                    val cashFlowDays = calculateCashFlowWithBills(
                        startDate = today,
                        endDate = endDate,
                        accounts = accounts,
                        modifiedBills = modifiedBills
                    )

                    // Check if any day has negative balance
                    hasNegativeBalance = cashFlowDays.any { it.isNegative }

                    if (hasNegativeBalance) {
                        // Reduce only one credit card bill by $5 in round-robin fashion
                        val cardToReduce = creditCardBills[currentCardIndex]
                        val currentAmount = currentAmounts[cardToReduce.id] ?: cardToReduce.amount
                        
                        // Only reduce if there's still amount left
                        if (currentAmount > 0) {
                            currentAmounts[cardToReduce.id] = (currentAmount - reductionStep).coerceAtLeast(0.0)
                        }
                        
                        // Move to next card (round-robin)
                        currentCardIndex = (currentCardIndex + 1) % creditCardBills.size
                        iterations++
                    }
                }

                // If we still have negative balance after max iterations, notify user
                if (hasNegativeBalance) {
                    _state.update { 
                        it.copy(
                            isAnalyzing = false,
                            error = "Unable to find a combination that avoids negative cash flow. Consider reducing expenses or increasing income."
                        ) 
                    }
                    return@launch
                }

                // Create recommendations
                val recommendations = creditCardBills.map { bill ->
                    val recommendedAmount = currentAmounts[bill.id] ?: bill.amount
                    CreditCardRecommendation(
                        bill = bill,
                        originalAmount = bill.amount,
                        recommendedAmount = recommendedAmount,
                        savings = bill.amount - recommendedAmount
                    )
                }

                val totalSavings = recommendations.sumOf { it.savings }

                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        isComplete = true,
                        recommendations = recommendations,
                        totalSavings = totalSavings
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        error = e.message ?: "An error occurred during analysis"
                    )
                }
            }
        }
    }

    private suspend fun calculateCashFlowWithBills(
        startDate: LocalDate,
        endDate: LocalDate,
        accounts: List<com.cashflow.app.domain.model.Account>,
        modifiedBills: List<Bill>
    ): List<com.cashflow.app.domain.model.CashFlowDay> {
        // This is a simplified version - we need to replicate the cash flow calculation
        // but with our modified bills
        
        // For now, we'll use the repository's calculation but we need to pass modified bills
        // Since the repository doesn't support this directly, we'll need to temporarily update bills
        // or implement a local calculation
        
        // Let's implement a simplified local calculation
        val income = repository.getAllActiveIncome().first()
        val transactions = repository.getTransactionsBetween(startDate, endDate).first()
        
        var currentBalance = accounts.sumOf { it.currentBalance }
        val cashFlowDays = mutableListOf<com.cashflow.app.domain.model.CashFlowDay>()
        
        var currentDate = startDate
        while (currentDate <= endDate) {
            var dayBalance = currentBalance
            
            // Process transactions
            val dayTransactions = transactions.filter { it.date == currentDate }
            dayTransactions.forEach { transaction ->
                when (transaction.type) {
                    com.cashflow.app.data.model.TransactionType.INCOME -> dayBalance += transaction.amount
                    com.cashflow.app.data.model.TransactionType.BILL_PAYMENT,
                    com.cashflow.app.data.model.TransactionType.CREDIT_CARD_PAYMENT -> dayBalance -= transaction.amount
                    com.cashflow.app.data.model.TransactionType.MANUAL_ADJUSTMENT -> dayBalance += transaction.amount
                }
            }
            
            // Process scheduled income
            income.forEach { inc ->
                if (shouldOccurOnDate(inc.startDate, null, inc.recurrenceType, currentDate)) {
                    // Check if not already received
                    val alreadyReceived = transactions.any { 
                        it.type == com.cashflow.app.data.model.TransactionType.INCOME && 
                        it.relatedIncomeId == inc.id && 
                        it.date == currentDate 
                    }
                    if (!alreadyReceived) {
                        dayBalance += inc.amount
                    }
                }
            }
            
            // Process scheduled bills (with modified amounts)
            modifiedBills.forEach { bill ->
                if (shouldOccurOnDate(bill.startDate, bill.endDate, bill.recurrenceType, currentDate)) {
                    // Check if not already paid
                    val alreadyPaid = repository.isBillPaid(bill.id, currentDate)
                    if (!alreadyPaid) {
                        dayBalance -= bill.amount
                    }
                }
            }
            
            cashFlowDays.add(
                com.cashflow.app.domain.model.CashFlowDay(
                    date = currentDate,
                    balance = dayBalance,
                    income = emptyList(),
                    bills = emptyList(),
                    isNegative = dayBalance < 0,
                    isWarning = dayBalance < 100 && dayBalance >= 0
                )
            )
            
            currentBalance = dayBalance
            currentDate = LocalDate.fromEpochDays(currentDate.toEpochDays() + 1)
        }
        
        return cashFlowDays
    }

    private fun shouldOccurOnDate(
        startDate: LocalDate,
        endDate: LocalDate?,
        recurrenceType: com.cashflow.app.data.model.RecurrenceType,
        checkDate: LocalDate
    ): Boolean {
        if (checkDate < startDate) return false
        if (endDate != null && checkDate > endDate) return false

        return when (recurrenceType) {
            com.cashflow.app.data.model.RecurrenceType.BI_WEEKLY -> {
                val daysBetween = (checkDate.toEpochDays() - startDate.toEpochDays())
                daysBetween >= 0 && daysBetween % 14 == 0
            }
            com.cashflow.app.data.model.RecurrenceType.WEEKLY -> {
                val daysBetween = (checkDate.toEpochDays() - startDate.toEpochDays())
                daysBetween >= 0 && daysBetween % 7 == 0
            }
            com.cashflow.app.data.model.RecurrenceType.MONTHLY -> {
                checkDate.dayOfMonth == startDate.dayOfMonth && checkDate >= startDate
            }
            com.cashflow.app.data.model.RecurrenceType.CUSTOM -> false
        }
    }
}

