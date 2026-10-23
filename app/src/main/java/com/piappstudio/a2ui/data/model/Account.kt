package com.piappstudio.a2ui.data.model

enum class AccountType {
    CHECKING, SAVINGS, INVESTMENT
}

data class Account(
    val id: String,
    val name: String,
    val accountNumber: String,
    val balance: Double,
    val currency: String = "$",
    val type: AccountType
)
