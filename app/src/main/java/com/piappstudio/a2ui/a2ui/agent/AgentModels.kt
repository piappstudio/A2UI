package com.piappstudio.a2ui.a2ui.agent

import androidx.a2ui.model.protocol.A2uiServerToClientMessage

enum class AgentWorkflow(val surfaceId: String, val title: String) {
    TRANSFER("wf_transfer", "Transfer money"),
    LOAN("wf_loan", "Personal loan"),
    CREDIT_LIMIT("wf_credit_limit", "Increase credit limit"),
    FRAUD("wf_fraud", "Report fraud"),
    ASSISTANT("wf_assistant", "Banking assistant"),
}

data class AgentRequest(
    val workflow: AgentWorkflow,
    val action: String,
    val context: Map<String, Any?>,
    val dataModel: Map<String, Any?> = emptyMap(),
    val sessionId: String,
    val transactionId: String? = null,
)

data class AgentResponse(
    val messages: List<A2uiServerToClientMessage>,
    val closeWorkflow: Boolean = false,
    val userMessage: String? = null,
    val pickDocument: Boolean = false,
)
