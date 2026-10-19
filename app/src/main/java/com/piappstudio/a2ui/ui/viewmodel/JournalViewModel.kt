package com.piappstudio.a2ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.a2ui.compose.runtime.a2uiRuntimeMessageProcessor
import androidx.a2ui.compose.runtime.A2uiRuntimeCatalog
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.a2ui.model.catalog.A2uiFunction
import androidx.a2ui.model.processor.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.protocol.A2uiCreateSurfaceMessage
import androidx.a2ui.model.protocol.A2uiUpdateComponentsMessage
import androidx.a2ui.model.protocol.A2uiComponentPayload
import com.piappstudio.a2ui.data.model.JournalEntry
import com.piappstudio.a2ui.data.model.Mood
import com.piappstudio.a2ui.data.repository.JournalRepository
import com.piappstudio.a2ui.data.repository.JournalRepositoryImpl
import kotlinx.coroutines.flow.*

data class JournalUiState(
    val entries: List<JournalEntry> = emptyList(),
    val allEntries: List<JournalEntry> = emptyList(),
    val selectedMood: Mood? = null,
    val searchQuery: String = "",
    val isAddEntryDialogOpen: Boolean = false,
    val selectedEntry: JournalEntry? = null
)

class JournalViewModel(
    private val repository: JournalRepository = JournalRepositoryImpl()
) : ViewModel() {

    val catalog: A2uiCatalog = A2uiCatalog(
        catalogId = "journal_catalog",
        components = emptyList<A2uiComponent>(),
        functions = emptyList<A2uiFunction>(),
        themeSchema = null,
        isInline = true
    )

    val messageProcessor: A2uiMessageProcessor = a2uiRuntimeMessageProcessor(
        listOf(catalog as A2uiRuntimeCatalog),
        emptyList()
    )

    val activeSurfaces: StateFlow<List<A2uiSurfaceModel>> = messageProcessor.activeSurfaces

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        createSurface()
        observeJournalData()
    }

    private fun createSurface() {
        messageProcessor.processMessage(
            A2uiCreateSurfaceMessage(
                surfaceId = "journal_surface",
                catalogId = catalog.id,
                theme = emptyMap(),
                shouldSendDataModel = true
            )
        )
    }

    private fun observeJournalData() {
        combine(
            repository.getAllEntries(),
            _uiState.map { it.selectedMood }.distinctUntilChanged(),
            _uiState.map { it.searchQuery }.distinctUntilChanged()
        ) { entries, mood, query ->
            Triple(entries, mood, query)
        }.onEach { (entries, mood, query) ->
            val filtered = entries.filter { entry ->
                val matchesMood = mood == null || entry.mood == mood
                val matchesQuery = query.isBlank() ||
                    entry.title.contains(query, ignoreCase = true) ||
                    entry.content.contains(query, ignoreCase = true)
                matchesMood && matchesQuery
            }
            updateA2UIComponents(filtered)
            _uiState.update { it.copy(entries = filtered, allEntries = entries) }
        }.launchIn(viewModelScope)
    }

    private fun updateA2UIComponents(entries: List<JournalEntry>) {
        val entryPayloads = entries.map { entry ->
            A2uiComponentPayload(
                id = "card_${entry.id}",
                type = "Card",
                properties = mapOf(
                    "entryId" to entry.id,
                    "title" to entry.title,
                    "content" to entry.content,
                    "mood" to entry.mood.displayName,
                    "bannerImage" to (entry.bannerImage ?: "")
                )
            )
        }

        val columnPayload = A2uiComponentPayload(
            id = "root_column",
            type = "Column",
            properties = mapOf(
                "children" to entryPayloads.map { it.id }
            )
        )

        messageProcessor.processMessage(
            A2uiUpdateComponentsMessage(
                surfaceId = "journal_surface",
                components = listOf(columnPayload) + entryPayloads
            )
        )
    }

    fun selectMoodFilter(mood: Mood?) {
        val newMood = if (_uiState.value.selectedMood == mood) null else mood
        _uiState.update { it.copy(selectedMood = newMood) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleAddDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isAddEntryDialogOpen = isOpen) }
    }

    fun selectJournalEntry(entry: JournalEntry?) {
        _uiState.update { it.copy(selectedEntry = entry) }
    }

    fun addJournalEntry(title: String, content: String, mood: Mood, bannerImage: String? = null) {
        val entry = JournalEntry(
            title = title,
            content = content,
            mood = mood,
            bannerImage = bannerImage
        )
        repository.addEntry(entry)
    }

    fun deleteJournalEntry(id: String) {
        repository.deleteEntry(id)
        if (_uiState.value.selectedEntry?.id == id) {
            selectJournalEntry(null)
        }
    }
}
