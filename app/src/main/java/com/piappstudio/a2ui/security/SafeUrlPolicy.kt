package com.piappstudio.a2ui.security

import android.net.Uri

object SafeUrlPolicy {
    private val allowedHosts = setOf(
        "www.trustbank.example",
        "trustbank.example",
        "support.trustbank.example",
    )

    fun isAllowed(url: String): Boolean {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return false
        val scheme = uri.scheme?.lowercase().orEmpty()
        if (scheme != "https") return false
        val host = uri.host?.lowercase().orEmpty()
        return host in allowedHosts
    }
}
