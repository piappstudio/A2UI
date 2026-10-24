package com.piappstudio.a2ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.BankingAlert
import com.piappstudio.a2ui.data.model.BankingCustomer
import com.piappstudio.a2ui.data.model.PendingApproval
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.data.repository.BankingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val customer: BankingCustomer,
    val accounts: List<Account> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val alerts: List<BankingAlert> = emptyList(),
    val approvals: List<PendingApproval> = emptyList(),
)

class DashboardViewModel(
    repository: BankingRepository,
) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAccounts(),
        repository.getTransactions(),
        repository.getAlerts(),
        repository.getPendingApprovals(),
    ) { accounts, transactions, alerts, approvals ->
        DashboardUiState(
            customer = repository.customer(),
            accounts = accounts,
            transactions = transactions,
            alerts = alerts,
            approvals = approvals,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardUiState(customer = repository.customer()),
    )
}
