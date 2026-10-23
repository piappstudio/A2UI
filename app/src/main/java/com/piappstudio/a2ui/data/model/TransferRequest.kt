package com.piappstudio.a2ui.data.model

data class TransferRequest(
    val recipientName: String,
    val accountNumber: String,
    val amount: Double,
    val currency: String = "USD",
    val isInternational: Boolean = false,
    val riskScore: Double = 0.1, // 0.0 to 1.0
    val note: String = ""
)
