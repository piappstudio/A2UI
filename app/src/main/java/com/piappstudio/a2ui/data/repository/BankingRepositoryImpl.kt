package com.piappstudio.a2ui.data.repository

import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.AccountType
import com.piappstudio.a2ui.data.model.BankingAlert
import com.piappstudio.a2ui.data.model.BankingCustomer
import com.piappstudio.a2ui.data.model.DisputeOutcome
import com.piappstudio.a2ui.data.model.DisputeRequest
import com.piappstudio.a2ui.data.model.DisputeResult
import com.piappstudio.a2ui.data.model.EligibilityDecision
import com.piappstudio.a2ui.data.model.LendingProduct
import com.piappstudio.a2ui.data.model.LoanApplication
import com.piappstudio.a2ui.data.model.MerchantInfo
import com.piappstudio.a2ui.data.model.PendingApproval
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.data.model.TransferQuote
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.model.TransferResult
import com.piappstudio.a2ui.data.model.TransferRiskTier
import com.piappstudio.a2ui.data.model.TransferStatus
import com.piappstudio.a2ui.data.sample.SampleBankingData
import com.piappstudio.a2ui.domain.TransferRiskEvaluator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

class BankingRepositoryImpl : BankingRepository {
    private val _accounts = MutableStateFlow(SampleBankingData.accounts)
    private val _transactions = MutableStateFlow(SampleBankingData.transactions)
    private val _alerts = MutableStateFlow(SampleBankingData.alerts)
    private val _approvals = MutableStateFlow(SampleBankingData.pendingApprovals)
    private val processedKeys = ConcurrentHashMap<String, Any>()

    override fun customer(): BankingCustomer = SampleBankingData.customer

    override fun getAccounts(): Flow<List<Account>> = _accounts.asStateFlow()

    override fun getTransactions(): Flow<List<Transaction>> = _transactions.asStateFlow()

    override fun getAlerts(): Flow<List<BankingAlert>> = _alerts.asStateFlow()

    override fun getPendingApprovals(): Flow<List<PendingApproval>> = _approvals.asStateFlow()

    override suspend fun getAccount(id: String): Account? = _accounts.value.firstOrNull { it.id == id }

    override suspend fun getTransaction(id: String): Transaction? =
        _transactions.value.firstOrNull { it.id == id }

    override fun quoteTransfer(
        sourceAccountId: String,
        recipientName: String,
        recipientAccount: String,
        amount: Double,
        currency: String,
        isInternational: Boolean,
    ): TransferQuote {
        val tier = TransferRiskEvaluator.tier(amount, isInternational)
        val fee = if (isInternational) 25.00 else if (amount >= 1_000.0) 12.00 else 0.00
        val rate = if (isInternational) 0.92 else null
        val converted = if (rate != null) amount * rate else null
        return TransferQuote(
            sourceAccountId = sourceAccountId,
            recipientName = recipientName,
            recipientAccount = recipientAccount,
            amount = amount,
            currency = currency,
            isInternational = isInternational,
            fee = fee,
            exchangeRate = rate,
            convertedAmount = converted,
            estimatedDelivery = if (isInternational) "2–4 business days" else "Same day",
            riskTier = tier,
            riskScore = TransferRiskEvaluator.score(amount, isInternational),
            riskExplanation = TransferRiskEvaluator.explanation(tier),
        )
    }

