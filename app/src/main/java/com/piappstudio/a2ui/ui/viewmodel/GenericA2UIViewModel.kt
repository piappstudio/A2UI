package com.piappstudio.a2ui.ui.viewmodel

import androidx.a2ui.compose.ui.A2uiMessageProcessor as createA2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Generic A2UI ViewModel supporting dynamic surface registries, data models,
 * and extensible protocol action dispatching without hardcoded page switches.
 */
open class GenericA2UIViewModel(
    catalogs: List<A2uiCatalog> = emptyList(),
) : ViewModel() {

    protected val messageProcessor: A2uiMessageProcessor = createA2uiMessageProcessor(
        catalogs = catalogs,
    )

    val activeSurfaces: StateFlow<List<A2uiSurfaceModel>> = messageProcessor.activeSurfaces

    private val _activeSurfacesMap = MutableStateFlow<Map<String, A2uiSurfaceModel>>(emptyMap())
    val activeSurfacesMap: StateFlow<Map<String, A2uiSurfaceModel>> = _activeSurfacesMap.asStateFlow()

    private val _protocolError = MutableStateFlow<String?>(null)
    val protocolError: StateFlow<String?> = _protocolError.asStateFlow()

    private val actionHandlers = mutableMapOf<String, (Map<String, Any?>) -> Unit>()
    private val dataModelRegistry = mutableMapOf<String, MutableMap<String, Any>>()

    init {
        viewModelScope.launch {
            messageProcessor.collectMessages()
        }
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            messageProcessor.outboundEvents.collect { message ->
                if (message is A2uiClientEventMessage) {
                    dispatchAction(message.type, message.context)
                }
            }
        }
        viewModelScope.launch {
            messageProcessor.activeSurfaces.collect { surfaces ->
                _activeSurfacesMap.value = surfaces.associateBy { it.id }
            }
        }
    }

    fun registerActionHandler(actionId: String, handler: (Map<String, Any?>) -> Unit) {
        actionHandlers[actionId] = handler
    }

    fun registerDataModel(surfaceId: String, path: String, value: Any) {
        val surfaceData = dataModelRegistry.getOrPut(surfaceId) { mutableMapOf() }
        surfaceData[path] = value
    }

    open fun dispatchAction(actionId: String, params: Map<String, Any?>) {
        val handler = actionHandlers[actionId]
        if (handler != null) {
            handler(params)
        } else {
            // Unhandled action or default behavior
        }
    }

    fun submit(message: A2uiServerToClientMessage) {
        try {
            messageProcessor.processMessage(message)
            _protocolError.value = null
        } catch (e: Exception) {
            _protocolError.value = e.message
        }
    }
}

typealias A2UISurfaceViewModel = GenericA2UIViewModel
