package com.piappstudio.a2ui.a2ui.agent

import androidx.a2ui.model.protocol.A2uiComponentPayload
import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import com.piappstudio.a2ui.a2ui.protocol.button
import com.piappstudio.a2ui.a2ui.protocol.card
import com.piappstudio.a2ui.a2ui.protocol.checkBox
import com.piappstudio.a2ui.a2ui.protocol.choicePicker
import com.piappstudio.a2ui.a2ui.protocol.column
import com.piappstudio.a2ui.a2ui.protocol.createSurface
import com.piappstudio.a2ui.a2ui.protocol.divider
import com.piappstudio.a2ui.a2ui.protocol.documentAttachment
import com.piappstudio.a2ui.a2ui.protocol.emiSummary
import com.piappstudio.a2ui.a2ui.protocol.exchangeRate
import com.piappstudio.a2ui.a2ui.protocol.locationCard
import com.piappstudio.a2ui.a2ui.protocol.path
import com.piappstudio.a2ui.a2ui.protocol.row
import com.piappstudio.a2ui.a2ui.protocol.slider
import com.piappstudio.a2ui.a2ui.protocol.text
import com.piappstudio.a2ui.a2ui.protocol.textField
import com.piappstudio.a2ui.a2ui.protocol.updateComponents
import com.piappstudio.a2ui.a2ui.protocol.updateData
import com.piappstudio.a2ui.a2ui.protocol.verificationStatus
import com.piappstudio.a2ui.security.RegisteredActions

internal object WorkflowSurfaces {
    fun bootstrap(surfaceId: String, components: List<A2uiComponentPayload>, data: Map<String, Any?>):
        List<A2uiServerToClientMessage> = listOf(
        createSurface(surfaceId),
        updateComponents(surfaceId, components),
        updateData(surfaceId, data),
    )

    fun transferForm(surfaceId: String, accountOptions: List<Map<String, String>>): List<A2uiComponentPayload> =
        listOf(
            column("root", listOf("title", "subtitle", "form_card", "actions")),
            text("title", "Secure fund transfer", "h3"),
            text("subtitle", "DEMO DATA. The agent proposes UI; the bank authorizes the payment."),
            card("form_card", "form"),
            column(
                "form",
                listOf("src", "recipient", "account", "amount", "currency", "intl"),
            ),
            choicePicker("src", "Source account", "/draft/sourceAccountId", accountOptions),
            textField("recipient", "Beneficiary name", "/draft/recipientName"),
            textField("account", "Beneficiary account / IBAN", "/draft/recipientAccount"),
            textField("amount", "Amount", "/draft/amount", "number"),
            choicePicker(
                "currency",
                "Currency",
                "/draft/currency",
                listOf(
                    mapOf("label" to "USD", "value" to "USD"),
                    mapOf("label" to "EUR", "value" to "EUR"),
                ),
            ),
            checkBox("intl", "International payment", "/draft/isInternational"),
            row("actions", listOf("cancel_btn", "review_btn")),
            button("cancel_btn", "cancel_lbl", RegisteredActions.CANCEL_WORKFLOW, variant = "default"),
            text("cancel_lbl", "Cancel"),
            button(
                "review_btn",
                "review_lbl",
                RegisteredActions.REVIEW_TRANSFER,
                context = mapOf(
                    "recipientName" to path("/draft/recipientName"),
                    "recipientAccount" to path("/draft/recipientAccount"),
                    "amount" to path("/draft/amount"),
                    "sourceAccountId" to path("/draft/sourceAccountId"),
                    "currency" to path("/draft/currency"),
                    "isInternational" to path("/draft/isInternational"),
                ),
            ),
            text("review_lbl", "Review transfer"),
        )

