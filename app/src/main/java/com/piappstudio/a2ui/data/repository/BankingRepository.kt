package com.piappstudio.a2ui.data.repository

import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.BankingAlert
import com.piappstudio.a2ui.data.model.BankingCustomer
import com.piappstudio.a2ui.data.model.DisputeRequest
import com.piappstudio.a2ui.data.model.DisputeResult
import com.piappstudio.a2ui.data.model.EligibilityDecision
import com.piappstudio.a2ui.data.model.LendingProduct
import com.piappstudio.a2ui.data.model.LoanApplication
import com.piappstudio.a2ui.data.model.PendingApproval
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.data.model.TransferQuote
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.model.TransferResult
import kotlinx.coroutines.flow.Flow

interface BankingRepository {
    fun customer(): BankingCustomer
    fun getAccounts(): Flow<List<Account>>
    fun getTransactions(): Flow<List<Transaction>>
    fun getAlerts(): Flow<List<BankingAlert>>
    fun getPendingApprovals(): Flow<List<PendingApproval>>
    suspend fun getAccount(id: String): Account?
    suspend fun getTransaction(id: String): Transaction?
    fun quoteTransfer(
        sourceAccountId: String,
        recipientName: String,
        recipientAccount: String,
        amount: Double,
        currency: String,
        isInternational: Boolean,
    ): TransferQuote
    fun executeTransfer(request: TransferRequest): TransferResult
    fun evaluateEligibility(
        product: LendingProduct,
        amount: Double,
        annualIncome: Double,
        employmentStatus: String,
    ): EligibilityDecision
    fun submitLoan(application: LoanApplication): EligibilityDecision
    fun submitDispute(request: DisputeRequest): DisputeResult
}
