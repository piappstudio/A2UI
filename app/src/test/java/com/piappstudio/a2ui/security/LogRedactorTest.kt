package com.piappstudio.a2ui.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogRedactorTest {
    @Test
    fun redactsAccountNumbersAndEmails() {
        val raw = "Paid 4892107733 to alex.rivera@example.com"
        val redacted = LogRedactor.redact(raw)
        assertFalse(redacted.contains("4892107733"))
        assertFalse(redacted.contains("alex.rivera@example.com"))
        assertTrue(redacted.contains("••••"))
        assertTrue(redacted.contains("[redacted-email]"))
    }
}