    fun transferReview(surfaceId: String, needsVerification: Boolean): List<A2uiComponentPayload> {
        val extra = buildList {
            add("quote_card")
            add("fx")
            if (needsVerification) add("verify_status")
            add("confirm_copy")
            add("actions")
        }
        return listOf(
            column("root", listOf("title", "subtitle") + extra),
            text("title", "Confirm transfer", "h3"),
            text("subtitle", path("/review/headline")),
            card("quote_card", "quote_col"),
            column("quote_col", listOf("q_recipient", "q_account", "q_amount", "q_fee", "q_risk")),
            text("q_recipient", path("/review/recipient")),
            text("q_account", path("/review/account")),
            text("q_amount", path("/review/amount"), "h4"),
            text("q_fee", path("/review/fee")),
            text("q_risk", path("/review/risk")),
            exchangeRate("fx"),
            verificationStatus("verify_status"),
            text("confirm_copy", "Confirming submits a pending transfer to the trusted backend. DEMO only."),
            row("actions", listOf("back_btn", "confirm_btn")),
            button("back_btn", "back_lbl", RegisteredActions.CANCEL_WORKFLOW, variant = "default"),
            text("back_lbl", "Cancel"),
            button(
                "confirm_btn",
                "confirm_lbl",
                if (needsVerification) RegisteredActions.START_MOCK_VERIFICATION else RegisteredActions.CONFIRM_TRANSFER,
            ),
            text("confirm_lbl", if (needsVerification) "Continue to demo verification" else "Confirm transfer"),
        )
    }

    fun mockVerification(): List<A2uiComponentPayload> = listOf(
        column("root", listOf("title", "subtitle", "status", "ack", "actions")),
        text("title", "Demo verification", "h3"),
        text(
            "subtitle",
            "This is a clearly labeled mock verification flow. It does not perform biometric authentication.",
        ),
        verificationStatus("status"),
        checkBox("ack", "I understand this is mock demo verification, not a real biometric check.", "/verification/acknowledged"),
        row("actions", listOf("cancel_btn", "complete_btn")),
        button("cancel_btn", "cancel_lbl", RegisteredActions.CANCEL_WORKFLOW, variant = "default"),
        text("cancel_lbl", "Cancel"),
        button("complete_btn", "complete_lbl", RegisteredActions.COMPLETE_MOCK_VERIFICATION),
        text("complete_lbl", "Complete mock verification"),
    )

    fun resultScreen(): List<A2uiComponentPayload> = listOf(
        column("root", listOf("title", "body", "ref", "close_btn")),
        text("title", path("/result/title"), "h3"),
        text("body", path("/result/body")),
        text("ref", path("/result/reference")),
        button("close_btn", "close_lbl", RegisteredActions.CLOSE_WORKFLOW),
        text("close_lbl", "Back to dashboard"),
    )

    fun loanForm(isCreditLimit: Boolean): List<A2uiComponentPayload> = listOf(
        column(
            "root",
            listOf("title", "subtitle", "amount", "tenure", "income", "employment", "emi", "docs", "consent", "actions"),
        ),
        text("title", if (isCreditLimit) "Credit-limit increase" else "Personal loan", "h3"),
        text("subtitle", "DEMO DATA. EMI values are illustrative and are not a bank offer."),
        slider("amount", "Requested amount (USD)", "/loan/amount", 500, if (isCreditLimit) 8000 else 25000),
        slider("tenure", "Tenure (months)", "/loan/tenureMonths", 6, 60),
        textField("income", "Annual income (USD)", "/loan/annualIncome", "number"),
        choicePicker(
            "employment",
            "Employment status",
            "/loan/employmentStatus",
            listOf(
                mapOf("label" to "Employed", "value" to "employed"),
                mapOf("label" to "Self-employed", "value" to "self-employed"),
                mapOf("label" to "Unemployed", "value" to "unemployed"),
            ),
        ),
        emiSummary("emi"),
        documentAttachment("docs"),
        checkBox("consent", "I consent to a demo eligibility check. This is not a credit application.", "/loan/consent"),
        row("actions", listOf("cancel_btn", "submit_btn")),
        button("cancel_btn", "cancel_lbl", RegisteredActions.CANCEL_WORKFLOW, variant = "default"),
        text("cancel_lbl", "Cancel"),
        button(
            "submit_btn",
            "submit_lbl",
            if (isCreditLimit) RegisteredActions.SUBMIT_CREDIT_LIMIT else RegisteredActions.SUBMIT_LOAN,
            context = mapOf(
                "amount" to path("/loan/amount"),
                "tenureMonths" to path("/loan/tenureMonths"),
                "annualIncome" to path("/loan/annualIncome"),
                "employmentStatus" to path("/loan/employmentStatus"),
                "consent" to path("/loan/consent"),
                "documentName" to path("/loan/documentName"),
            ),
        ),
        text("submit_lbl", "Submit demo application"),
    )

