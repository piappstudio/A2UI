package com.piappstudio.a2ui.a2ui

import com.piappstudio.a2ui.a2ui.agent.AgentRequest
import com.piappstudio.a2ui.a2ui.agent.AgentWorkflow
import com.piappstudio.a2ui.a2ui.agent.MockAgentBackend
import com.piappstudio.a2ui.data.repository.BankingRepositoryImpl
import com.piappstudio.a2ui.security.RegisteredActions
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockAgentBackendTest {
    @Test
    fun startTransferReturnsSurfaceMessages() = runTest {
        val agent = MockAgentBackend(BankingRepositoryImpl())
        val response = agent.startWorkflow(
            AgentRequest(AgentWorkflow.TRANSFER, "start", emptyMap(), sessionId = "s1"),
        )
        assertTrue(response.messages.size >= 3)
        assertFalse(response.closeWorkflow)
    }

    @Test
    fun reviewWithoutAmountReturnsErrorSurface() = runTest {
        val agent = MockAgentBackend(BankingRepositoryImpl())
        agent.startWorkflow(AgentRequest(AgentWorkflow.TRANSFER, "start", emptyMap(), sessionId = "s2"))
        val response = agent.handleAction(
            AgentRequest(
                workflow = AgentWorkflow.TRANSFER,
                action = RegisteredActions.REVIEW_TRANSFER,
                context = mapOf("recipientName" to "", "amount" to ""),
                sessionId = "s2",
            ),
        )
        assertTrue(response.userMessage?.contains("beneficiary") == true)
    }

    @Test
    fun cancelClosesWorkflow() = runTest {
        val agent = MockAgentBackend(BankingRepositoryImpl())
        agent.startWorkflow(AgentRequest(AgentWorkflow.ASSISTANT, "start", emptyMap(), sessionId = "s3"))
        val response = agent.handleAction(
            AgentRequest(AgentWorkflow.ASSISTANT, RegisteredActions.CLOSE_WORKFLOW, emptyMap(), sessionId = "s3"),
        )
        assertTrue(response.closeWorkflow)
    }
}
