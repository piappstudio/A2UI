package com.piappstudio.a2ui.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmiCalculatorTest {
    @Test
    fun zeroInterestSplitsPrincipalEvenly() {
        val payment = EmiCalculator.monthlyPayment(12_000.0, 0.0, 12)
        assertEquals(1_000.0, payment, 0.01)
    }

    @Test
    fun positiveRateUsesAmortizingFormula() {
        val payment = EmiCalculator.monthlyPayment(10_000.0, 12.0, 12)
        assertEquals(888.49, payment, 0.05)
    }

    @Test
    fun invalidInputsReturnZero() {
        assertEquals(0.0, EmiCalculator.monthlyPayment(-1.0, 10.0, 12), 0.0)
        assertEquals(0.0, EmiCalculator.monthlyPayment(1000.0, 10.0, 0), 0.0)
        assertEquals(0.0, EmiCalculator.monthlyPayment(1000.0, -5.0, 12), 0.0)
    }

    @Test
    fun estimateMarksResultAsIllustrative() {
        val estimate = EmiCalculator.estimate(8_000.0, 9.75, 36)
        assertTrue(estimate.isIllustrative)
        assertTrue(estimate.monthlyPayment > 0.0)
        assertEquals(estimate.monthlyPayment * 36, estimate.totalPayable, 0.01)
    }
}
