package com.piappstudio.a2ui.data.model

data class MerchantInfo(
    val name: String,
    val category: String,
    val contact: String? = null,
    val city: String? = null,
    val country: String? = null,
    val verifiedLatitude: Double? = null,
    val verifiedLongitude: Double? = null,
)

data class Transaction(
    val id: String,
    val accountId: String,
    val merchant: MerchantInfo,
    val amount: Double,
    val currency: String = "USD",
    val dateLabel: String,
    val isoTimestamp: String,
    val status: String,
    val isHighRisk: Boolean = false,
    val disputeEligible: Boolean = true,
    val locationVerified: Boolean = false,
)
