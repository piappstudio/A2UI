package com.piappstudio.a2ui.a2ui.catalog

import androidx.a2ui.compose.ui.A2uiCatalog

/**
 * Sanctuary Journal catalog.
 *
 * The catalog is the contract an agent is allowed to use. Components, property
 * schemas, and the events they emit are fixed here. The agent describes a
 * surface with these components. It does not send executable UI code.
 */
const val SANCTUARY_CATALOG_ID: String =
    "https://piappstudio.com/a2ui/catalogs/sanctuary-journal/v1"

fun sanctuaryJournalCatalog(): A2uiCatalog = A2uiCatalog(
    catalogId = SANCTUARY_CATALOG_ID,
    components = listOf(
        JournalScreenComponent,
        SanctuaryHeaderComponent,
        MoodFilterComponent,
        SearchFieldComponent,
        JournalCardComponent,
        AddEntryDialogComponent,
        EntryDetailDialogComponent,
    ),
)

object SanctuaryEvents {
    const val SELECT_MOOD = "selectMood"
    const val SEARCH_CHANGED = "searchChanged"
    const val OPEN_ENTRY = "openEntry"
    const val DELETE_ENTRY = "deleteEntry"
    const val SAVE_ENTRY = "saveEntry"
    const val DISMISS_ADD = "dismissAdd"
    const val DISMISS_DETAIL = "dismissDetail"
}
