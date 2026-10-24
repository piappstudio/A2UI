package com.piappstudio.a2ui.security

object RegisteredActions {
    const val REVIEW_TRANSFER = "reviewTransfer"
    const val CONFIRM_TRANSFER = "confirmTransfer"
    const val CANCEL_WORKFLOW = "cancelWorkflow"
    const val START_MOCK_VERIFICATION = "startMockVerification"
    const val COMPLETE_MOCK_VERIFICATION = "completeMockVerification"
    const val RECALCULATE_EMI = "recalculateEmi"
    const val SUBMIT_LOAN = "submitLoan"
    const val SUBMIT_CREDIT_LIMIT = "submitCreditLimit"
    const val ATTACH_DOCUMENT = "attachDocument"
    const val SUBMIT_DISPUTE = "submitDispute"
    const val CLOSE_WORKFLOW = "closeWorkflow"
    const val RETRY_WORKFLOW = "retryWorkflow"

    val all: Set<String> = setOf(
        REVIEW_TRANSFER,
        CONFIRM_TRANSFER,
        CANCEL_WORKFLOW,
        START_MOCK_VERIFICATION,
        COMPLETE_MOCK_VERIFICATION,
        RECALCULATE_EMI,
        SUBMIT_LOAN,
        SUBMIT_CREDIT_LIMIT,
        ATTACH_DOCUMENT,
        SUBMIT_DISPUTE,
        CLOSE_WORKFLOW,
        RETRY_WORKFLOW,
    )

    fun isRegistered(actionId: String): Boolean = actionId in all
}
