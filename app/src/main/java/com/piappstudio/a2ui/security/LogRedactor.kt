package com.piappstudio.a2ui.security

object LogRedactor {
    private val accountPattern = Regex("""\b\d{8,}\b""")
    private val emailPattern = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")

    fun redact(value: String): String {
        return value
            .replace(accountPattern, "••••")
            .replace(emailPattern, "[redacted-email]")
    }
}
