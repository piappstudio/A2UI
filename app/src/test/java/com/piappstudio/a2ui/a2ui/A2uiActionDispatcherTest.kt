package com.piappstudio.a2ui.a2ui

import androidx.a2ui.model.protocol.A2uiClientEventMessage
import com.piappstudio.a2ui.a2ui.dispatcher.A2uiActionDispatcher
import com.piappstudio.a2ui.security.RegisteredActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class A2uiActionDispatcherTest {
    private val dispatcher = A2uiActionDispatcher()

    @Test
    fun registeredActionIsAccepted() {
        val event = A2uiClientEventMessage(
            type = RegisteredActions.CONFIRM_TRANSFER,
            surfaceId = "wf_transfer",
            componentId = "confirm_btn",
            timestamp = 1L,
            context = mapOf("amount" to "25"),
        )
        val result = dispatcher.dispatch(event)
        assertTrue(result.isSuccess)
        assertEquals(RegisteredActions.CONFIRM_TRANSFER, result.getOrThrow().actionId)
    }

    @Test
    fun unregisteredActionIsRejected() {
        val event = A2uiClientEventMessage(
            type = "navigateToAdmin",
            surfaceId = "wf_transfer",
            componentId = "evil",
            timestamp = 1L,
        )
        val result = dispatcher.dispatch(event)
        assertTrue(result.isFailure)
    }
}
