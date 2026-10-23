package com.piappstudio.a2ui.a2ui.agent

import androidx.a2ui.model.protocol.A2uiComponentPayload
import androidx.a2ui.model.protocol.A2uiCreateSurfaceMessage
import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import androidx.a2ui.model.protocol.A2uiUpdateComponentsMessage
import androidx.a2ui.model.protocol.A2uiUpdateDataModelMessage
import com.piappstudio.a2ui.a2ui.catalog.TRUSTBANK_CATALOG_ID
import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.Transaction
import java.util.Locale

object BankingSurfaceAgent {
    const val SURFACE_ID_DASHBOARD = "checking_dashboard"
    const val SURFACE_ID_TRANSFER = "transfer_page"
    const val SURFACE_ID_TRANSACTION_DETAIL = "transaction_detail"

    fun createDashboardSurface(): A2uiServerToClientMessage = A2uiCreateSurfaceMessage(
        surfaceId = SURFACE_ID_DASHBOARD,
        catalogId = TRUSTBANK_CATALOG_ID,
        theme = emptyMap(),
        shouldSendDataModel = true,
    )

    fun createTransferSurface(): A2uiServerToClientMessage = A2uiCreateSurfaceMessage(
        surfaceId = SURFACE_ID_TRANSFER,
        catalogId = TRUSTBANK_CATALOG_ID,
        theme = emptyMap(),
        shouldSendDataModel = true,
    )

    fun createTransactionDetailSurface(): A2uiServerToClientMessage = A2uiCreateSurfaceMessage(
        surfaceId = SURFACE_ID_TRANSACTION_DETAIL,
        catalogId = TRUSTBANK_CATALOG_ID,
        theme = emptyMap(),
        shouldSendDataModel = true,
    )

    fun defineDashboardInterface(): A2uiServerToClientMessage = A2uiUpdateComponentsMessage(
        surfaceId = SURFACE_ID_DASHBOARD,
        components = listOf(
            A2uiComponentPayload(
                id = "root",
                type = "DashboardScreen",
                properties = mapOf(
                    "header" to "header",
                    "accounts" to mapOf("componentId" to "account_card", "path" to "/accounts"),
                    "transactions" to mapOf("componentId" to "transaction_card", "path" to "/transactions"),
                ),
            ),
            A2uiComponentPayload(
                id = "header",
                type = "TrustBankHeader",
                properties = mapOf(
                    "badge" to "🛡️ A2UI TRUSTBANK",
                    "title" to "Secure Wealth Management",
                    "subtitle" to "Instant secure transfers with AI-powered risk protection.",
                ),
            ),
            A2uiComponentPayload(
                id = "account_card",
                type = "AccountSummaryCard",
                properties = mapOf(
                    "accountId" to mapOf("path" to "id"),
                    "name" to mapOf("path" to "name"),
                    "accountNumber" to mapOf("path" to "accountNumber"),
                    "balance" to mapOf("path" to "balance"),
                    "currency" to mapOf("path" to "currency"),
                    "type" to mapOf("path" to "type"),
                ),
            ),
            A2uiComponentPayload(
                id = "transaction_card",
                type = "TransactionCard",
                properties = mapOf(
                    "transactionId" to mapOf("path" to "id"),
                    "merchantName" to mapOf("path" to "merchantName"),
                    "amount" to mapOf("path" to "amount"),
                    "date" to mapOf("path" to "date"),
                    "category" to mapOf("path" to "category"),
                    "isHighRisk" to mapOf("path" to "isHighRisk"),
                    "selectEvent" to "selectTransaction",
                ),
            ),
        ),
    )

    fun defineTransferInterface(): A2uiServerToClientMessage = A2uiUpdateComponentsMessage(
        surfaceId = SURFACE_ID_TRANSFER,
        components = listOf(
            A2uiComponentPayload(
                id = "root",
                type = "TransferPage",
                properties = mapOf(
                    "isVerification" to mapOf("path" to "/ui/isVerification"),
                    "recipientName" to mapOf("path" to "/draft/recipientName"),
                    "amount" to mapOf("path" to "/draft/amount"),
                    "isInternational" to mapOf("path" to "/draft/isInternational"),
                    "riskScore" to mapOf("path" to "/verification/riskScore"),
                    "isHighRisk" to mapOf("path" to "/verification/isHighRisk"),
                    "submitEvent" to "submitTransfer",
                    "verifyEvent" to "verifyBiometric",
                    "backEvent" to "navigateBack",
                ),
            ),
        ),
    )

