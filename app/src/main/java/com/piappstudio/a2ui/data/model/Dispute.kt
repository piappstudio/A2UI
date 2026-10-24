package com.piappstudio.a2ui.data.model

enum class DisputeOutcome {
    SUBMITTED,
    ADDITIONAL_INFORMATION_REQUIRED,
    NOT_ELIGIBLE,
}

data class DisputeRequest(
    val idempotencyKey: String,
    val transactionId: String,
    val customerId: String,
    val reason: String,
    val details: String,
    val confirmed: Boolean,
)

data class DisputeResult(
    val outcome: DisputeOutcome,
    val reference: String?,
    val message: String,
)
