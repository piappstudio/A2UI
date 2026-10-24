package com.piappstudio.a2ui.domain

import com.piappstudio.a2ui.data.model.TransferRiskTier

object TransferRiskEvaluator {
    fun score(amount: Double, isInternational: Boolean): Double {
        var score = 0.08
        if (amount >= 1_000.0) score += 0.35
        if (amount >= 5_000.0) score += 0.25
        if (isInternational) score += 0.40
        return score.coerceIn(0.0, 0.99)
    }

    fun tier(amount: Double, isInternational: Boolean): TransferRiskTier {
        if (amount <= 0.0) return TransferRiskTier.REJECTED
        val value = score(amount, isInternational)
        return when {
            value >= 0.85 -> TransferRiskTier.HIGH_REVIEW
            value >= 0.50 -> TransferRiskTier.VERIFICATION_REQUIRED
            else -> TransferRiskTier.LOW
        }
    }

    fun explanation(tier: TransferRiskTier): String = when (tier) {
        TransferRiskTier.LOW -> "Demo risk engine classified this as a routine domestic payment."
        TransferRiskTier.VERIFICATION_REQUIRED ->
            "Demo risk engine requires extra confirmation because of amount or destination."
        TransferRiskTier.HIGH_REVIEW ->
            "Demo risk engine sent this payment for manual review. The agent cannot authorize it."
        TransferRiskTier.REJECTED -> "The trusted backend rejected this transfer request."
    }
}
