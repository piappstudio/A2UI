package com.piappstudio.a2ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piappstudio.a2ui.a2ui.agent.AgentWorkflow
import com.piappstudio.a2ui.di.AppContainer

class TrustBankViewModelFactory(
    private val container: AppContainer,
    private val workflow: AgentWorkflow? = null,
    private val transactionId: String? = null,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(SessionViewModel::class.java) ->
                SessionViewModel(container.bankingRepository)
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(container.bankingRepository)
            modelClass.isAssignableFrom(A2uiSessionViewModel::class.java) ->
                A2uiSessionViewModel(
                    workflow = requireNotNull(workflow),
                    transactionId = transactionId,
                    agent = container.agentBackend,
                    catalogs = listOf(container.catalog),
                )
            else -> error("Unknown ViewModel ${modelClass.simpleName}")
        }
        return viewModel as T
    }
}
