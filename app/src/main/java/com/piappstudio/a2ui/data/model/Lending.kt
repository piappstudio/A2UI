package com.piappstudio.a2ui.data.model

enum class LendingProduct {
    PERSONAL_LOAN,
    CREDIT_LIMIT_INCREASE,
}

data class EmiEstimate(
    val principal: Double,
    val annualRatePercent: Double,
    val tenureMonths: Int,
    val monthlyPayment: Double,
    val totalPayable: Double,
    val isIllustrative: Boolean = true,
)

data class EligibilityDecision(
    val eligible: Boolean,
    val product: LendingProduct,
    val approvedAmount: Double?,
    val annualRatePercent: Double?,
    val message: String,
)

data class LoanApplication(
    val idempotencyKey: String,
    val product: LendingProduct,
    val amount: Double,
    val tenureMonths: Int,
    val annualIncome: Double,
    val employmentStatus: String,
    val consentGiven: Boolean,
    val documentName: String?,
)
