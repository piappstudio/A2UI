package com.piappstudio.a2ui.a2ui

import androidx.a2ui.model.protocol.A2uiCreateSurfaceMessage
import androidx.a2ui.model.protocol.A2uiUpdateComponentsMessage
import com.piappstudio.a2ui.a2ui.agent.WorkflowSurfaces
import com.piappstudio.a2ui.a2ui.catalog.BANKING_CATALOG_ID
import com.piappstudio.a2ui.a2ui.protocol.createSurface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class A2uiMessageFactoryTest {
    @Test
    fun createSurfaceUsesBankingCatalog() {
        val message = createSurface("wf_transfer") as A2uiCreateSurfaceMessage
        assertEquals("wf_transfer", message.surfaceId)
        assertEquals(BANKING_CATALOG_ID, message.catalogId)
        assertTrue(message.shouldSendDataModel)
    }

    @Test
    fun transferFormIncludesRootAndRequiredFields() {
        val components = WorkflowSurfaces.transferForm(
            "wf_transfer",
            listOf(mapOf("label" to "Checking", "value" to "acc_1")),
        )
        assertEquals("root", components.first().id)
        assertTrue(components.any { it.type == "TextField" && it.id == "amount" })
        assertTrue(components.any { it.type == "Button" && it.id == "review_btn" })
    }

    @Test
    fun loanFormIncludesEmiAndDocumentComponents() {
        val components = WorkflowSurfaces.loanForm(isCreditLimit = false)
        assertTrue(components.any { it.type == "EmiSummary" })
        assertTrue(components.any { it.type == "DocumentAttachment" })
        assertTrue(components.any { it.type == "Slider" })
    }

    @Test
    fun fraudFormIncludesLocationCard() {
        val components = WorkflowSurfaces.fraudForm()
        assertTrue(components.any { it.type == "TransactionLocationCard" })
        val update = WorkflowSurfaces.bootstrap("wf_fraud", components, emptyMap())
        assertTrue(update[1] is A2uiUpdateComponentsMessage)
    }

    @Test
    fun unsupportedComponentDemoUsesUnknownType() {
        val messages = WorkflowSurfaces.unsupportedComponentDemo("wf_error")
        val update = messages[1] as A2uiUpdateComponentsMessage
        assertEquals("NotARealComponent", update.components.first().type)
    }
}
