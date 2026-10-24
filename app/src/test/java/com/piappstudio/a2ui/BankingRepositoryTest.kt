package com.piappstudio.a2ui

import com.piappstudio.a2ui.data.model.AccountType
import com.piappstudio.a2ui.data.model.DisputeRequest
import com.piappstudio.a2ui.data.model.DisputeOutcome
import com.piappstudio.a2ui.data.model.LendingProduct
import com.piappstudio.a2ui.data.model.LoanApplication
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.model.TransferStatus
import com.piappstudio.a2ui.data.repository.BankingRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BankingRepositoryTest {

    @Test
    fun sampleAccountsAndTransactionsLoad() = runTest {
        val repo = BankingRepositoryImpl()
        assertTrue(repo.getAccounts().first().isNotEmpty())
        assertTrue(repo.getTransactions().first().isNotEmpty())
    }

    @Test
    fun executeTransferDeductsFundsAndIsIdempotent() = runTest {
        val repo = BankingRepositoryImpl()
        val checking = repo.getAccounts().first().first { it.type == AccountType.CHECKING }
        val quote = repo.quoteTransfer(
            sourceAccountId = checking.id,
            recipientName = "Acme Corp",
            recipientAccount = "123456789",
            amount = 500.0,
            currency = "USD",
            isInternational = false,
        )
        val request = TransferRequest(idempotencyKey = "key-1", quote = quote)
        val first = repo.executeTransfer(request)
        val second = repo.executeTransfer(request)
        assertTrue(first.accepted)
        assertEquals(TransferStatus.COMPLETED, first.status)
        assertEquals(first.reference, second.reference)
        val updated = repo.getAccounts().first().first { it.id == checking.id }
        assertEquals(checking.balance - 500.0, updated.balance, 0.01)
    }

    @Test
    fun executeTransferFailsWhenInsufficientFunds() = runTest {
        val repo = BankingRepositoryImpl()
        val checking = repo.getAccounts().first().first { it.type == AccountType.CHECKING }
        val quote = repo.quoteTransfer(
            sourceAccountId = checking.id,
            recipientName = "Yacht",
            recipientAccount = "999",
            amount = 999_999.0,
            currency = "USD",
            isInternational = false,
        )
        val result = repo.executeTransfer(TransferRequest("key-2", quote))
        assertFalse(result.accepted)
        assertEquals(TransferStatus.FAILED, result.status)
    }

    @Test
    fun highRiskTransferIsPendingReviewNotExecuted() = runTest {
        val repo = BankingRepositoryImpl()
        val checking = repo.getAccounts().first().first { it.type == AccountType.CHECKING }
        val quote = repo.quoteTransfer(checking.id, "Zurich AG", "CH00", 5_000.0, "USD", true)
        val before = checking.balance
        val result = repo.executeTransfer(TransferRequest("key-3", quote))
        assertTrue(result.accepted)
        assertEquals(TransferStatus.PENDING_REVIEW, result.status)
        val after = repo.getAccounts().first().first { it.id == checking.id }.balance
        assertEquals(before, after, 0.01)
    }

    @Test
    fun loanEligibilityUsesDemoRules() {
        val repo = BankingRepositoryImpl()
        val approved = repo.evaluateEligibility(LendingProduct.PERSONAL_LOAN, 8_000.0, 80_000.0, "employed")
        val declined = repo.evaluateEligibility(LendingProduct.PERSONAL_LOAN, 8_000.0, 10_000.0, "unemployed")
        assertTrue(approved.eligible)
        assertFalse(declined.eligible)
    }

    @Test
    fun loanSubmissionRequiresConsent() {
        val repo = BankingRepositoryImpl()
        val decision = repo.submitLoan(
            LoanApplication(
                idempotencyKey = "loan-1",
                product = LendingProduct.PERSONAL_LOAN,
                amount = 5_000.0,
                tenureMonths = 24,
                annualIncome = 80_000.0,
                employmentStatus = "employed",
                consentGiven = false,
                documentName = null,
            ),
        )
        assertFalse(decision.eligible)
    }

    @Test
    fun disputeRequiresOwnershipAndConfirmation() = runTest {
        val repo = BankingRepositoryImpl()
        val tx = repo.getTransactions().first().first { it.disputeEligible }
        val unconfirmed = repo.submitDispute(
            DisputeRequest("d1", tx.id, repo.customer().id, "unrecognized", "details", confirmed = false),
        )
        assertEquals(DisputeOutcome.ADDITIONAL_INFORMATION_REQUIRED, unconfirmed.outcome)
        val submitted = repo.submitDispute(
            DisputeRequest("d2", tx.id, repo.customer().id, "unrecognized", "I did not make this purchase", true),
        )
        assertEquals(DisputeOutcome.SUBMITTED, submitted.outcome)
        val foreign = repo.submitDispute(
            DisputeRequest("d3", tx.id, "someone-else", "unrecognized", "details", true),
        )
        assertEquals(DisputeOutcome.NOT_ELIGIBLE, foreign.outcome)
    }
}
