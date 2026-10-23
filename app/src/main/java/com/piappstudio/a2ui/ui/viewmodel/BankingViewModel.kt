package com.piappstudio.a2ui.ui.viewmodel

import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import androidx.lifecycle.viewModelScope
import com.piappstudio.a2ui.a2ui.agent.BankingSurfaceAgent
import com.piappstudio.a2ui.a2ui.catalog.trustBankCatalog
import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.repository.BankingRepository
import com.piappstudio.a2ui.data.repository.BankingRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class BankingUiState(
    val currentSurfaceId: String = BankingSurfaceAgent.SURFACE_ID_DASHBOARD,
    val accounts: List<Account> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val selectedTransaction: Transaction? = null,
    val isVerificationStep: Boolean = false,
    val draftRecipient: String = "",
    val draftAmount: String = "",
    val isInternational: Boolean = false,
    val riskScore: Double = 0.1,
    val isHighRisk: Boolean = false,
    val statusMessage: String? = null,
)

class BankingViewModel(
    private val repository: BankingRepository = BankingRepositoryImpl(),
) : GenericA2UIViewModel(catalogs = listOf(trustBankCatalog())) {

    private val _uiState = MutableStateFlow(BankingUiState())
    val uiState: StateFlow<BankingUiState> = _uiState.asStateFlow()

    private var didSeedModel = false

    init {
        // Register action handlers dynamically (Extensible Action Dispatching)
        registerActionHandler("submitTransfer") { context ->
            val recipient = context["recipientName"] as? String ?: _uiState.value.draftRecipient
            val amount = context["amount"] as? String ?: _uiState.value.draftAmount
            val isIntl = context["isInternational"] as? Boolean ?: _uiState.value.isInternational
            val execute = context["execute"] as? Boolean ?: false
            submitTransferDraft(recipient, amount, isIntl, execute)
        }
        registerActionHandler("verifyBiometric") { _ ->
            verifyBiometric()
        }
        registerActionHandler("navigateBack") { _ ->
            navigateToDashboard()
        }
        registerActionHandler("selectTransaction") { context ->
            val txId = context["transactionId"] as? String
            if (txId != null) {
                selectTransaction(txId)
            }
        }

        // Initialize all navigation surfaces dynamically
        submit(BankingSurfaceAgent.createDashboardSurface())
        submit(BankingSurfaceAgent.defineDashboardInterface())

        submit(BankingSurfaceAgent.createTransferSurface())
        submit(BankingSurfaceAgent.defineTransferInterface())

        submit(BankingSurfaceAgent.createTransactionDetailSurface())
        submit(BankingSurfaceAgent.defineTransactionDetailInterface())

        didSeedModel = true

        observeBankingData()
    }

    override fun dispatchAction(actionId: String, params: Map<String, Any?>) {
        super.dispatchAction(actionId, params)
    }

    fun navigateToDashboard() {
        _uiState.update { it.copy(currentSurfaceId = BankingSurfaceAgent.SURFACE_ID_DASHBOARD, isVerificationStep = false) }
        pushDataModel()
    }

    fun openTransferPage() {
        _uiState.update {
            it.copy(
                currentSurfaceId = BankingSurfaceAgent.SURFACE_ID_TRANSFER,
                isVerificationStep = false,
                draftRecipient = "",
                draftAmount = "",
                isInternational = false,
                riskScore = 0.1,
                isHighRisk = false,
            )
        }
        pushDataModel()
    }

    fun selectTransaction(transactionId: String) {
        val tx = _uiState.value.transactions.firstOrNull { it.id == transactionId }
        _uiState.update {
            it.copy(
                currentSurfaceId = BankingSurfaceAgent.SURFACE_ID_TRANSACTION_DETAIL,
                selectedTransaction = tx,
            )
        }
        pushDataModel()
    }

    fun submitTransferDraft(recipient: String, amount: String, isInternational: Boolean, execute: Boolean = false) {
        val parsedAmount = amount.toDoubleOrNull() ?: 0.0
        val riskScore = if (isInternational) 0.85 else if (parsedAmount >= 1000.0) 0.65 else 0.12
        val isHighRisk = riskScore >= 0.5

        _uiState.update {
            it.copy(
                draftRecipient = recipient,
                draftAmount = amount,
                isInternational = isInternational,
                riskScore = riskScore,
                isHighRisk = isHighRisk,
            )
        }

        if (execute) {
            if (isHighRisk) {
                _uiState.update { it.copy(isVerificationStep = true) }
            } else {
                executeTransferConfirmed(recipient, parsedAmount, isInternational)
            }
        }
        pushDataModel()
    }

    fun verifyBiometric() {
        val state = _uiState.value
        val amount = state.draftAmount.toDoubleOrNull() ?: 0.0
        executeTransferConfirmed(state.draftRecipient, amount, state.isInternational)
    }

    private fun executeTransferConfirmed(recipient: String, amount: Double, isInternational: Boolean) {
        val request = TransferRequest(
            recipientName = recipient,
            accountNumber = "•••• 4892",
            amount = amount,
            isInternational = isInternational,
            riskScore = _uiState.value.riskScore,
        )
        val success = repository.executeTransfer(request)
        if (success) {
            _uiState.update {
                it.copy(
                    currentSurfaceId = BankingSurfaceAgent.SURFACE_ID_DASHBOARD,
                    isVerificationStep = false,
                    draftRecipient = "",
                    draftAmount = "",
                    statusMessage = "Successfully transferred $$amount to $recipient",
                )
            }
        } else {
            _uiState.update {
                it.copy(statusMessage = "Transfer failed: Insufficient funds.")
            }
        }
        pushDataModel()
    }

    private fun observeBankingData() {
        combine(
            repository.getAccounts(),
            repository.getTransactions(),
            _uiState,
        ) { accounts, transactions, state ->
            Triple(accounts, transactions, state)
        }.onEach { (accounts, transactions, _) ->
            _uiState.update { it.copy(accounts = accounts, transactions = transactions) }
            pushDataModel()
        }.launchIn(viewModelScope)
    }

    private fun pushDataModel() {
        if (!didSeedModel) return
        val state = _uiState.value
        submit(
            BankingSurfaceAgent.updateDashboardDataModel(
                accounts = state.accounts,
                transactions = state.transactions,
            )
        )
        submit(
            BankingSurfaceAgent.updateTransferDataModel(
                draftRecipient = state.draftRecipient,
                draftAmount = state.draftAmount,
                isInternational = state.isInternational,
                isVerification = state.isVerificationStep,
                riskScore = state.riskScore,
                isHighRisk = state.isHighRisk,
            )
        )
        submit(
            BankingSurfaceAgent.updateTransactionDetailDataModel(
                transaction = state.selectedTransaction,
            )
        )
    }
}
