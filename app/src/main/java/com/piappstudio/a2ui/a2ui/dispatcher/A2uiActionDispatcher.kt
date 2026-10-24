package com.piappstudio.a2ui.a2ui.dispatcher

import androidx.a2ui.model.protocol.A2uiClientDataModel
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import com.piappstudio.a2ui.security.RegisteredActions

data class DispatchedAction(
    val actionId: String,
    val context: Map<String, Any?>,
    val dataModel: Map<String, Any?>,
    val surfaceId: String,
)

class A2uiActionDispatcher {
    fun dispatch(event: A2uiClientEventMessage): Result<DispatchedAction> {
        if (!RegisteredActions.isRegistered(event.type)) {
            return Result.failure(
                IllegalArgumentException("Unregistered A2UI action '${event.type}' was ignored."),
            )
        }
        return Result.success(
            DispatchedAction(
                actionId = event.type,
                context = event.context,
                dataModel = surfaceData(event.clientDataModel, event.surfaceId),
                surfaceId = event.surfaceId,
            ),
        )
    }

    private fun surfaceData(model: A2uiClientDataModel?, surfaceId: String): Map<String, Any?> {
        val root = model?.surfaces?.get(surfaceId) ?: return emptyMap()
        @Suppress("UNCHECKED_CAST")
        return root as? Map<String, Any?> ?: emptyMap()
    }
}
