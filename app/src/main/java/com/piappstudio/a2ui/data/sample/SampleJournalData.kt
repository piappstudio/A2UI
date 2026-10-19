package com.piappstudio.a2ui.data.sample

import com.piappstudio.a2ui.data.model.JournalEntry
import com.piappstudio.a2ui.data.model.Mood

object SampleJournalData {
    val initialEntries = listOf(
        JournalEntry(
            id = "entry-1",
            title = "Morning Dew on Pine Needles",
            dateMillis = System.currentTimeMillis() - 86400000L * 2,
            content = "Woke up early to catch the sunrise over the mist-covered lake. The air was cool and crisp, carrying the soothing scent of damp earth and pine needles. Watching the light filter through the golden canopy brought a deep sense of stillness to my mind.",
            mood = Mood.PEACEFUL,
            bannerImage = "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=800&q=80"
        ),
        JournalEntry(
            id = "entry-2",
            title = "Ripples Across Lake Solitude",
            dateMillis = System.currentTimeMillis() - 86400000L * 1,
            content = "Sat by the crystal waters for an hour watching dragonflies dart gracefully across the surface. The gentle rhythm of water lapping against smoothly polished pebbles had an almost hypnotic, healing effect.",
            mood = Mood.SERENE,
            bannerImage = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80"
        ),
        JournalEntry(
            id = "entry-3",
            title = "Starlight in the Mountain Meadow",
            dateMillis = System.currentTimeMillis() - 3600000L * 5,
            content = "As twilight settled over the valley, the sky transformed into a deep indigo canvas dotted with brilliant stars. Surrounded by quiet, timeless mountains, I felt immense clarity and inner peace.",
            mood = Mood.REFLECTIVE,
            bannerImage = "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=800&q=80"
        ),
        JournalEntry(
            id = "entry-4",
            title = "Whispering Aspen Leaves",
            dateMillis = System.currentTimeMillis() - 1800000L * 2,
            content = "Walking along the forest trail, the golden aspen leaves flickered in the sunlight like tiny lanterns. The gentle breeze played a soft, rustling melody through the canopy.",
            mood = Mood.GRATITUDE,
            bannerImage = "https://images.unsplash.com/photo-1473448912268-2022ce9509d8?auto=format&fit=crop&w=800&q=80"
        ),
        JournalEntry(
            id = "entry-5",
            title = "Spring Rain in the Fern Glen",
            dateMillis = System.currentTimeMillis(),
            content = "Soft raindrops fell upon lush green fronds. Each droplet glistened like a tiny pearl in the quiet forest light. Nature renewing itself, refreshing the heart and soul.",
            mood = Mood.RENEWED,
            bannerImage = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?auto=format&fit=crop&w=800&q=80"
        ),
        JournalEntry(
            id = "entry-6",
            title = "Breeze Over Mossy Stones",
            dateMillis = System.currentTimeMillis() - 3600000L * 12,
            content = "Found a secluded stream tucked inside a cedar grove. Resting on moss-covered stone while listening to the rushing water cleared every lingering worry.",
            mood = Mood.CALM,
            bannerImage = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=800&q=80"
        )
    )
}
