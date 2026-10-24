package com.piappstudio.a2ui.a2ui.protocol

import androidx.a2ui.model.protocol.A2uiComponentPayload
import androidx.a2ui.model.protocol.A2uiCreateSurfaceMessage
import androidx.a2ui.model.protocol.A2uiDeleteSurfaceMessage
import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import androidx.a2ui.model.protocol.A2uiUpdateComponentsMessage
import androidx.a2ui.model.protocol.A2uiUpdateDataModelMessage
import com.piappstudio.a2ui.a2ui.catalog.BANKING_CATALOG_ID

fun createSurface(surfaceId: String): A2uiServerToClientMessage =
    A2uiCreateSurfaceMessage(
        surfaceId = surfaceId,
        catalogId = BANKING_CATALOG_ID,
        theme = mapOf(
            "primaryColor" to "#0B4F6C",
            "agentDisplayName" to "TrustBank Assistant",
        ),
        shouldSendDataModel = true,
    )

fun updateComponents(
    surfaceId: String,
    components: List<A2uiComponentPayload>,
): A2uiServerToClientMessage = A2uiUpdateComponentsMessage(surfaceId, components)

fun updateData(
    surfaceId: String,
    value: Map<String, Any?>,
    path: String = "/",
): A2uiServerToClientMessage = A2uiUpdateDataModelMessage(surfaceId, path, value)

fun deleteSurface(surfaceId: String): A2uiServerToClientMessage =
    A2uiDeleteSurfaceMessage(surfaceId)

fun path(pointer: String): Map<String, String> = mapOf("path" to pointer)

fun eventAction(name: String, context: Map<String, Any?> = emptyMap()): Map<String, Any?> =
    mapOf("event" to mapOf("name" to name, "context" to context))

fun column(id: String, children: List<String>): A2uiComponentPayload =
    A2uiComponentPayload(id, "Column", mapOf("children" to children, "align" to "stretch"))

fun row(id: String, children: List<String>): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "Row",
        mapOf("children" to children, "justify" to "spaceBetween", "align" to "center"),
    )

fun card(id: String, childId: String): A2uiComponentPayload =
    A2uiComponentPayload(id, "Card", mapOf("child" to childId))

fun text(
    id: String,
    value: Any,
    variant: String = "body",
): A2uiComponentPayload =
    A2uiComponentPayload(id, "Text", mapOf("text" to value, "variant" to variant))

fun divider(id: String): A2uiComponentPayload =
    A2uiComponentPayload(id, "Divider", emptyMap())

fun textField(
    id: String,
    label: String,
    valuePath: String,
    variant: String = "shortText",
): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "TextField",
        mapOf(
            "label" to label,
            "value" to path(valuePath),
            "variant" to variant,
        ),
    )

fun checkBox(id: String, label: String, valuePath: String): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "CheckBox",
        mapOf("label" to label, "value" to path(valuePath)),
    )

fun choicePicker(
    id: String,
    label: String,
    valuePath: String,
    options: List<Map<String, String>>,
): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "ChoicePicker",
        mapOf(
            "label" to label,
            "value" to path(valuePath),
            "options" to options,
            "variant" to "mutuallyExclusive",
            "displayStyle" to "chips",
        ),
    )

fun slider(
    id: String,
    label: String,
    valuePath: String,
    min: Number,
    max: Number,
): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "Slider",
        mapOf(
            "label" to label,
            "value" to path(valuePath),
            "min" to min,
            "max" to max,
        ),
    )

fun button(
    id: String,
    childId: String,
    event: String,
    context: Map<String, Any?> = emptyMap(),
    variant: String = "primary",
): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "Button",
        mapOf(
            "child" to childId,
            "variant" to variant,
            "action" to eventAction(event, context),
        ),
    )

fun emiSummary(id: String): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "EmiSummary",
        mapOf(
            "amount" to path("/loan/amount"),
            "annualRatePercent" to path("/loan/annualRatePercent"),
            "tenureMonths" to path("/loan/tenureMonths"),
        ),
    )

fun exchangeRate(id: String): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "ExchangeRateSummary",
        mapOf(
            "fromCurrency" to path("/quote/fromCurrency"),
            "toCurrency" to path("/quote/toCurrency"),
            "rate" to path("/quote/rate"),
            "convertedAmount" to path("/quote/convertedAmount"),
            "fee" to path("/quote/fee"),
            "delivery" to path("/quote/delivery"),
        ),
    )

fun verificationStatus(id: String): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "VerificationStatus",
        mapOf(
            "status" to path("/verification/status"),
            "detail" to path("/verification/detail"),
        ),
    )

fun locationCard(id: String): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "TransactionLocationCard",
        mapOf(
            "city" to path("/transaction/city"),
            "country" to path("/transaction/country"),
            "verified" to path("/transaction/locationVerified"),
            "note" to path("/transaction/locationNote"),
        ),
    )

fun documentAttachment(id: String): A2uiComponentPayload =
    A2uiComponentPayload(
        id,
        "DocumentAttachment",
        mapOf(
            "label" to "Income document",
            "fileName" to path("/loan/documentName"),
            "attachEvent" to "attachDocument",
        ),
    )
