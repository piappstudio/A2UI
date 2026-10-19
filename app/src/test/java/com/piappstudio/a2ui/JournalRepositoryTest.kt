package com.piappstudio.a2ui

import com.piappstudio.a2ui.data.model.JournalEntry
import com.piappstudio.a2ui.data.model.Mood
import com.piappstudio.a2ui.data.repository.JournalRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JournalRepositoryTest {

    @Test
    fun `initial sample data is loaded correctly`() = runTest {
        val repo = JournalRepositoryImpl()
        val entries = repo.getAllEntries().first()
        assertTrue(entries.isNotEmpty())
        assertTrue(entries.size >= 5)
    }

    @Test
    fun `filterByMood returns only entries matching specified mood`() = runTest {
        val repo = JournalRepositoryImpl()
        val sereneEntries = repo.getFilteredEntries(mood = Mood.SERENE).first()
        assertTrue(sereneEntries.all { it.mood == Mood.SERENE })
    }

    @Test
    fun `addEntry adds new entry to repository`() = runTest {
        val repo = JournalRepositoryImpl()
        val newEntry = JournalEntry(
            id = "test-custom-1",
            title = "Tranquil Waterfall Path",
            content = "Found a gentle cascade in the forest.",
            mood = Mood.CALM
        )
        repo.addEntry(newEntry)

        val retrieved = repo.getEntryById("test-custom-1")
        assertNotNull(retrieved)
        assertEquals("Tranquil Waterfall Path", retrieved?.title)
    }

    @Test
    fun `deleteEntry removes entry from repository`() = runTest {
        val repo = JournalRepositoryImpl()
        val initialEntries = repo.getAllEntries().first()
        val targetId = initialEntries.first().id

        repo.deleteEntry(targetId)

        val afterDelete = repo.getAllEntries().first()
        assertNull(repo.getEntryById(targetId))
        assertEquals(initialEntries.size - 1, afterDelete.size)
    }

    @Test
    fun `search query filters entries by title and content`() = runTest {
        val repo = JournalRepositoryImpl()
        val searchResults = repo.getFilteredEntries(mood = null, searchQuery = "Pine Needles").first()
        assertEquals(1, searchResults.size)
        assertEquals("Morning Dew on Pine Needles", searchResults.first().title)
    }
}
