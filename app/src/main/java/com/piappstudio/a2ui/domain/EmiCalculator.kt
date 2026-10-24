package com.piappstudio.a2ui.domain

import com.piappstudio.a2ui.data.model.EmiEstimate
import kotlin.math.pow

object EmiCalculator {
    fun estimate(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int,
    ): EmiEstimate {
        val months = tenureMonths.coerceAtLeast(0)
        val monthlyPayment = monthlyPayment(principal, annualRatePercent, months)
        val totalPayable = if (monthlyPayment.isFinite()) monthlyPayment * months else 0.0
        return EmiEstimate(
            principal = principal,
            annualRatePercent = annualRatePercent,
            tenureMonths = months,
            monthlyPayment = monthlyPayment,
            totalPayable = totalPayable,
            isIllustrative = true,
        )
    }

    fun monthlyPayment(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int,
    ): Double {
        if (principal <= 0.0 || tenureMonths <= 0) return 0.0
        if (!principal.isFinite() || !annualRatePercent.isFinite()) return 0.0
        if (annualRatePercent == 0.0) return principal / tenureMonths
        if (annualRatePercent < 0.0) return 0.0
        val monthlyRate = annualRatePercent / 12.0 / 100.0
        val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
        val denominator = factor - 1.0
        if (denominator == 0.0) return principal / tenureMonths
        return principal * monthlyRate * factor / denominator
    }
}
