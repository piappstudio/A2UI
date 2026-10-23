package com.piappstudio.a2ui.data.sample

import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.AccountType
import com.piappstudio.a2ui.data.model.Transaction

object SampleBankingData {
    val accounts = listOf(
        Account(
            id = "acc_checking_1",
            name = "Primary Checking",
            accountNumber = "•••• 4892",
            balance = 12450.00,
            currency = "$",
            type = AccountType.CHECKING
        ),
        Account(
            id = "acc_savings_1",
            name = "High-Yield Savings",
            accountNumber = "•••• 8104",
            balance = 45800.50,
            currency = "$",
            type = AccountType.SAVINGS
        ),
        Account(
            id = "acc_investment_1",
            name = "Wealth Management",
            accountNumber = "•••• 3321",
            balance = 128920.00,
            currency = "$",
            type = AccountType.INVESTMENT
        )
    )

    val transactions = listOf(
        Transaction(
            id = "tx_1",
            merchantName = "Whole Foods Market",
            amount = -142.50,
            date = "Today, 2:45 PM",
            category = "Groceries",
            isHighRisk = false,
            accountId = "acc_checking_1"
        ),
        Transaction(
            id = "tx_2",
            merchantName = "Apple Store Online",
            amount = -1299.00,
            date = "Yesterday",
            category = "Electronics",
            isHighRisk = false,
            accountId = "acc_checking_1"
        ),
        Transaction(
            id = "tx_3",
            merchantName = "Int'l Wire Transfer - Zurich AG",
            amount = -5000.00,
            date = "3 days ago",
            category = "Wire Transfer",
            isHighRisk = true,
            accountId = "acc_checking_1"
        ),
        Transaction(
            id = "tx_4",
            merchantName = "Salary Direct Deposit",
            amount = 6500.00,
            date = "Oct 31",
            category = "Income",
            isHighRisk = false,
            accountId = "acc_checking_1"
        ),
        Transaction(
            id = "tx_5",
            merchantName = "Starbucks Reserve",
            amount = -8.75,
            date = "Oct 30",
            category = "Dining",
            isHighRisk = false,
            accountId = "acc_checking_1"
        )
    )
}