    override fun executeTransfer(request: TransferRequest): TransferResult {
        processedKeys[request.idempotencyKey]?.let { cached ->
            return cached as TransferResult
        }
        val quote = request.quote
        if (quote.riskTier == TransferRiskTier.REJECTED || quote.amount <= 0.0) {
            return remember(request.idempotencyKey, TransferResult(
                accepted = false,
                status = TransferStatus.REJECTED,
                reference = null,
                message = "The trusted backend rejected this transfer.",
            ))
        }
        if (quote.riskTier == TransferRiskTier.HIGH_REVIEW) {
            return remember(request.idempotencyKey, TransferResult(
                accepted = true,
                status = TransferStatus.PENDING_REVIEW,
                reference = "REV-${request.idempotencyKey.takeLast(8)}",
                message = "MOCK OUTCOME: sent for manual review. The agent cannot move funds.",
            ))
        }
        val accounts = _accounts.value
        val source = accounts.firstOrNull { it.id == quote.sourceAccountId }
            ?: accounts.firstOrNull { it.type == AccountType.CHECKING }
        val total = quote.amount + quote.fee
        if (source == null || source.balance < total) {
            return remember(request.idempotencyKey, TransferResult(
                accepted = false,
                status = TransferStatus.FAILED,
                reference = null,
                message = "Transfer failed: insufficient funds in the source account.",
            ))
        }
        _accounts.value = accounts.map { account ->
            if (account.id == source.id) account.copy(balance = account.balance - total) else account
        }
        val tx = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            accountId = source.id,
            merchant = MerchantInfo(
                name = "Transfer to ${quote.recipientName}",
                category = if (quote.isInternational) "Int'l Wire" else "Transfer",
            ),
            amount = -quote.amount,
            currency = quote.currency,
            dateLabel = "Just now",
            isoTimestamp = "2026-10-23T16:00:00Z",
            status = "Posted",
            isHighRisk = quote.isInternational || quote.amount >= 1_000.0,
        )
        _transactions.value = listOf(tx) + _transactions.value
        return remember(request.idempotencyKey, TransferResult(
            accepted = true,
            status = TransferStatus.COMPLETED,
            reference = "TR-${request.idempotencyKey.takeLast(8)}",
            message = "MOCK OUTCOME: demo transfer completed. No real funds moved.",
        ))
    }

    override fun evaluateEligibility(
        product: LendingProduct,
        amount: Double,
        annualIncome: Double,
        employmentStatus: String,
    ): EligibilityDecision {
        val employed = employmentStatus.equals("employed", ignoreCase = true) ||
            employmentStatus.equals("self-employed", ignoreCase = true)
        val maxAmount = when (product) {
            LendingProduct.PERSONAL_LOAN -> annualIncome * 0.4
            LendingProduct.CREDIT_LIMIT_INCREASE -> 8_000.0
        }
        val eligible = employed && annualIncome >= 35_000.0 && amount in 500.0..maxAmount
        return EligibilityDecision(
            eligible = eligible,
            product = product,
            approvedAmount = if (eligible) amount else null,
            annualRatePercent = if (eligible) 9.75 else null,
            message = if (eligible) {
                "MOCK OUTCOME: demo eligibility approved. This is not a bank offer."
            } else {
                "MOCK OUTCOME: demo rules declined this request. Income, employment, or amount is outside policy."
            },
        )
    }

    override fun submitLoan(application: LoanApplication): EligibilityDecision {
        processedKeys[application.idempotencyKey]?.let { return it as EligibilityDecision }
        if (!application.consentGiven) {
            return remember(application.idempotencyKey, EligibilityDecision(
                eligible = false,
                product = application.product,
                approvedAmount = null,
                annualRatePercent = null,
                message = "Consent is required before a demo application can be submitted.",
            ))
        }
        val decision = evaluateEligibility(
            product = application.product,
            amount = application.amount,
            annualIncome = application.annualIncome,
            employmentStatus = application.employmentStatus,
        )
        return remember(application.idempotencyKey, decision)
    }

    override fun submitDispute(request: DisputeRequest): DisputeResult {
        processedKeys[request.idempotencyKey]?.let { return it as DisputeResult }
        if (!request.confirmed) {
            return remember(request.idempotencyKey, DisputeResult(
                outcome = DisputeOutcome.ADDITIONAL_INFORMATION_REQUIRED,
                reference = null,
                message = "Explicit confirmation is required before a dispute is created.",
            ))
        }
        val transaction = _transactions.value.firstOrNull { it.id == request.transactionId }
        if (transaction == null || request.customerId != customer().id) {
            return remember(request.idempotencyKey, DisputeResult(
                outcome = DisputeOutcome.NOT_ELIGIBLE,
                reference = null,
                message = "The selected transaction does not belong to the authenticated demo customer.",
            ))
        }
        if (!transaction.disputeEligible) {
            return remember(request.idempotencyKey, DisputeResult(
                outcome = DisputeOutcome.NOT_ELIGIBLE,
                reference = null,
                message = "MOCK OUTCOME: this credit/income transaction is not eligible for a dispute.",
            ))
        }
        if (request.details.isBlank() && transaction.amount > -50.0) {
            return remember(request.idempotencyKey, DisputeResult(
                outcome = DisputeOutcome.ADDITIONAL_INFORMATION_REQUIRED,
                reference = null,
                message = "MOCK OUTCOME: more detail is required before this dispute can be filed.",
            ))
        }
        return remember(request.idempotencyKey, DisputeResult(
            outcome = DisputeOutcome.SUBMITTED,
            reference = "DSP-${request.idempotencyKey.takeLast(8)}",
            message = "MOCK OUTCOME: dispute filed. No real investigation was opened.",
        ))
    }

    private fun <T : Any> remember(key: String, value: T): T {
        processedKeys[key] = value
        return value
    }
}
