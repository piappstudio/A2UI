package com.piappstudio.a2ui.domain

import java.util.Locale

object MoneyFormat {
    fun currency(amount: Double, currencyCode: String = "USD"): String {
        val symbol = if (currencyCode == "USD") "$" else "$currencyCode "
        return symbol + String.format(Locale.US, "%,.2f", amount)
    }

    fun parseAmount(raw: String): Double? {
        val cleaned = raw.replace(",", "").replace("$", "").trim()
        if (cleaned.isEmpty()) return null
        return cleaned.toDoubleOrNull()
    }
}
