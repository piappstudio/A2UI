package com.piappstudio.a2ui.data.model

enum class TransferRiskTier {
    LOW,
    VERIFICATION_REQUIRED,
    HIGH_REVIEW,
    REJECTED,
}

enum class TransferStatus {
    DRAFT,
    PENDING_REVIEW,
    PENDING_VERIFICATION,
    AUTHORIZED,
    SUBMITTED,
    COMPLETED,
    FAILED,
    REJECTED,
}

data class TransferQuote(
    val sourceAccountId: String,
    val recipientName: String,
    val recipientAccount: String,
    val amount: Double,
    val currency: String,
    val isInternational: Boolean,
    val fee: Double,
    val exchangeRate: Double?,
    val convertedAmount: Double?,
    val estimatedDelivery: String,
    val riskTier: TransferRiskTier,
    val riskScore: Double,
    val riskExplanation: String,
)

data class TransferRequest(
    val idempotencyKey: String,
    val quote: TransferQuote,
)

data class TransferResult(
    val accepted: Boolean,
    val status: TransferStatus,
    val reference: String?,
    val message: String,
)
