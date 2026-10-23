package com.piappstudio.a2ui.a2ui.agent

import androidx.a2ui.model.protocol.A2uiComponentPayload
import androidx.a2ui.model.protocol.A2uiCreateSurfaceMessage
import androidx.a2ui.model.protocol.A2uiServerToClientMessage
import androidx.a2ui.model.protocol.A2uiUpdateComponentsMessage
import androidx.a2ui.model.protocol.A2uiUpdateDataModelMessage
import com.piappstudio.a2ui.a2ui.catalog.SANCTUARY_CATALOG_ID
import com.piappstudio.a2ui.a2ui.catalog.SanctuaryEvents
import com.piappstudio.a2ui.data.model.JournalEntry
import com.piappstudio.a2ui.data.model.Mood

/**
 * Builds the A2UI server-to-client stream for the sanctuary journal.
 *
 * Structure and data are separate messages: [createSurface] chooses the catalog,
 * [updateComponents] sends the flat adjacency list (one component must use id
 * "root"), and [updateDataModel] fills the bindings those components read.
 *
 * A remote agent sends the same documents as JSON. [com.piappstudio.a2ui.ui.viewmodel.JournalViewModel.onAgentMessage]
 * parses one object per call. Do not wrap the stream in an array. `version` is
 * required and must be `v0.9` or `v0.9.1`. The component type is the JSON field
 * `component`. Every other field on that object is a property. A binding is
 * `{"path": "..."}`. A path that starts with `/` is absolute. A path without `/`
 * is relative to the template item (`/entries/0`, `/entries/1`, ...). Send the
 * messages in this order.
 *
 * ```
 * {
 *   "version": "v0.9",
 *   "createSurface": {
 *     "surfaceId": "journal_surface",
 *     "catalogId": "https://piappstudio.com/a2ui/catalogs/sanctuary-journal/v1",
 *     "theme": {},
 *     "sendDataModel": true
 *   }
 * }
 * ```
 *
 * ```
 * {
 *   "version": "v0.9",
 *   "updateComponents": {
 *     "surfaceId": "journal_surface",
 *     "components": [
 *       {
 *         "id": "root",
 *         "component": "JournalScreen",
 *         "header": "header",
 *         "moodFilter": "mood_filter",
 *         "search": "search_field",
 *         "entries": { "componentId": "entry_card", "path": "/entries" },
 *         "addDialog": "add_dialog",
 *         "detailDialog": "detail_dialog"
 *       },
 *       {
 *         "id": "header",
 *         "component": "SanctuaryHeader",
 *         "badge": "🌿 DAILY SANCTUARY",
 *         "title": "Breathe deeply, reflect softly.",
 *         "subtitle": "Capture your quiet moments in nature and let your mind unwind.",
 *         "imageUrl": "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80"
 *       },
 *       {
 *         "id": "mood_filter",
 *         "component": "MoodFilter",
 *         "selected": { "path": "/ui/selectedMood" },
 *         "selectEvent": "selectMood"
 *       },
 *       {
 *         "id": "search_field",
 *         "component": "SearchField",
 *         "value": { "path": "/ui/searchQuery" },
 *         "changeEvent": "searchChanged"
 *       },
 *       {
 *         "id": "entry_card",
 *         "component": "JournalCard",
 *         "entryId": { "path": "id" },
 *         "title": { "path": "title" },
 *         "content": { "path": "content" },
 *         "mood": { "path": "mood" },
 *         "emoji": { "path": "emoji" },
 *         "date": { "path": "date" },
 *         "banner": { "path": "banner" },
 *         "openEvent": "openEntry",
 *         "deleteEvent": "deleteEntry"
 *       },
 *       {
 *         "id": "add_dialog",
 *         "component": "AddEntryDialog",
 *         "open": { "path": "/ui/addOpen" },
 *         "title": { "path": "/draft/title" },
 *         "content": { "path": "/draft/content" },
 *         "mood": { "path": "/draft/mood" },
 *         "banner": { "path": "/draft/banner" },
 *         "saveEvent": "saveEntry",
 *         "dismissEvent": "dismissAdd"
 *       },
 *       {
 *         "id": "detail_dialog",
 *         "component": "EntryDetailDialog",
 *         "open": { "path": "/ui/detailOpen" },
 *         "entryId": { "path": "/ui/selectedEntry/id" },
 *         "title": { "path": "/ui/selectedEntry/title" },
 *         "content": { "path": "/ui/selectedEntry/content" },
 *         "mood": { "path": "/ui/selectedEntry/mood" },
 *         "emoji": { "path": "/ui/selectedEntry/emoji" },
 *         "date": { "path": "/ui/selectedEntry/date" },
 *         "banner": { "path": "/ui/selectedEntry/banner" },
 *         "closeEvent": "dismissDetail",
 *         "deleteEvent": "deleteEntry"
 *       }
 *     ]
 *   }
 * }
 * ```
 *
 * The value at path `/` must be a JSON object. A list at the root is rejected.
 * `null` deletes that path.
 *
 * ```
 * {
 *   "version": "v0.9",
 *   "updateDataModel": {
 *     "surfaceId": "journal_surface",
 *     "path": "/",
 *     "value": {
 *       "ui": {
 *         "selectedMood": "",
 *         "searchQuery": "",
 *         "addOpen": false,
 *         "detailOpen": false,
 *         "selectedEntry": {
 *           "id": "",
 *           "title": "",
 *           "content": "",
 *           "mood": "",
 *           "emoji": "",
 *           "date": "",
 *           "banner": ""
 *         }
 *       },
 *       "draft": {
 *         "title": "",
 *         "content": "",
 *         "mood": "Peaceful",
 *         "banner": ""
 *       },
 *       "entries": [
 *         {
 *           "id": "entry-1",
 *           "title": "Spring Rain in the Fern Glen",
 *           "content": "The path was quiet and the ferns held the rain.",
 *           "mood": "Renewed",
 *           "emoji": "🌸",
 *           "date": "Oct 18, 2026 • 10:47 PM",
 *           "banner": "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1200&q=80"
 *         }
 *       ]
 *     }
 *   }
 * }
 * ```
 *
 * Later updates touch one path so search text, the draft, and dialog flags stay
 * put. Replacing `/entries` re-renders the card template. Opening the add dialog
 * is two messages: reset `/draft`, then set `/ui/addOpen` to true.
 *
 * ```
 * {
 *   "version": "v0.9",
 *   "updateDataModel": {
 *     "surfaceId": "journal_surface",
 *     "path": "/entries",
 *     "value": []
 *   }
 * }
 * ```
 *
 * ```
 * { "version": "v0.9", "deleteSurface": { "surfaceId": "journal_surface" } }
 * ```
 *
 * Clicks stay in the app. The widgets resolve their bindings and emit a client
 * event whose `type` is the event name from the component (`selectMood`,
 * `searchChanged`, `openEntry`, `deleteEntry`, `saveEntry`, `dismissAdd`,
 * `dismissDetail`). Card actions carry the already-bound literal id, because a
 * relative path in action context is resolved from `/`, not from the list item.
 */