    fun defineTransactionDetailInterface(): A2uiServerToClientMessage = A2uiUpdateComponentsMessage(
        surfaceId = SURFACE_ID_TRANSACTION_DETAIL,
        components = listOf(
            A2uiComponentPayload(
                id = "root",
                type = "TransactionDetail",
                properties = mapOf(
                    "transactionId" to mapOf("path" to "/selectedTransaction/id"),
                    "merchantName" to mapOf("path" to "/selectedTransaction/merchantName"),
                    "amount" to mapOf("path" to "/selectedTransaction/amount"),
                    "date" to mapOf("path" to "/selectedTransaction/date"),
                    "category" to mapOf("path" to "/selectedTransaction/category"),
                    "isHighRisk" to mapOf("path" to "/selectedTransaction/isHighRisk"),
                    "backEvent" to "navigateBack",
                ),
            ),
        ),
    )

    fun updateDashboardDataModel(
        accounts: List<Account>,
        transactions: List<Transaction>,
    ): A2uiServerToClientMessage = A2uiUpdateDataModelMessage(
        surfaceId = SURFACE_ID_DASHBOARD,
        path = "/",
        value = mapOf(
            "accounts" to accounts.map { acc ->
                mapOf(
                    "id" to acc.id,
                    "name" to acc.name,
                    "accountNumber" to acc.accountNumber,
                    "balance" to String.format(Locale.US, "%.2f", acc.balance),
                    "currency" to acc.currency,
                    "type" to acc.type.name,
                )
            },
            "transactions" to transactions.map { tx ->
                mapOf(
                    "id" to tx.id,
                    "merchantName" to tx.merchantName,
                    "amount" to (if (tx.amount < 0) "-$${String.format(Locale.US, "%.2f", -tx.amount)}" else "+$${String.format(Locale.US, "%.2f", tx.amount)}"),
                    "date" to tx.date,
                    "category" to tx.category,
                    "isHighRisk" to tx.isHighRisk,
                )
            },
        ),
    )

    fun updateTransferDataModel(
        draftRecipient: String,
        draftAmount: String,
        isInternational: Boolean,
        isVerification: Boolean,
        riskScore: Double,
        isHighRisk: Boolean,
    ): A2uiServerToClientMessage = A2uiUpdateDataModelMessage(
        surfaceId = SURFACE_ID_TRANSFER,
        path = "/",
        value = mapOf(
            "ui" to mapOf(
                "isVerification" to isVerification,
            ),
            "draft" to mapOf(
                "recipientName" to draftRecipient,
                "amount" to draftAmount,
                "isInternational" to isInternational,
            ),
            "verification" to mapOf(
                "riskScore" to String.format(Locale.US, "%.2f", riskScore),
                "isHighRisk" to isHighRisk,
            ),
        ),
    )

    fun updateTransactionDetailDataModel(
        transaction: Transaction?,
    ): A2uiServerToClientMessage = A2uiUpdateDataModelMessage(
        surfaceId = SURFACE_ID_TRANSACTION_DETAIL,
        path = "/",
        value = mapOf(
            "selectedTransaction" to if (transaction != null) {
                mapOf(
                    "id" to transaction.id,
                    "merchantName" to transaction.merchantName,
                    "amount" to (if (transaction.amount < 0) "-$${String.format(Locale.US, "%.2f", -transaction.amount)}" else "+$${String.format(Locale.US, "%.2f", transaction.amount)}"),
                    "date" to transaction.date,
                    "category" to transaction.category,
                    "isHighRisk" to transaction.isHighRisk,
                )
            } else {
                emptyMap<String, Any>()
            }
        ),
    )
}
