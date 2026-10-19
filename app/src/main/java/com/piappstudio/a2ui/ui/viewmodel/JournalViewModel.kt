package com.piappstudio.a2ui.ui.viewmodel

import androidx.a2ui.compose.runtime.A2uiMessageParser
import androidx.a2ui.compose.ui.A2uiMessageProcessor as createA2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.processor.processInput
import androidx.a2ui.model.protocol.A2uiClientErrorMessage
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import androidx.a2ui.model.protocol.A2uiClientToServerMessage
import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piappstudio.a2ui.a2ui.agent.JournalSurfaceAgent
import com.piappstudio.a2ui.a2ui.catalog.SanctuaryEvents
import com.piappstudio.a2ui.a2ui.catalog.sanctuaryJournalCatalog
import com.piappstudio.a2ui.data.model.JournalEntry
import com.piappstudio.a2ui.data.model.Mood
import com.piappstudio.a2ui.data.repository.JournalRepository
import com.piappstudio.a2ui.data.repository.JournalRepositoryImpl
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JournalUiState(
    val entries: List<JournalEntry> = emptyList(),
    val allEntries: List<JournalEntry> = emptyList(),
    val selectedMood: Mood? = null,
    val searchQuery: String = "",
    val isAddEntryDialogOpen: Boolean = false,
    val selectedEntry: JournalEntry? = null,
)

class JournalViewModel(
    private val repository: JournalRepository = JournalRepositoryImpl(),
) : ViewModel() {

    private val catalog = sanctuaryJournalCatalog()

    /**
     * Compose-backed processor. Messages stay queued until [A2uiMessageProcessor.collectMessages]
     * is running, which is started in [init].
     */
    val messageProcessor: A2uiMessageProcessor = createA2uiMessageProcessor(
        catalogs = listOf(catalog),
    )

    private val jsonParser by lazy { A2uiMessageParser() }

    val activeSurfaces: StateFlow<List<A2uiSurfaceModel>> = messageProcessor.activeSurfaces

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    private val _protocolError = MutableStateFlow<String?>(null)
    val protocolError: StateFlow<String?> = _protocolError.asStateFlow()

    private var didSeedModel = false

    init {
        viewModelScope.launch {
            messageProcessor.collectMessages()
        }
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            messageProcessor.outboundEvents.collect(::onClientMessage)
        }
        submit(JournalSurfaceAgent.createSurface())
        submit(JournalSurfaceAgent.defineInterface())
        observeJournalData()
    }

    /**
     * Accepts one A2UI JSON envelope, the same document a Gemini agent would stream.
     * Malformed JSON is reported on [protocolError] and is not applied.
     */
    fun onAgentMessage(json: String) {
        messageProcessor.processInput(jsonParser, json)
    }

    fun selectMoodFilter(mood: Mood?) {
        val newMood = if (_uiState.value.selectedMood == mood) null else mood
        _uiState.update { it.copy(selectedMood = newMood) }
    }

    fun onSearchQueryChange(query: String) {
        updateSearchQuery(query, writeDataModel = true)
    }

    fun openAddDialog() {
        _uiState.update { it.copy(isAddEntryDialogOpen = true) }
        if (didSeedModel) {
            JournalSurfaceAgent.showAddDialog().forEach(::submit)
        }
    }

    fun toggleAddDialog(isOpen: Boolean) {
        if (isOpen) {
            openAddDialog()
        } else {
            _uiState.update { it.copy(isAddEntryDialogOpen = false) }
            if (didSeedModel) {
                submit(JournalSurfaceAgent.hideAddDialog())
            }
        }
    }

    fun selectJournalEntry(entry: JournalEntry?) {
        _uiState.update { it.copy(selectedEntry = entry) }
        if (!didSeedModel) return
        if (entry == null) {
            submit(JournalSurfaceAgent.hideDetail())
        } else {
            JournalSurfaceAgent.showDetail(entry).forEach(::submit)
        }
    }

    fun addJournalEntry(title: String, content: String, mood: Mood, bannerImage: String? = null) {
        if (title.isBlank() || content.isBlank()) return
        repository.addEntry(
            JournalEntry(
                title = title.trim(),
                content = content.trim(),
                mood = mood,
                bannerImage = bannerImage?.takeIf { it.isNotBlank() },
            ),
        )
        toggleAddDialog(false)
    }

    fun deleteJournalEntry(id: String) {
        repository.deleteEntry(id)
        if (_uiState.value.selectedEntry?.id == id) {
            selectJournalEntry(null)
        }
    }

    private fun observeJournalData() {
        combine(
            repository.getAllEntries(),
            _uiState.map { it.selectedMood }.distinctUntilChanged(),
            _uiState.map { it.searchQuery }.distinctUntilChanged(),
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
            publishEntries(filtered, mood, query)
            _uiState.update { it.copy(entries = filtered, allEntries = entries) }
        }.launchIn(viewModelScope)
    }

    private fun publishEntries(entries: List<JournalEntry>, mood: Mood?, query: String) {
        if (!didSeedModel) {
            didSeedModel = true
            submit(JournalSurfaceAgent.seedModel(entries, mood, query))
            return
        }
        submit(JournalSurfaceAgent.updateEntries(entries))
        submit(JournalSurfaceAgent.updateSelectedMood(mood))
    }

    private fun onClientMessage(message: A2uiClientToServerMessage) {
        when (message) {
            is A2uiClientEventMessage -> handleAction(message)
            is A2uiClientErrorMessage -> _protocolError.value = message.message
        }
    }

    private fun handleAction(message: A2uiClientEventMessage) {
        if (message.surfaceId != JournalSurfaceAgent.SURFACE_ID) return
        when (message.type) {
            SanctuaryEvents.SELECT_MOOD -> {
                val mood = Mood.fromName(message.context["mood"]?.toString())
                selectMoodFilter(mood)
            }
            SanctuaryEvents.SEARCH_CHANGED -> {
                // The field already wrote /ui/searchQuery. Echoing it would fight the cursor.
                updateSearchQuery(
                    query = message.context["query"]?.toString().orEmpty(),
                    writeDataModel = false,
                )
            }
            SanctuaryEvents.OPEN_ENTRY -> {
                val id = message.context["entryId"]?.toString().orEmpty()
                selectJournalEntry(repository.getEntryById(id))
            }
            SanctuaryEvents.DELETE_ENTRY -> {
                val id = message.context["entryId"]?.toString().orEmpty()
                if (id.isNotBlank()) deleteJournalEntry(id)
            }
            SanctuaryEvents.SAVE_ENTRY -> {
                val mood = Mood.fromName(message.context["mood"]?.toString()) ?: Mood.PEACEFUL
                addJournalEntry(
                    title = message.context["title"]?.toString().orEmpty(),
                    content = message.context["content"]?.toString().orEmpty(),
                    mood = mood,
                    bannerImage = message.context["banner"]?.toString(),
                )
            }
            SanctuaryEvents.DISMISS_ADD -> toggleAddDialog(false)
            SanctuaryEvents.DISMISS_DETAIL -> selectJournalEntry(null)
        }
    }

    private fun updateSearchQuery(query: String, writeDataModel: Boolean) {
        _uiState.update { it.copy(searchQuery = query) }
        if (writeDataModel && didSeedModel) {
            submit(JournalSurfaceAgent.updateSearchQuery(query))
        }
    }

    private fun submit(message: A2uiServerToClientMessage) {
        messageProcessor.processMessage(message)
    }
}