object JournalSurfaceAgent {
    const val SURFACE_ID: String = "journal_surface"

    const val ROOT_ID: String = "root"
    const val HEADER_ID: String = "header"
    const val MOOD_FILTER_ID: String = "mood_filter"
    const val SEARCH_ID: String = "search_field"
    const val ENTRY_CARD_ID: String = "entry_card"
    const val ADD_DIALOG_ID: String = "add_dialog"
    const val DETAIL_DIALOG_ID: String = "detail_dialog"

    fun createSurface(): A2uiCreateSurfaceMessage = A2uiCreateSurfaceMessage(
        surfaceId = SURFACE_ID,
        catalogId = SANCTUARY_CATALOG_ID,
        theme = emptyMap(),
        shouldSendDataModel = true,
    )

    fun defineInterface(): A2uiUpdateComponentsMessage = A2uiUpdateComponentsMessage(
        surfaceId = SURFACE_ID,
        components = listOf(
            component(
                ROOT_ID,
                "JournalScreen",
                "header" to HEADER_ID,
                "moodFilter" to MOOD_FILTER_ID,
                "search" to SEARCH_ID,
                "entries" to mapOf(
                    "componentId" to ENTRY_CARD_ID,
                    "path" to "/entries",
                ),
                "addDialog" to ADD_DIALOG_ID,
                "detailDialog" to DETAIL_DIALOG_ID,
            ),
            component(
                HEADER_ID,
                "SanctuaryHeader",
                "badge" to "🌿 DAILY SANCTUARY",
                "title" to "Breathe deeply, reflect softly.",
                "subtitle" to "Capture your quiet moments in nature and let your mind unwind.",
                "imageUrl" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80",
            ),
            component(
                MOOD_FILTER_ID,
                "MoodFilter",
                "selected" to path("/ui/selectedMood"),
                "selectEvent" to SanctuaryEvents.SELECT_MOOD,
            ),
            component(
                SEARCH_ID,
                "SearchField",
                "value" to path("/ui/searchQuery"),
                "changeEvent" to SanctuaryEvents.SEARCH_CHANGED,
            ),
            component(
                ENTRY_CARD_ID,
                "JournalCard",
                "entryId" to path("id"),
                "title" to path("title"),
                "content" to path("content"),
                "mood" to path("mood"),
                "emoji" to path("emoji"),
                "date" to path("date"),
                "banner" to path("banner"),
                "openEvent" to SanctuaryEvents.OPEN_ENTRY,
                "deleteEvent" to SanctuaryEvents.DELETE_ENTRY,
            ),
            component(
                ADD_DIALOG_ID,
                "AddEntryDialog",
                "open" to path("/ui/addOpen"),
                "title" to path("/draft/title"),
                "content" to path("/draft/content"),
                "mood" to path("/draft/mood"),
                "banner" to path("/draft/banner"),
                "saveEvent" to SanctuaryEvents.SAVE_ENTRY,
                "dismissEvent" to SanctuaryEvents.DISMISS_ADD,
            ),
            component(
                DETAIL_DIALOG_ID,
                "EntryDetailDialog",
                "open" to path("/ui/detailOpen"),
                "entryId" to path("/ui/selectedEntry/id"),
                "title" to path("/ui/selectedEntry/title"),
                "content" to path("/ui/selectedEntry/content"),
                "mood" to path("/ui/selectedEntry/mood"),
                "emoji" to path("/ui/selectedEntry/emoji"),
                "date" to path("/ui/selectedEntry/date"),
                "banner" to path("/ui/selectedEntry/banner"),
                "closeEvent" to SanctuaryEvents.DISMISS_DETAIL,
                "deleteEvent" to SanctuaryEvents.DELETE_ENTRY,
            ),
        ),
    )

