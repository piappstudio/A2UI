package com.piappstudio.a2ui.data.model

data class BankingCustomer(
    val id: String,
    val displayName: String,
    val email: String,
)

data class BankingAlert(
    val id: String,
    val title: String,
    val body: String,
    val severity: String,
)

data class PendingApproval(
    val id: String,
    val title: String,
    val body: String,
)
