package com.piappstudio.a2ui.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.piappstudio.a2ui.a2ui.agent.AgentWorkflow
import kotlinx.serialization.Serializable

@Serializable
sealed interface TrustBankRoute : NavKey

@Serializable
data object LoginRoute : TrustBankRoute

@Serializable
data object DashboardRoute : TrustBankRoute

@Serializable
data class AgentRoute(
    val workflowName: String,
    val transactionId: String? = null,
) : TrustBankRoute {
    val workflow: AgentWorkflow
        get() = AgentWorkflow.valueOf(workflowName)
}
