package com.piappstudio.a2ui.a2ui.agent

import com.piappstudio.a2ui.a2ui.protocol.updateComponents
import com.piappstudio.a2ui.a2ui.protocol.updateData
import com.piappstudio.a2ui.data.model.DisputeRequest
import com.piappstudio.a2ui.data.model.LendingProduct
import com.piappstudio.a2ui.data.model.LoanApplication
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.model.TransferRiskTier
import com.piappstudio.a2ui.data.model.TransferStatus
import com.piappstudio.a2ui.data.repository.BankingRepository
import com.piappstudio.a2ui.domain.EmiCalculator
import com.piappstudio.a2ui.domain.MoneyFormat
import com.piappstudio.a2ui.security.RegisteredActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class MockAgentBackend(
    private val repository: BankingRepository,
) : AgentBackend {

    private data class Session(
        val workflow: AgentWorkflow,
        var idempotencyKey: String = UUID.randomUUID().toString(),
        var authorized: Boolean = false,
        var documentName: String? = null,
        var lastQuoteKey: String? = null,
    )

    private val sessions = ConcurrentHashMap<String, Session>()

    override suspend fun startWorkflow(request: AgentRequest): AgentResponse {
        delay(280)
        val session = Session(request.workflow)
        sessions[request.sessionId] = session
        return when (request.workflow) {
            AgentWorkflow.TRANSFER -> transferForm(request)
            AgentWorkflow.LOAN -> loanForm(request, LendingProduct.PERSONAL_LOAN)
            AgentWorkflow.CREDIT_LIMIT -> loanForm(request, LendingProduct.CREDIT_LIMIT_INCREASE)
            AgentWorkflow.FRAUD -> fraudForm(request)
            AgentWorkflow.ASSISTANT -> assistant(request)
        }
    }

    override suspend fun handleAction(request: AgentRequest): AgentResponse {
        delay(180)
        val session = sessions[request.sessionId] ?: Session(request.workflow).also {
            sessions[request.sessionId] = it
        }
        return when (request.action) {
            RegisteredActions.REVIEW_TRANSFER -> reviewTransfer(request, session)
            RegisteredActions.START_MOCK_VERIFICATION -> mockVerification(request)
            RegisteredActions.COMPLETE_MOCK_VERIFICATION -> completeMockVerification(request, session)
            RegisteredActions.CONFIRM_TRANSFER -> confirmTransfer(request, session)
            RegisteredActions.SUBMIT_LOAN -> submitLending(request, session, LendingProduct.PERSONAL_LOAN)
            RegisteredActions.SUBMIT_CREDIT_LIMIT ->
                submitLending(request, session, LendingProduct.CREDIT_LIMIT_INCREASE)
            RegisteredActions.ATTACH_DOCUMENT -> AgentResponse(messages = emptyList(), pickDocument = true)
            RegisteredActions.SUBMIT_DISPUTE -> submitDispute(request, session)
            RegisteredActions.RETRY_WORKFLOW -> startWorkflow(request)
            RegisteredActions.CANCEL_WORKFLOW, RegisteredActions.CLOSE_WORKFLOW -> {
                sessions.remove(request.sessionId)
                AgentResponse(messages = emptyList(), closeWorkflow = true)
            }
            else -> errorResponse(request, "The action '${request.action}' is not registered.")
        }
    }

    override suspend fun cancel(sessionId: String, workflow: AgentWorkflow) {
        sessions.remove(sessionId)
    }

    fun attachDocument(sessionId: String, fileName: String) {
        sessions[sessionId]?.documentName = fileName
    }

    private suspend fun transferForm(request: AgentRequest): AgentResponse {
        val accounts = currentAccounts()
        val options = accounts.map { mapOf("label" to "${it.name} ${it.maskedNumber}", "value" to it.id) }
        val defaultId = accounts.firstOrNull()?.id.orEmpty()
        val surfaceId = request.workflow.surfaceId
        return AgentResponse(
            messages = WorkflowSurfaces.bootstrap(
                surfaceId,
                WorkflowSurfaces.transferForm(surfaceId, options),
                mapOf(
                    "draft" to mapOf(
                        "sourceAccountId" to listOf(defaultId),
                        "recipientName" to "",
                        "recipientAccount" to "",
                        "amount" to "",
                        "currency" to listOf("USD"),
                        "isInternational" to false,
                    ),
                ),
            ),
        )
    }

    private suspend fun reviewTransfer(request: AgentRequest, session: Session): AgentResponse {
        val amount = MoneyFormat.parseAmount(stringValue(request, "amount", "/draft/amount")).orZero()
        val recipient = stringValue(request, "recipientName", "/draft/recipientName")
        val recipientAccount = stringValue(request, "recipientAccount", "/draft/recipientAccount")
        val source = firstString(request, "sourceAccountId", "/draft/sourceAccountId")
        val currency = firstString(request, "currency", "/draft/currency").ifBlank { "USD" }
        val international = boolValue(request, "isInternational", "/draft/isInternational")
        if (recipient.isBlank() || amount <= 0.0) {
            return errorResponse(request, "Enter a beneficiary and a valid amount before review.")
        }
        val quote = repository.quoteTransfer(source, recipient, recipientAccount, amount, currency, international)
        session.lastQuoteKey = session.idempotencyKey
        session.authorized = quote.riskTier == TransferRiskTier.LOW
        val surfaceId = request.workflow.surfaceId
        val needsVerification = quote.riskTier == TransferRiskTier.VERIFICATION_REQUIRED
        val data = mapOf(
            "review" to mapOf(
                "headline" to "Review recipient, amount, currency, and fees before confirmation.",
                "recipient" to "Recipient: $recipient",
                "account" to "Account: ${maskAccount(recipientAccount)}",
                "amount" to "Amount: ${MoneyFormat.currency(quote.amount, quote.currency)}",
                "fee" to "Fee: ${MoneyFormat.currency(quote.fee, quote.currency)}",
                "risk" to quote.riskExplanation,
            ),
            "quote" to mapOf(
                "fromCurrency" to quote.currency,
                "toCurrency" to if (quote.isInternational) "EUR" else quote.currency,
                "rate" to (quote.exchangeRate?.toString() ?: "1.00"),
                "convertedAmount" to (quote.convertedAmount?.let { MoneyFormat.currency(it, "EUR") } ?: "n/a"),
                "fee" to MoneyFormat.currency(quote.fee, quote.currency),
                "delivery" to quote.estimatedDelivery,
            ),
            "verification" to mapOf(
                "status" to if (needsVerification) "Additional verification required" else "Not required",
                "detail" to if (needsVerification) {
                    "Complete the labeled mock verification. This is not biometric authentication."
                } else {
                    "Low-risk demo transfer. Backend authorization is still required."
                },
            ),
            "pendingQuote" to mapOf(
                "sourceAccountId" to source,
                "recipientName" to recipient,
                "recipientAccount" to recipientAccount,
                "amount" to quote.amount,
                "currency" to quote.currency,
                "isInternational" to international,
                "riskTier" to quote.riskTier.name,
            ),
        )
        val messages = buildList {
            add(updateComponents(surfaceId, WorkflowSurfaces.transferReview(surfaceId, needsVerification)))
            add(updateData(surfaceId, data))
            if (international) {
                delay(220)
                add(
                    updateData(
                        surfaceId,
                        mapOf("detail" to "Live FX quote refreshed from the trusted backend."),
                        "/quote/incrementalNote",
                    ),
                )
            }
        }
        return AgentResponse(messages = messages)
    }

    private fun mockVerification(request: AgentRequest): AgentResponse {
        val surfaceId = request.workflow.surfaceId
        return AgentResponse(
            messages = listOf(
                updateComponents(surfaceId, WorkflowSurfaces.mockVerification()),
                updateData(
                    surfaceId,
                    mapOf(
                        "verification" to mapOf(
                            "status" to "Mock verification in progress",
                            "detail" to "DEMO verification. No biometric hardware is used.",
                            "acknowledged" to false,
                        ),
                    ),
                ),
            ),
        )
    }

    private suspend fun completeMockVerification(request: AgentRequest, session: Session): AgentResponse {
        val acknowledged = boolValue(request, "acknowledged", "/verification/acknowledged")
        if (!acknowledged) {
            return errorResponse(request, "Acknowledge that this is mock demo verification before continuing.")
        }
        session.authorized = true
        return confirmTransfer(request, session)
    }

    private suspend fun confirmTransfer(request: AgentRequest, session: Session): AgentResponse {
        val pending = mapAt(request.dataModel, "pendingQuote")
        val amount = (pending["amount"] as? Number)?.toDouble()
            ?: MoneyFormat.parseAmount(stringValue(request, "amount", "/draft/amount")).orZero()
        val recipient = pending["recipientName"] as? String
            ?: stringValue(request, "recipientName", "/draft/recipientName")
        val recipientAccount = pending["recipientAccount"] as? String
            ?: stringValue(request, "recipientAccount", "/draft/recipientAccount")
        val source = pending["sourceAccountId"] as? String
            ?: firstString(request, "sourceAccountId", "/draft/sourceAccountId")
        val currency = pending["currency"] as? String ?: "USD"
        val international = pending["isInternational"] as? Boolean ?: false
        val quote = repository.quoteTransfer(source, recipient, recipientAccount, amount, currency, international)
        if (quote.riskTier == TransferRiskTier.VERIFICATION_REQUIRED && !session.authorized) {
            return mockVerification(request)
        }
        val result = repository.executeTransfer(
            TransferRequest(idempotencyKey = session.idempotencyKey, quote = quote),
        )
        session.authorized = result.status == TransferStatus.COMPLETED ||
            result.status == TransferStatus.PENDING_REVIEW
        return resultScreen(
            request,
            title = if (result.accepted) "Transfer update" else "Transfer not completed",
            body = result.message,
            reference = result.reference ?: "None",
        )
    }

    private suspend fun loanForm(request: AgentRequest, product: LendingProduct): AgentResponse {
        val surfaceId = request.workflow.surfaceId
        val amount = if (product == LendingProduct.CREDIT_LIMIT_INCREASE) 3_000.0 else 8_000.0
        val tenure = if (product == LendingProduct.CREDIT_LIMIT_INCREASE) 12 else 36
        val estimate = EmiCalculator.estimate(amount, 9.75, tenure)
        return AgentResponse(
            messages = WorkflowSurfaces.bootstrap(
                surfaceId,
                WorkflowSurfaces.loanForm(product == LendingProduct.CREDIT_LIMIT_INCREASE),
                mapOf(
                    "loan" to mapOf(
                        "amount" to amount,
                        "tenureMonths" to tenure.toDouble(),
                        "annualRatePercent" to 9.75,
                        "annualIncome" to "",
                        "employmentStatus" to listOf("employed"),
                        "consent" to false,
                        "documentName" to "",
                        "monthlyPayment" to estimate.monthlyPayment,
                    ),
                ),
            ),
        )
    }

    private suspend fun submitLending(
        request: AgentRequest,
        session: Session,
        product: LendingProduct,
    ): AgentResponse {
        val amount = numberValue(request, "amount", "/loan/amount")
        val tenure = numberValue(request, "tenureMonths", "/loan/tenureMonths").toInt()
        val income = MoneyFormat.parseAmount(stringValue(request, "annualIncome", "/loan/annualIncome")).orZero()
        val employment = firstString(request, "employmentStatus", "/loan/employmentStatus")
        val consent = boolValue(request, "consent", "/loan/consent")
        val application = LoanApplication(
            idempotencyKey = session.idempotencyKey,
            product = product,
            amount = amount,
            tenureMonths = tenure,
            annualIncome = income,
            employmentStatus = employment,
            consentGiven = consent,
            documentName = session.documentName ?: stringValue(request, "documentName", "/loan/documentName"),
        )
        val decision = repository.submitLoan(application)
        return resultScreen(
            request,
            title = if (decision.eligible) "Demo application accepted" else "Demo application not approved",
            body = decision.message,
            reference = decision.approvedAmount?.let { MoneyFormat.currency(it) } ?: "None",
        )
    }

    private suspend fun fraudForm(request: AgentRequest): AgentResponse {
        val tx = request.transactionId?.let { repository.getTransaction(it) }
            ?: currentTransactions().firstOrNull { it.isHighRisk }
        if (tx == null) {
            return errorResponse(request, "No eligible transaction is available for investigation.")
        }
        val surfaceId = request.workflow.surfaceId
        return AgentResponse(
            messages = WorkflowSurfaces.bootstrap(
                surfaceId,
                WorkflowSurfaces.fraudForm(),
                mapOf(
                    "transaction" to mapOf(
                        "id" to tx.id,
                        "merchant" to tx.merchant.name,
                        "amount" to MoneyFormat.currency(tx.amount, tx.currency),
                        "date" to tx.dateLabel,
                        "status" to tx.status,
                        "contact" to ("Merchant contact: " + (tx.merchant.contact ?: "not provided")),
                        "city" to (tx.merchant.city ?: "Unknown"),
                        "country" to (tx.merchant.country ?: "Unknown"),
                        "locationVerified" to tx.locationVerified,
                        "locationNote" to if (tx.locationVerified) {
                            "Verified merchant location. This is not your physical location."
                        } else {
                            "No verified merchant location is available."
                        },
                    ),
                    "dispute" to mapOf(
                        "reason" to listOf("unrecognized"),
                        "details" to "",
                        "confirmed" to false,
                    ),
                ),
            ),
        )
    }

    private suspend fun submitDispute(request: AgentRequest, session: Session): AgentResponse {
        val txId = stringValue(request, "transactionId", "/transaction/id")
        val result = repository.submitDispute(
            DisputeRequest(
                idempotencyKey = session.idempotencyKey,
                transactionId = txId,
                customerId = repository.customer().id,
                reason = firstString(request, "reason", "/dispute/reason"),
                details = stringValue(request, "details", "/dispute/details"),
                confirmed = boolValue(request, "confirmed", "/dispute/confirmed"),
            ),
        )
        return resultScreen(
            request,
            title = "Dispute update",
            body = result.message,
            reference = result.reference ?: "None",
        )
    }

    private fun assistant(request: AgentRequest): AgentResponse {
        val surfaceId = request.workflow.surfaceId
        return AgentResponse(
            messages = WorkflowSurfaces.bootstrap(
                surfaceId,
                WorkflowSurfaces.assistantHome(),
                emptyMap(),
            ),
        )
    }

    private fun resultScreen(
        request: AgentRequest,
        title: String,
        body: String,
        reference: String,
    ): AgentResponse {
        val surfaceId = request.workflow.surfaceId
        return AgentResponse(
            messages = listOf(
                updateComponents(surfaceId, WorkflowSurfaces.resultScreen()),
                updateData(
                    surfaceId,
                    mapOf(
                        "result" to mapOf(
                            "title" to title,
                            "body" to body,
                            "reference" to "Reference: $reference",
                        ),
                    ),
                ),
            ),
        )
    }

    private fun errorResponse(request: AgentRequest, message: String): AgentResponse {
        val surfaceId = request.workflow.surfaceId
        return AgentResponse(
            messages = listOf(
                updateComponents(surfaceId, WorkflowSurfaces.errorSurface(message)),
            ),
            userMessage = message,
        )
    }

    private suspend fun currentAccounts() = repository.getAccounts().first()

    private suspend fun currentTransactions() = repository.getTransactions().first()

    private fun stringValue(request: AgentRequest, key: String, path: String): String {
        request.context[key]?.let { return scalar(it) }
        return lookup(request.dataModel, path)
    }

    private fun firstString(request: AgentRequest, key: String, path: String): String {
        val raw = request.context[key] ?: lookupRaw(request.dataModel, path)
        return when (raw) {
            is List<*> -> raw.firstOrNull()?.toString().orEmpty()
            else -> raw?.toString().orEmpty()
        }
    }

    private fun boolValue(request: AgentRequest, key: String, path: String): Boolean {
        val raw = request.context[key] ?: lookupRaw(request.dataModel, path)
        return when (raw) {
            is Boolean -> raw
            is String -> raw.equals("true", true)
            else -> false
        }
    }

    private fun numberValue(request: AgentRequest, key: String, path: String): Double {
        val raw = request.context[key] ?: lookupRaw(request.dataModel, path)
        return when (raw) {
            is Number -> raw.toDouble()
            is String -> raw.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun lookup(model: Map<String, Any?>, path: String): String =
        lookupRaw(model, path)?.toString().orEmpty()

    private fun lookupRaw(model: Map<String, Any?>, path: String): Any? {
        val parts = path.trim('/').split('/').filter { it.isNotEmpty() }
        var current: Any? = model
        for (part in parts) {
            current = (current as? Map<*, *>)?.get(part)
        }
        return current
    }

    private fun mapAt(model: Map<String, Any?>, key: String): Map<String, Any?> {
        @Suppress("UNCHECKED_CAST")
        return (model[key] as? Map<String, Any?>) ?: emptyMap()
    }

    private fun scalar(value: Any?): String = when (value) {
        is List<*> -> value.firstOrNull()?.toString().orEmpty()
        else -> value?.toString().orEmpty()
    }

    private fun maskAccount(value: String): String {
        if (value.length < 4) return "••••"
        return "•••• " + value.takeLast(4)
    }

    private fun Double?.orZero(): Double = this ?: 0.0
}
