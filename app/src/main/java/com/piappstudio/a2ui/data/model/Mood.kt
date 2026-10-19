package com.piappstudio.a2ui.data.model

enum class Mood(
    val displayName: String,
    val emoji: String,
    val colorHex: Long
) {
    PEACEFUL("Peaceful", "🌿", 0xFF4CAF50),
    SERENE("Serene", "🌊", 0xFF0288D1),
    REFLECTIVE("Reflective", "🌙", 0xFF7E57C2),
    GRATITUDE("Gratitude", "✨", 0xFFFFB300),
    RENEWED("Renewed", "🌸", 0xFFE91E63),
    CALM("Calm", "🍃", 0xFF009688);

    companion object {
        fun fromName(name: String?): Mood? {
            if (name == null) return null
            return entries.firstOrNull { 
                it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) 
            }
        }
    }
}