    fun seedModel(
        entries: List<JournalEntry>,
        selectedMood: Mood?,
        searchQuery: String,
    ): A2uiUpdateDataModelMessage = dataModel(
        path = "/",
        value = mapOf(
            "ui" to mapOf(
                "selectedMood" to (selectedMood?.displayName ?: ""),
                "searchQuery" to searchQuery,
                "addOpen" to false,
                "detailOpen" to false,
                "selectedEntry" to blankEntry(),
            ),
            "draft" to defaultDraft(),
            "entries" to entryRecords(entries),
        ),
    )

    fun updateEntries(entries: List<JournalEntry>): A2uiServerToClientMessage = dataModel(
        path = "/entries",
        value = entryRecords(entries),
    )

    fun updateSelectedMood(mood: Mood?): A2uiServerToClientMessage = dataModel(
        path = "/ui/selectedMood",
        value = mood?.displayName ?: "",
    )

    fun updateSearchQuery(query: String): A2uiServerToClientMessage = dataModel(
        path = "/ui/searchQuery",
        value = query,
    )

    fun showAddDialog(): List<A2uiServerToClientMessage> = listOf(
        dataModel("/draft", defaultDraft()),
        dataModel("/ui/addOpen", true),
    )

    fun hideAddDialog(): A2uiServerToClientMessage = dataModel("/ui/addOpen", false)

    fun showDetail(entry: JournalEntry): List<A2uiServerToClientMessage> = listOf(
        dataModel("/ui/selectedEntry", entryRecord(entry)),
        dataModel("/ui/detailOpen", true),
    )

    fun hideDetail(): A2uiServerToClientMessage = dataModel("/ui/detailOpen", false)

    fun entryRecord(entry: JournalEntry): Map<String, String> = mapOf(
        "id" to entry.id,
        "title" to entry.title,
        "content" to entry.content,
        "mood" to entry.mood.displayName,
        "emoji" to entry.mood.emoji,
        "date" to entry.formattedDate,
        "banner" to entry.bannerImage.orEmpty(),
    )

    private fun entryRecords(entries: List<JournalEntry>): List<Map<String, String>> =
        entries.map(::entryRecord)

    private fun defaultDraft(): Map<String, String> = mapOf(
        "title" to "",
        "content" to "",
        "mood" to Mood.PEACEFUL.displayName,
        "banner" to "",
    )

    private fun blankEntry(): Map<String, String> = mapOf(
        "id" to "",
        "title" to "",
        "content" to "",
        "mood" to "",
        "emoji" to "",
        "date" to "",
        "banner" to "",
    )

    private fun dataModel(path: String, value: Any?): A2uiUpdateDataModelMessage =
        A2uiUpdateDataModelMessage(
            surfaceId = SURFACE_ID,
            path = path,
            value = value,
        )

    private fun path(pointer: String): Map<String, String> = mapOf("path" to pointer)

    private fun component(
        id: String,
        type: String,
        vararg properties: Pair<String, Any?>,
    ): A2uiComponentPayload = A2uiComponentPayload(
        id = id,
        type = type,
        properties = mapOf(*properties),
    )
}
