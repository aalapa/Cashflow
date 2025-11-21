package com.cashflow.app.ui.timeline

import com.cashflow.app.domain.model.CashFlowDay
import kotlinx.datetime.LocalDate

data class TimelineState(
    val cashFlowDays: List<CashFlowDay> = emptyList(),
    val selectedTimePeriod: TimePeriod = TimePeriod.DAYS_30,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class TimePeriod(val days: Int) {
    DAYS_30(30),
    DAYS_60(60),
    DAYS_90(90),
    DAYS_180(180),
    DAYS_360(360)
}

sealed class TimelineIntent {
    data class SetTimePeriod(val period: TimePeriod) : TimelineIntent()
    object Refresh : TimelineIntent()
}

