package com.piappstudio.a2ui.a2ui.agent

interface AgentBackend {
    suspend fun startWorkflow(request: AgentRequest): AgentResponse
    suspend fun handleAction(request: AgentRequest): AgentResponse
    suspend fun cancel(sessionId: String, workflow: AgentWorkflow)
}
