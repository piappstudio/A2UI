package com.piappstudio.a2ui.a2ui.catalog

import androidx.a2ui.compose.runtime.A2uiComponentProperties
import androidx.a2ui.compose.runtime.A2uiComponentScope
import androidx.a2ui.compose.runtime.A2uiProperty
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.piappstudio.a2ui.domain.EmiCalculator
import com.piappstudio.a2ui.domain.MoneyFormat
import com.piappstudio.a2ui.security.RegisteredActions

private fun A2uiComponentScope.dispatchEvent(name: String, context: Map<String, Any?> = emptyMap()) {
    dispatchAction(
        mapOf(
            "event" to mapOf(
                "name" to name,
                "context" to context,
            ),
        ),
    )
}

object EmiSummaryComponent : A2uiComponent {
    private val amountProp = A2uiProperty.dynamicNumber("amount", true, "Loan principal")
    private val rateProp = A2uiProperty.dynamicNumber("annualRatePercent", true, "Annual interest rate")
    private val tenureProp = A2uiProperty.dynamicNumber("tenureMonths", true, "Tenure in months")

    override val name: String = "EmiSummary"
    override val description: String =
        "Shows an illustrative EMI using the standard amortizing-loan formula. Not a bank offer."
    override val properties = listOf(amountProp, rateProp, tenureProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val amount = properties.bind(amountProp)?.toDouble() ?: 0.0
        val rate = properties.bind(rateProp)?.toDouble() ?: 0.0
        val tenure = properties.bind(tenureProp)?.toInt() ?: 0
        val estimate = EmiCalculator.estimate(amount, rate, tenure)
        Card(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Illustrative EMI summary" },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Illustrative EMI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    MoneyFormat.currency(estimate.monthlyPayment) + " / month",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text("Principal ${MoneyFormat.currency(amount)} · ${tenure} months · $rate% APR")
                Text("Total payable ${MoneyFormat.currency(estimate.totalPayable)}")
                Text(
                    "DEMO estimate only. Interest, eligibility, and final terms come from the trusted backend.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

object ExchangeRateSummaryComponent : A2uiComponent {
    private val fromProp = A2uiProperty.dynamicString("fromCurrency", true, "Source currency")
    private val toProp = A2uiProperty.dynamicString("toCurrency", true, "Destination currency")
    private val rateProp = A2uiProperty.dynamicString("rate", true, "Quoted rate")
    private val convertedProp = A2uiProperty.dynamicString("convertedAmount", true, "Converted amount")
    private val feeProp = A2uiProperty.dynamicString("fee", true, "Transfer fee")
    private val deliveryProp = A2uiProperty.dynamicString("delivery", true, "Estimated delivery")

    override val name: String = "ExchangeRateSummary"
    override val description: String = "Displays a backend-provided FX quote, fee, and delivery estimate."
    override val properties = listOf(fromProp, toProp, rateProp, convertedProp, feeProp, deliveryProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val from = properties.bind(fromProp) ?: "USD"
        val to = properties.bind(toProp) ?: "EUR"
        val rate = properties.bind(rateProp) ?: "—"
        val converted = properties.bind(convertedProp) ?: "—"
        val fee = properties.bind(feeProp) ?: "—"
        val delivery = properties.bind(deliveryProp) ?: "—"
        Card(modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Exchange-rate summary", fontWeight = FontWeight.Bold)
                Text("$from → $to at $rate  ·  Recipient receives $converted")
                Text("Fee $fee  ·  Estimated delivery $delivery")
                Text("DEMO quote from the trusted backend.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

object VerificationStatusComponent : A2uiComponent {
    private val statusProp = A2uiProperty.dynamicString("status", true, "Verification status")
    private val detailProp = A2uiProperty.dynamicString("detail", true, "Status detail")

    override val name: String = "VerificationStatus"
    override val description: String = "Shows a trusted-backend verification state. Never claims biometric success."
    override val properties = listOf(statusProp, detailProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val status = properties.bind(statusProp) ?: "Pending"
        val detail = properties.bind(detailProp) ?: ""
        Card(
            modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (status.contains("complete", true)) Icons.Outlined.Verified else Icons.Outlined.Security,
                    contentDescription = null,
                )
                Column {
                    Text("Verification: $status", fontWeight = FontWeight.Bold)
                    Text(detail, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

object TransactionLocationCardComponent : A2uiComponent {
    private val cityProp = A2uiProperty.dynamicString("city", true, "Merchant city")
    private val countryProp = A2uiProperty.dynamicString("country", true, "Merchant country")
    private val verifiedProp = A2uiProperty.dynamicBoolean("verified", true, "Location is verified")
    private val noteProp = A2uiProperty.dynamicString("note", false, "Location note")

    override val name: String = "TransactionLocationCard"
    override val description: String =
        "Shows a merchant location summary using only verified backend location data."
    override val properties = listOf(cityProp, countryProp, verifiedProp, noteProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val city = properties.bind(cityProp) ?: "Unknown"
        val country = properties.bind(countryProp) ?: "Unknown"
        val verified = properties.bind(verifiedProp) ?: false
        val note = properties.bind(noteProp)
            ?: if (verified) {
                "Merchant location is verified. This is not your physical location."
            } else {
                "No verified merchant location is available."
            }
        Card(modifier.fillMaxWidth()) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Place, contentDescription = "Merchant location", modifier = Modifier.size(28.dp))
                Column {
                    Text("Merchant location", fontWeight = FontWeight.Bold)
                    Text("$city, $country")
                    Text(note, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

object DocumentAttachmentComponent : A2uiComponent {
    private val labelProp = A2uiProperty.dynamicString("label", true, "Attachment label")
    private val fileNameProp = A2uiProperty.dynamicString("fileName", false, "Selected file name")
    private val eventProp = A2uiProperty.string("attachEvent", false, "Event name for the system picker")

    override val name: String = "DocumentAttachment"
    override val description: String =
        "Triggers the Android document picker through a registered application action."
    override val properties = listOf(labelProp, fileNameProp, eventProp)

    @Composable
    override fun A2uiComponentScope.Content(properties: A2uiComponentProperties, modifier: Modifier) {
        val label = properties.bind(labelProp) ?: "Attach a document"
        val fileName = properties.bind(fileNameProp)
        val event = properties[eventProp] ?: RegisteredActions.ATTACH_DOCUMENT
        val scope = this
        Card(modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(label, fontWeight = FontWeight.Bold)
                Text(
                    fileName?.takeIf { it.isNotBlank() } ?: "No file selected",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { scope.dispatchEvent(event) }) {
                    Icon(Icons.Outlined.AttachFile, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Choose with system picker")
                }
            }
        }
    }
}
