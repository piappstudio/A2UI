package com.piappstudio.a2ui.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val content: String,
    val mood: Mood,
    val bannerImage: String? = null
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            return sdf.format(Date(dateMillis))
        }
}