    fun fraudForm(): List<A2uiComponentPayload> = listOf(
        column(
            "root",
            listOf("title", "subtitle", "tx_card", "location", "reason", "details", "confirm", "actions"),
        ),
        text("title", "Investigate transaction", "h3"),
        text("subtitle", "DEMO DATA. The backend verifies ownership and eligibility before a dispute is created."),
        card("tx_card", "tx_col"),
        column("tx_col", listOf("merchant", "amount", "date", "status", "contact")),
        text("merchant", path("/transaction/merchant"), "h4"),
        text("amount", path("/transaction/amount")),
        text("date", path("/transaction/date")),
        text("status", path("/transaction/status")),
        text("contact", path("/transaction/contact")),
        locationCard("location"),
        choicePicker(
            "reason",
            "Dispute reason",
            "/dispute/reason",
            listOf(
                mapOf("label" to "I do not recognize this charge", "value" to "unrecognized"),
                mapOf("label" to "Duplicate charge", "value" to "duplicate"),
                mapOf("label" to "Goods or services not received", "value" to "not_received"),
                mapOf("label" to "Incorrect amount", "value" to "incorrect_amount"),
            ),
        ),
        textField("details", "Additional details", "/dispute/details", "longText"),
        checkBox("confirm", "I confirm I want to dispute this transaction.", "/dispute/confirmed"),
        row("actions", listOf("cancel_btn", "submit_btn")),
        button("cancel_btn", "cancel_lbl", RegisteredActions.CANCEL_WORKFLOW, variant = "default"),
        text("cancel_lbl", "Cancel"),
        button(
            "submit_btn",
            "submit_lbl",
            RegisteredActions.SUBMIT_DISPUTE,
            context = mapOf(
                "reason" to path("/dispute/reason"),
                "details" to path("/dispute/details"),
                "confirmed" to path("/dispute/confirmed"),
                "transactionId" to path("/transaction/id"),
            ),
        ),
        text("submit_lbl", "Submit dispute"),
    )

    fun assistantHome(): List<A2uiComponentPayload> = listOf(
        column("root", listOf("title", "body", "hint", "close_btn")),
        text("title", "Banking assistant", "h3"),
        text("body", "I can help with transfers, loans, credit-limit increases, and fraud investigations."),
        text("hint", "Use the dashboard quick actions to start a workflow. The assistant cannot move money on its own."),
        button("close_btn", "close_lbl", RegisteredActions.CLOSE_WORKFLOW),
        text("close_lbl", "Close"),
    )

    fun errorSurface(message: String): List<A2uiComponentPayload> = listOf(
        column("root", listOf("title", "body", "retry_btn")),
        text("title", "Something went wrong", "h3"),
        text("body", message),
        button("retry_btn", "retry_lbl", RegisteredActions.RETRY_WORKFLOW),
        text("retry_lbl", "Retry"),
    )

    fun unsupportedComponentDemo(surfaceId: String): List<A2uiServerToClientMessage> = listOf(
        createSurface(surfaceId),
        updateComponents(
            surfaceId,
            listOf(
                A2uiComponentPayload(
                    id = "root",
                    type = "NotARealComponent",
                    properties = mapOf("text" to "This should fail safely."),
                ),
            ),
        ),
    )
}
