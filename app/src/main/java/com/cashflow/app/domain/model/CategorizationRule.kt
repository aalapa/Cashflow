package com.cashflow.app.domain.model

data class CategorizationRule(
    val id: Long = 0,
    val categoryId: Long,
    val keyword: String, // Transaction description must contain this keyword
    val isActive: Boolean = true
)
