package com.piappstudio.a2ui.data.sample

import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.AccountType
import com.piappstudio.a2ui.data.model.BankingAlert
import com.piappstudio.a2ui.data.model.BankingCustomer
import com.piappstudio.a2ui.data.model.MerchantInfo
import com.piappstudio.a2ui.data.model.PendingApproval
import com.piappstudio.a2ui.data.model.Transaction

object SampleBankingData {
    val customer = BankingCustomer(
        id = "cust_demo_001",
        displayName = "Alex Rivera",
        email = "alex.rivera@example.com",
    )

    val accounts = listOf(
        Account(
            id = "acc_checking_1",
            name = "Primary Checking",
            accountNumber = "4892107733",
            maskedNumber = "•••• 4892",
            balance = 12_450.00,
            type = AccountType.CHECKING,
        ),
        Account(
            id = "acc_savings_1",
            name = "High-Yield Savings",
            accountNumber = "8104992210",
            maskedNumber = "•••• 8104",
            balance = 45_800.50,
            type = AccountType.SAVINGS,
        ),
        Account(
            id = "acc_investment_1",
            name = "Wealth Management",
            accountNumber = "3321884401",
            maskedNumber = "•••• 3321",
            balance = 128_920.00,
            type = AccountType.INVESTMENT,
        ),
    )

    val transactions = listOf(
        Transaction(
            id = "tx_1",
            accountId = "acc_checking_1",
            merchant = MerchantInfo(
                name = "Whole Foods Market",
                category = "Groceries",
                contact = "support@wholefoods.example",
                city = "Austin",
                country = "US",
            ),
            amount = -142.50,
            dateLabel = "Today, 2:45 PM",
            isoTimestamp = "2026-10-23T14:45:00Z",
            status = "Posted",
        ),
        Transaction(
            id = "tx_2",
            accountId = "acc_checking_1",
            merchant = MerchantInfo(
                name = "Apple Store Online",
                category = "Electronics",
                contact = "orders@apple.example",
                city = "Cupertino",
                country = "US",
            ),
            amount = -1_299.00,
            dateLabel = "Yesterday",
            isoTimestamp = "2026-10-22T18:12:00Z",
            status = "Posted",
        ),
        Transaction(
            id = "tx_3",
            accountId = "acc_checking_1",
            merchant = MerchantInfo(
                name = "Zurich AG Wire Desk",
                category = "Wire Transfer",
                contact = "ops@zurichag.example",
                city = "Zurich",
                country = "CH",
                verifiedLatitude = 47.3769,
                verifiedLongitude = 8.5417,
            ),
            amount = -5_000.00,
            dateLabel = "3 days ago",
            isoTimestamp = "2026-10-20T09:04:00Z",
            status = "Posted",
            isHighRisk = true,
            locationVerified = true,
        ),
        Transaction(
            id = "tx_4",
            accountId = "acc_checking_1",
            merchant = MerchantInfo(
                name = "Northwind Payroll",
                category = "Income",
                city = "Chicago",
                country = "US",
            ),
            amount = 6_500.00,
            dateLabel = "Oct 15",
            isoTimestamp = "2026-10-15T08:00:00Z",
            status = "Posted",
            disputeEligible = false,
        ),
        Transaction(
            id = "tx_5",
            accountId = "acc_checking_1",
            merchant = MerchantInfo(
                name = "Unknown POS 8841",
                category = "Uncategorized",
                city = "Unknown",
                country = "US",
            ),
            amount = -187.40,
            dateLabel = "Oct 14",
            isoTimestamp = "2026-10-14T21:18:00Z",
            status = "Posted",
            isHighRisk = true,
        ),
    )

    val alerts = listOf(
        BankingAlert(
            id = "alert_1",
            title = "Unusual merchant activity",
            body = "A $187.40 charge from Unknown POS 8841 is flagged for review. DEMO DATA.",
            severity = "high",
        ),
        BankingAlert(
            id = "alert_2",
            title = "Scheduled maintenance",
            body = "Demo wire window is closed 01:00–03:00 UTC on Sundays.",
            severity = "info",
        ),
    )

    val pendingApprovals = listOf(
        PendingApproval(
            id = "appr_1",
            title = "Credit-limit increase pending",
            body = "A demo credit review is waiting on income documents.",
        ),
    )
}
