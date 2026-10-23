package com.piappstudio.a2ui.a2ui.catalog

import androidx.a2ui.compose.ui.A2uiCatalog

const val TRUSTBANK_CATALOG_ID: String =
    "https://piappstudio.com/a2ui/catalogs/trustbank-banking/v1"

fun trustBankCatalog(): A2uiCatalog = A2uiCatalog(
    catalogId = TRUSTBANK_CATALOG_ID,
    components = listOf(
        DashboardScreenComponent,
        TrustBankHeaderComponent,
        AccountSummaryCardComponent,
        TransactionCardComponent,
        TransferPageComponent,
        TransactionDetailComponent,
    ),
)

object TrustBankEvents {
    const val OPEN_TRANSFER = "openTransfer"
    const val SUBMIT_TRANSFER = "submitTransfer"
    const val EXECUTE_TRANSFER = "executeTransfer"
    const val VERIFY_BIOMETRIC = "verifyBiometric"
    const val NAVIGATE_BACK = "navigateBack"
    const val SELECT_TRANSACTION = "selectTransaction"
}
