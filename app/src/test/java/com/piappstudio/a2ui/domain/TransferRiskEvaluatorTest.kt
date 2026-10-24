package com.piappstudio.a2ui.domain

import com.piappstudio.a2ui.data.model.TransferRiskTier
import org.junit.Assert.assertEquals
import org.junit.Test

class TransferRiskEvaluatorTest {
    @Test
    fun smallDomesticTransferIsLowRisk() {
        assertEquals(TransferRiskTier.LOW, TransferRiskEvaluator.tier(50.0, false))
    }

    @Test
    fun largeDomesticTransferRequiresVerification() {
        assertEquals(TransferRiskTier.VERIFICATION_REQUIRED, TransferRiskEvaluator.tier(5_000.0, false))
    }

    @Test
    fun internationalHighValueGoesToManualReview() {
        assertEquals(TransferRiskTier.HIGH_REVIEW, TransferRiskEvaluator.tier(5_000.0, true))
    }

    @Test
    fun nonPositiveAmountIsRejected() {
        assertEquals(TransferRiskTier.REJECTED, TransferRiskEvaluator.tier(0.0, false))
    }
}
