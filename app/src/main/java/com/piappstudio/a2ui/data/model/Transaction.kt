package com.piappstudio.a2ui.data.model

data class Transaction(
    val id: String,
    val merchantName: String,
    val amount: Double,
    val date: String,
    val category: String,
    val isHighRisk: Boolean = false,
    val accountId: String
)
