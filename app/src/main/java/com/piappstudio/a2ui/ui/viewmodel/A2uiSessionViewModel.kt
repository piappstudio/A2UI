package com.piappstudio.a2ui.ui.viewmodel

import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.protocol.A2uiClientErrorMessage
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import androidx.a2ui.model.protocol.A2uiDeleteSurfaceMessage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piappstudio.a2ui.a2ui.agent.AgentBackend
import com.piappstudio.a2ui.a2ui.agent.AgentRequest
import com.piappstudio.a2ui.a2ui.agent.AgentResponse
import com.piappstudio.a2ui.a2ui.agent.AgentWorkflow
import com.piappstudio.a2ui.a2ui.agent.MockAgentBackend
import com.piappstudio.a2ui.a2ui.dispatcher.A2uiActionDispatcher
import com.piappstudio.a2ui.security.LogRedactor
import com.piappstudio.a2ui.security.RegisteredActions
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AgentSessionUiState(
    val workflow: AgentWorkflow,
    val isLoading: Boolean = true,
    val error: String? = null,
    val statusMessage: String? = null,
    val closeRequested: Boolean = false,
    val pickDocument: Boolean = false,
    val attachedFileName: String? = null,
)

class A2uiSessionViewModel(
    private val workflow: AgentWorkflow,
    private val transactionId: String?,
    private val agent: AgentBackend,
    catalogs: List<A2uiCatalog>,
    private val dispatcher: A2uiActionDispatcher = A2uiActionDispatcher(),
) : ViewModel() {

    private val sessionId: String = UUID.randomUUID().toString()
    private val processor: A2uiMessageProcessor = A2uiMessageProcessor(catalogs = catalogs)
    val activeSurfaces: StateFlow<List<A2uiSurfaceModel>> = processor.activeSurfaces

    private val _uiState = MutableStateFlow(AgentSessionUiState(workflow = workflow))
    val uiState: StateFlow<AgentSessionUiState> = _uiState.asStateFlow()

    private var startJob: Job? = null

    init {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) { processor.collectMessages() }
        viewModelScope.launch {
            processor.outboundEvents.collect { message ->
                when (message) {
                    is A2uiClientEventMessage -> onClientEvent(message)
                    is A2uiClientErrorMessage -> {
                        _uiState.update {
                            it.copy(error = LogRedactor.redact(message.message), isLoading = false)
                        }
                    }
                }
            }
        }
        start()
    }

    fun start() {
        startJob?.cancel()
        startJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, closeRequested = false) }
            runCatching {
                agent.startWorkflow(
                    AgentRequest(
                        workflow = workflow,
                        action = "start",
                        context = emptyMap(),
                        sessionId = sessionId,
                        transactionId = transactionId,
                    ),
                )
            }.onSuccess { applyResponse(it) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "The agent failed to start.")
                    }
                }
        }
    }

    fun retry() = start()

    fun cancel() {
        viewModelScope.launch {
            agent.cancel(sessionId, workflow)
            processor.processMessage(A2uiDeleteSurfaceMessage(workflow.surfaceId))
            _uiState.update { it.copy(closeRequested = true, isLoading = false) }
        }
    }

    fun onDocumentPicked(fileName: String) {
        (agent as? MockAgentBackend)?.attachDocument(sessionId, fileName)
        _uiState.update { it.copy(pickDocument = false, attachedFileName = fileName) }
        viewModelScope.launch {
            processor.processMessage(
                com.piappstudio.a2ui.a2ui.protocol.updateData(
                    workflow.surfaceId,
                    mapOf("documentName" to fileName),
                    "/loan/documentName",
                ),
            )
        }
    }

    fun consumeDocumentPicker() {
        _uiState.update { it.copy(pickDocument = false) }
    }

    private fun onClientEvent(event: A2uiClientEventMessage) {
        val dispatched = dispatcher.dispatch(event).getOrElse { error ->
            _uiState.update { it.copy(error = error.message) }
            return
        }
        if (dispatched.actionId == RegisteredActions.CANCEL_WORKFLOW ||
            dispatched.actionId == RegisteredActions.CLOSE_WORKFLOW
        ) {
            cancel()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                agent.handleAction(
                    AgentRequest(
                        workflow = workflow,
                        action = dispatched.actionId,
                        context = dispatched.context,
                        dataModel = dispatched.dataModel,
                        sessionId = sessionId,
                        transactionId = transactionId,
                    ),
                )
            }.onSuccess { applyResponse(it) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "The agent failed.")
                    }
                }
        }
    }

    private fun applyResponse(response: AgentResponse) {
        response.messages.forEach { processor.processMessage(it) }
        _uiState.update {
            it.copy(
                isLoading = false,
                error = response.userMessage,
                statusMessage = response.userMessage,
                closeRequested = response.closeWorkflow,
                pickDocument = response.pickDocument,
            )
        }
    }
}
