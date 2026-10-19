package com.piappstudio.a2ui.data.repository

import com.piappstudio.a2ui.data.model.JournalEntry
import com.piappstudio.a2ui.data.model.Mood
import com.piappstudio.a2ui.data.sample.SampleJournalData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    fun getFilteredEntries(mood: Mood?, searchQuery: String? = null): Flow<List<JournalEntry>>
    fun getEntryById(id: String): JournalEntry?
    fun addEntry(entry: JournalEntry)
    fun deleteEntry(id: String)
    fun updateEntry(entry: JournalEntry)
}

class JournalRepositoryImpl(
    initialData: List<JournalEntry> = SampleJournalData.initialEntries
) : JournalRepository {

    private val entriesState = MutableStateFlow(initialData)

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        return entriesState.asStateFlow().map { list ->
            list.sortedByDescending { it.dateMillis }
        }
    }

    override fun getFilteredEntries(mood: Mood?, searchQuery: String?): Flow<List<JournalEntry>> {
        return entriesState.asStateFlow().map { list ->
            list.filter { entry ->
                val matchesMood = mood == null || entry.mood == mood
                val matchesQuery = searchQuery.isNullOrBlank() || 
                        entry.title.contains(searchQuery, ignoreCase = true) ||
                        entry.content.contains(searchQuery, ignoreCase = true)
                matchesMood && matchesQuery
            }.sortedByDescending { it.dateMillis }
        }
    }

    override fun getEntryById(id: String): JournalEntry? {
        return entriesState.value.firstOrNull { it.id == id }
    }

    override fun addEntry(entry: JournalEntry) {
        entriesState.update { current ->
            listOf(entry) + current.filterNot { it.id == entry.id }
        }
    }

    override fun deleteEntry(id: String) {
        entriesState.update { current ->
            current.filterNot { it.id == id }
        }
    }

    override fun updateEntry(entry: JournalEntry) {
        entriesState.update { current ->
            current.map { existing ->
                if (existing.id == entry.id) entry else existing
            }
        }
    }
}
