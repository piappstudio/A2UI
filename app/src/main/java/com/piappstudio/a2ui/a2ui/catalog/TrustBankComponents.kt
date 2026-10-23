package com.piappstudio.a2ui.a2ui.catalog

import androidx.a2ui.compose.runtime.A2uiComponentProperties
import androidx.a2ui.compose.runtime.A2uiComponentScope
import androidx.a2ui.compose.runtime.A2uiProperty
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.piappstudio.a2ui.a2ui.renderer.A2uiSlot

private fun dispatchTypedEvent(
    scope: A2uiComponentScope,
    eventName: String,
    context: Map<String, Any?> = emptyMap()
) {
    scope.dispatchAction(
        mapOf(
            "event" to mapOf(
                "name" to eventName,
                "context" to context,
            ),
        ),
    )
}

// 1. Dashboard Screen Component
object DashboardScreenComponent : A2uiComponent {
    private val headerProp = A2uiProperty.componentId("header", true, "Header component id")
    private val accountsProp = A2uiProperty.childList("accounts", true, "Account summary cards")
    private val transactionsProp = A2uiProperty.childList("transactions", true, "Recent transactions list")

    override val name: String = "DashboardScreen"
    override val description: String = "A2UI TrustBank dashboard with accounts and transactions."
    override val properties = listOf(headerProp, accountsProp, transactionsProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val headerId = properties[headerProp]
        val accounts = properties.bindChildReferences(accountsProp)
        val transactions = properties.bindChildReferences(transactionsProp)

        Box(modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    A2uiSlot(headerId)
                }
                item {
                    Text(
                        text = "Accounts & Wealth",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                item {
                    when {
                        accounts == null -> {
                            Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                Text("Loading accounts...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        accounts.isEmpty() -> {
                            Text("No accounts found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        else -> {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(accounts.size) { index ->
                                    A2uiSlot(accounts[index])
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                when {
                    transactions == null -> {
                        item {
                            Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                Text("Loading transactions...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    transactions.isEmpty() -> {
                        item {
                            Text("No recent transactions.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    else -> {
                        items(transactions.size) { index ->
                            A2uiSlot(transactions[index])
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// 2. TrustBank Header Component
object TrustBankHeaderComponent : A2uiComponent {
    private val badgeProp = A2uiProperty.string("badge", true, "Badge text")
    private val titleProp = A2uiProperty.string("title", true, "Title text")
    private val subtitleProp = A2uiProperty.string("subtitle", true, "Subtitle text")

    override val name: String = "TrustBankHeader"
    override val description: String = "Header banner for TrustBank."
    override val properties = listOf(badgeProp, titleProp, subtitleProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val badge = properties[badgeProp] ?: "🛡️ A2UI TRUSTBANK"
        val title = properties[titleProp] ?: "Secure Wealth Management"
        val subtitle = properties[subtitleProp] ?: "Instant transfers with AI-powered risk protection."

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// 3. Account Summary Card Component
object AccountSummaryCardComponent : A2uiComponent {
    private val accountIdProp = A2uiProperty.dynamicString("accountId", true, "Account ID")
    private val nameProp = A2uiProperty.dynamicString("name", true, "Account Name")
    private val accountNumberProp = A2uiProperty.dynamicString("accountNumber", true, "Account Number")
    val balanceProp = A2uiProperty.dynamicString("balance", true, "Account Balance")
    private val currencyProp = A2uiProperty.dynamicString("currency", true, "Currency symbol")
    private val typeProp = A2uiProperty.dynamicString("type", true, "Account Type")

    override val name: String = "AccountSummaryCard"
    override val description: String = "Card displaying account balance and details."
    override val properties = listOf(accountIdProp, nameProp, accountNumberProp, balanceProp, currencyProp, typeProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val name = properties.bind(nameProp) ?: "Account"
        val number = properties.bind(accountNumberProp) ?: "•••• 0000"
        val balance = properties.bind(balanceProp) ?: "0.00"
        val currency = properties.bind(currencyProp) ?: "$"

        Card(
            modifier = modifier.width(260.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        imageVector = Icons.Rounded.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    )
                }
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "$currency$balance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// 4. Transaction Card Component
object TransactionCardComponent : A2uiComponent {
    private val transactionIdProp = A2uiProperty.dynamicString("transactionId", true, "Transaction ID")
    private val merchantNameProp = A2uiProperty.dynamicString("merchantName", true, "Merchant Name")
    private val amountProp = A2uiProperty.dynamicString("amount", true, "Amount")
    private val dateProp = A2uiProperty.dynamicString("date", true, "Date")
    private val categoryProp = A2uiProperty.dynamicString("category", true, "Category")
    private val isHighRiskProp = A2uiProperty.dynamicBoolean("isHighRisk", false, "Is High Risk")
    private val selectEventProp = A2uiProperty.string("selectEvent", false, "Select event")

    override val name: String = "TransactionCard"
    override val description: String = "Displays a transaction row."
    override val properties = listOf(transactionIdProp, merchantNameProp, amountProp, dateProp, categoryProp, isHighRiskProp, selectEventProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val txId = properties.bind(transactionIdProp) ?: ""
        val merchant = properties.bind(merchantNameProp) ?: "Merchant"
        val amount = properties.bind(amountProp) ?: "$0.00"
        val date = properties.bind(dateProp) ?: "Today"
        val category = properties.bind(categoryProp) ?: "General"
        val isHighRisk = properties.bind(isHighRiskProp) ?: false
        val selectEvent = properties[selectEventProp] ?: TrustBankEvents.SELECT_TRANSACTION

        val scope = this

        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    dispatchTypedEvent(scope, selectEvent, mapOf("transactionId" to txId))
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isHighRisk) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Icon(
                            imageVector = if (isHighRisk) Icons.Rounded.Warning else Icons.Rounded.Security,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp).size(24.dp),
                            tint = if (isHighRisk) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = merchant,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (amount.startsWith("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

// 5. Transfer Page Component (Full Screen Transfer & Verification Flow)
object TransferPageComponent : A2uiComponent {
    private val isVerificationProp = A2uiProperty.dynamicBoolean("isVerification", true, "Is verification step")
    private val recipientProp = A2uiProperty.dynamicString("recipientName", true, "Recipient name")
    private val amountProp = A2uiProperty.dynamicString("amount", true, "Amount")
    private val isInternationalProp = A2uiProperty.dynamicBoolean("isInternational", true, "International wire")
    private val riskScoreProp = A2uiProperty.dynamicString("riskScore", true, "Risk score")
    private val isHighRiskProp = A2uiProperty.dynamicBoolean("isHighRisk", true, "Is high risk flag")
    private val submitEventProp = A2uiProperty.string("submitEvent", true, "Submit event")
    private val verifyEventProp = A2uiProperty.string("verifyEvent", true, "Verify event")
    private val backEventProp = A2uiProperty.string("backEvent", true, "Back event")

    override val name: String = "TransferPage"
    override val description: String = "Full-screen page for initiating fund transfers and risk verification."
    override val properties = listOf(isVerificationProp, recipientProp, amountProp, isInternationalProp, riskScoreProp, isHighRiskProp, submitEventProp, verifyEventProp, backEventProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val isVerification = properties.bind(isVerificationProp) ?: false
        val recipient = properties.bind(recipientProp) ?: ""
        val amount = properties.bind(amountProp) ?: ""
        val isIntl = properties.bind(isInternationalProp) ?: false
        val riskScore = properties.bind(riskScoreProp) ?: "0.1"
        val isHighRisk = properties.bind(isHighRiskProp) ?: false
        val submitEvent = properties[submitEventProp] ?: TrustBankEvents.SUBMIT_TRANSFER
        val verifyEvent = properties[verifyEventProp] ?: TrustBankEvents.VERIFY_BIOMETRIC
        val backEvent = properties[backEventProp] ?: TrustBankEvents.NAVIGATE_BACK

        val scope = this

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { dispatchTypedEvent(scope, backEvent) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = if (isVerification) "Secure Transfer Verification" else "Send Money / Transfer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (!isVerification) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Enter recipient details and amount for instant secure wire transfer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { newVal ->
                            dispatchTypedEvent(scope, submitEvent, mapOf("recipientName" to newVal, "amount" to amount, "isInternational" to isIntl))
                        },
                        label = { Text("Recipient Name / IBAN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newVal ->
                            dispatchTypedEvent(scope, submitEvent, mapOf("recipientName" to recipient, "amount" to newVal, "isInternational" to isIntl))
                        },
                        label = { Text("Amount (USD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("International Wire", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("Triggers high-risk dynamic AI verification", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isIntl,
                                onCheckedChange = { checked ->
                                    dispatchTypedEvent(scope, submitEvent, mapOf("recipientName" to recipient, "amount" to amount, "isInternational" to checked))
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            dispatchTypedEvent(scope, submitEvent, mapOf("recipientName" to recipient, "amount" to amount, "isInternational" to isIntl, "execute" to true))
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("Evaluate Risk & Transfer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighRisk) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isHighRisk) Icons.Rounded.Warning else Icons.Rounded.Security,
                                    contentDescription = null,
                                    tint = if (isHighRisk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = if (isHighRisk) "⚠️ High Risk Security Alert" else "Secure Transfer Summary",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHighRisk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            Text("Recipient: $recipient", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text("Amount: $$amount", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("AI Risk Score: $riskScore", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = if (isHighRisk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (isHighRisk) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Rounded.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                Text(
                                    text = "International wire or large amount detected. Additional biometric authentication is required.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { dispatchTypedEvent(scope, verifyEvent, mapOf("verified" to true)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHighRisk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Icon(Icons.Rounded.Fingerprint, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isHighRisk) "Authorize High-Risk Wire (Biometric)" else "Confirm with Biometric / PIN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// 6. Transaction Detail Component (Full Screen Transaction Details Page)
object TransactionDetailComponent : A2uiComponent {
    private val transactionIdProp = A2uiProperty.dynamicString("transactionId", true, "Transaction ID")
    private val merchantNameProp = A2uiProperty.dynamicString("merchantName", true, "Merchant Name")
    private val amountProp = A2uiProperty.dynamicString("amount", true, "Amount")
    private val dateProp = A2uiProperty.dynamicString("date", true, "Date")
    private val categoryProp = A2uiProperty.dynamicString("category", true, "Category")
    private val isHighRiskProp = A2uiProperty.dynamicBoolean("isHighRisk", false, "Is High Risk")
    private val backEventProp = A2uiProperty.string("backEvent", true, "Back event")

    override val name: String = "TransactionDetail"
    override val description: String = "Full-screen page displaying individual transaction details."
    override val properties = listOf(transactionIdProp, merchantNameProp, amountProp, dateProp, categoryProp, isHighRiskProp, backEventProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val txId = properties.bind(transactionIdProp) ?: "TXN-000"
        val merchant = properties.bind(merchantNameProp) ?: "Merchant"
        val amount = properties.bind(amountProp) ?: "$0.00"
        val date = properties.bind(dateProp) ?: "Today"
        val category = properties.bind(categoryProp) ?: "General"
        val isHighRisk = properties.bind(isHighRiskProp) ?: false
        val backEvent = properties[backEventProp] ?: TrustBankEvents.NAVIGATE_BACK

        val scope = this

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { dispatchTypedEvent(scope, backEvent) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                    Text(
                        text = "Transaction Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isHighRisk) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Icon(
                                imageVector = if (isHighRisk) Icons.Rounded.Warning else Icons.Rounded.Security,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp).size(28.dp),
                                tint = if (isHighRisk) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                        Text(
                            text = amount,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (amount.startsWith("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = merchant,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Transaction ID: $txId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Date & Time", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(date, fontWeight = FontWeight.SemiBold)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Category", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(category, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("AI Risk Status", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    if (isHighRisk) "Flagged (High Risk)" else "Verified Safe",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHighRisk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { dispatchTypedEvent(scope, backEvent) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Back to Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
