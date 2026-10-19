package com.piappstudio.a2ui.a2ui.catalog

import androidx.a2ui.compose.runtime.A2uiComponentProperties
import androidx.a2ui.compose.runtime.A2uiComponentScope
import androidx.a2ui.compose.runtime.A2uiProperty
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.piappstudio.a2ui.a2ui.renderer.A2uiSlot
import com.piappstudio.a2ui.data.model.Mood

private const val HEADER_IMAGE =
    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80"

object JournalScreenComponent : A2uiComponent {
    private val headerProp = A2uiProperty.componentId(
        key = "header",
        required = true,
        description = "Component id of the sanctuary header.",
    )
    private val moodFilterProp = A2uiProperty.componentId(
        key = "moodFilter",
        required = true,
        description = "Component id of the mood filter.",
    )
    private val searchProp = A2uiProperty.componentId(
        key = "search",
        required = true,
        description = "Component id of the search field.",
    )
    private val entriesProp = A2uiProperty.childList(
        key = "entries",
        required = true,
        description = "Journal cards. Use a child template bound to the entries list in the data model.",
    )
    private val addDialogProp = A2uiProperty.componentId(
        key = "addDialog",
        required = true,
        description = "Component id of the add-entry dialog.",
    )
    private val detailDialogProp = A2uiProperty.componentId(
        key = "detailDialog",
        required = true,
        description = "Component id of the entry detail dialog.",
    )

    override val name: String = "JournalScreen"
    override val description: String =
        "Scrollable sanctuary journal. Header, mood filter, and search stay above a data-bound list of journal cards. Dialogs overlay the list."
    override val properties = listOf(
        headerProp,
        moodFilterProp,
        searchProp,
        entriesProp,
        addDialogProp,
        detailDialogProp,
    )

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val headerId = properties[headerProp]
        val moodId = properties[moodFilterProp]
        val searchId = properties[searchProp]
        val addDialogId = properties[addDialogProp]
        val detailDialogId = properties[detailDialogProp]
        val entries = properties.bindChildReferences(entriesProp)

        Box(modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    A2uiSlot(headerId, onLoading = {})
                }
                item { A2uiSlot(moodId, onLoading = {}) }
                item { A2uiSlot(searchId, onLoading = {}) }
                when {
                    entries == null -> {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Gathering your reflections…",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    entries.isEmpty() -> item { EmptyJournalState() }
                    else -> {
                        items(
                            items = entries,
                            key = { reference -> reference.baseDataPath ?: reference.id },
                        ) { reference ->
                            A2uiSlot(reference)
                        }
                    }
                }
                item { Spacer(Modifier.height(88.dp)) }
            }
            A2uiSlot(addDialogId, onLoading = {})
            A2uiSlot(detailDialogId, onLoading = {})
        }
    }
}

object SanctuaryHeaderComponent : A2uiComponent {
    private val badgeProp = A2uiProperty.dynamicString(
        key = "badge",
        required = true,
        description = "Short label drawn over the header image.",
    )
    private val titleProp = A2uiProperty.dynamicString(
        key = "title",
        required = true,
        description = "Header title.",
    )
    private val subtitleProp = A2uiProperty.dynamicString(
        key = "subtitle",
        required = true,
        description = "Header subtitle.",
    )
    private val imageUrlProp = A2uiProperty.dynamicString(
        key = "imageUrl",
        required = true,
        description = "Remote image URL for the header.",
    )

    override val name: String = "SanctuaryHeader"
    override val description: String = "Tranquil image header with a badge, title, and subtitle."
    override val properties = listOf(badgeProp, titleProp, subtitleProp, imageUrlProp)

    @Composable
    override fun A2uiComponentScope.isReady(properties: A2uiComponentProperties): Boolean {
        return properties.bind(titleProp) != null
    }

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val badge = properties.bind(badgeProp).orEmpty()
        val title = properties.bind(titleProp).orEmpty()
        val subtitle = properties.bind(subtitleProp).orEmpty()
        val imageUrl = properties.bind(imageUrlProp) ?: HEADER_IMAGE

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Tranquil nature background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                            ),
                        ),
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                    )
                }
            }
        }
    }
}

object MoodFilterComponent : A2uiComponent {
    private val selectedProp = A2uiProperty.dynamicString(
        key = "selected",
        required = true,
        description = "Display name of the selected mood, or an empty string when no filter is active.",
    )
    private val selectEventProp = A2uiProperty.string(
        key = "selectEvent",
        required = true,
        description = "Event name emitted when a mood chip is tapped. Context contains mood.",
    )

    override val name: String = "MoodFilter"
    override val description: String =
        "Horizontal mood chips. Tapping a chip emits selectEvent with context.mood set to that mood's display name. Tapping the active mood clears the filter when the client toggles it."
    override val properties = listOf(selectedProp, selectEventProp)

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val selected = properties.bind(selectedProp).orEmpty()
        val eventName = properties[selectEventProp] ?: return
        Column(modifier.fillMaxWidth()) {
            Text(
                text = "Filter by Mood",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Mood.entries, key = { it.name }) { mood ->
                    FilterChip(
                        selected = selected.equals(mood.displayName, ignoreCase = true),
                        onClick = {
                            dispatchAction(
                                event(
                                    name = eventName,
                                    context = mapOf("mood" to mood.displayName),
                                ),
                            )
                        },
                        label = { Text("${mood.emoji} ${mood.displayName}") },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
            }
        }
    }
}

object SearchFieldComponent : A2uiComponent {
    private val valueProp = A2uiProperty.dynamicString(
        key = "value",
        required = true,
        description = "Search text. Bind this to a writable data-model path.",
    )
    private val changeEventProp = A2uiProperty.string(
        key = "changeEvent",
        required = true,
        description = "Event name emitted after the text changes. Context contains query.",
    )

    override val name: String = "SearchField"
    override val description: String =
        "Single-line search field. Writes the text back to its data binding and emits changeEvent with context.query."
    override val properties = listOf(valueProp, changeEventProp)

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val value = properties.bind(valueProp).orEmpty()
        val updater = properties.bindUpdater(valueProp)
        val eventName = properties[changeEventProp] ?: return
        OutlinedTextField(
            value = value,
            onValueChange = { updated ->
                updater?.invoke(updated)
                dispatchAction(
                    event(
                        name = eventName,
                        context = mapOf("query" to updated),
                    ),
                )
            },
            modifier = modifier.fillMaxWidth(),
            placeholder = { Text("Search reflections & notes...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            updater?.invoke("")
                            dispatchAction(
                                event(
                                    name = eventName,
                                    context = mapOf("query" to ""),
                                ),
                            )
                        },
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            singleLine = true,
            enabled = updater != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
        )
    }
}

object JournalCardComponent : A2uiComponent {
    private val entryIdProp = A2uiProperty.dynamicString("entryId", required = true, description = "Stable entry id.")
    private val titleProp = A2uiProperty.dynamicString("title", required = true, description = "Entry title.")
    private val contentProp = A2uiProperty.dynamicString("content", required = true, description = "Entry body.")
    private val moodProp = A2uiProperty.dynamicString("mood", required = true, description = "Mood display name.")
    private val emojiProp = A2uiProperty.dynamicString("emoji", required = true, description = "Mood emoji.")
    private val dateProp = A2uiProperty.dynamicString("date", required = true, description = "Formatted date.")
    private val bannerProp = A2uiProperty.dynamicString("banner", required = true, description = "Banner image URL. Empty hides the image.")
    private val openEventProp = A2uiProperty.string(
        key = "openEvent",
        required = true,
        description = "Event emitted when the card is opened. Context contains entryId.",
    )
    private val deleteEventProp = A2uiProperty.string(
        key = "deleteEvent",
        required = true,
        description = "Event emitted when delete is tapped. Context contains entryId.",
    )

    override val name: String = "JournalCard"
    override val description: String =
        "One journal reflection. Bind fields to the current list-item scope. Opening or deleting emits the configured event with the resolved entryId."
    override val properties = listOf(
        entryIdProp,
        titleProp,
        contentProp,
        moodProp,
        emojiProp,
        dateProp,
        bannerProp,
        openEventProp,
        deleteEventProp,
    )

    @Composable
    override fun A2uiComponentScope.isReady(properties: A2uiComponentProperties): Boolean {
        return properties.bind(titleProp) != null && properties.bind(entryIdProp) != null
    }

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val entryId = properties.bind(entryIdProp).orEmpty()
        val title = properties.bind(titleProp).orEmpty()
        val content = properties.bind(contentProp).orEmpty()
        val mood = properties.bind(moodProp).orEmpty()
        val emoji = properties.bind(emojiProp).orEmpty()
        val date = properties.bind(dateProp).orEmpty()
        val banner = properties.bind(bannerProp).orEmpty()
        val openEvent = properties[openEventProp] ?: return
        val deleteEvent = properties[deleteEventProp] ?: return

        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    dispatchAction(event(openEvent, mapOf("entryId" to entryId)))
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column {
                if (banner.isNotBlank()) {
                    AsyncImage(
                        model = banner,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
                Column(Modifier.padding(20.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text(
                                text = "$emoji $mood".trim(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        IconButton(
                            onClick = {
                                dispatchAction(event(deleteEvent, mapOf("entryId" to entryId)))
                            },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

object AddEntryDialogComponent : A2uiComponent {
    private val openProp = A2uiProperty.dynamicBoolean(
        key = "open",
        required = true,
        description = "Whether the dialog is visible.",
    )
    private val titleProp = A2uiProperty.dynamicString("title", required = true, description = "Draft title. Must be a writable path.")
    private val contentProp = A2uiProperty.dynamicString("content", required = true, description = "Draft reflection. Must be a writable path.")
    private val moodProp = A2uiProperty.dynamicString("mood", required = true, description = "Draft mood display name. Must be a writable path.")
    private val bannerProp = A2uiProperty.dynamicString("banner", required = true, description = "Draft banner URL. Must be a writable path.")
    private val saveEventProp = A2uiProperty.string(
        key = "saveEvent",
        required = true,
        description = "Event emitted on save. Context contains title, content, mood, and banner.",
    )
    private val dismissEventProp = A2uiProperty.string(
        key = "dismissEvent",
        required = true,
        description = "Event emitted when the dialog is dismissed.",
    )

    override val name: String = "AddEntryDialog"
    override val description: String =
        "Modal for a new reflection. Field edits write through their data bindings. Save emits the current draft."
    override val properties = listOf(
        openProp,
        titleProp,
        contentProp,
        moodProp,
        bannerProp,
        saveEventProp,
        dismissEventProp,
    )

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val open = properties.bind(openProp) == true
        if (!open) return
        val title = properties.bind(titleProp).orEmpty()
        val content = properties.bind(contentProp).orEmpty()
        val moodName = properties.bind(moodProp).orEmpty()
        val banner = properties.bind(bannerProp).orEmpty()
        val titleUpdater = properties.bindUpdater(titleProp)
        val contentUpdater = properties.bindUpdater(contentProp)
        val moodUpdater = properties.bindUpdater(moodProp)
        val bannerUpdater = properties.bindUpdater(bannerProp)
        val saveEvent = properties[saveEventProp] ?: return
        val dismissEvent = properties[dismissEventProp] ?: return
        val selectedMood = Mood.fromName(moodName) ?: Mood.PEACEFUL

        Dialog(onDismissRequest = { dispatchAction(event(dismissEvent)) }) {
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    Modifier
                        .padding(24.dp)
                        .heightIn(max = 520.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("New Journal Reflection", style = MaterialTheme.typography.headlineSmall)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { titleUpdater?.invoke(it) },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = titleUpdater != null,
                        shape = RoundedCornerShape(16.dp),
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { contentUpdater?.invoke(it) },
                        label = { Text("Reflection / Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        enabled = contentUpdater != null,
                        shape = RoundedCornerShape(16.dp),
                    )
                    OutlinedTextField(
                        value = banner,
                        onValueChange = { bannerUpdater?.invoke(it) },
                        label = { Text("Banner Image URL (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = bannerUpdater != null,
                        shape = RoundedCornerShape(16.dp),
                    )
                    Text(
                        text = "Select Mood",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Mood.entries, key = { it.name }) { mood ->
                            FilterChip(
                                selected = selectedMood == mood,
                                onClick = { moodUpdater?.invoke(mood.displayName) },
                                label = { Text("${mood.emoji} ${mood.displayName}") },
                                shape = RoundedCornerShape(16.dp),
                            )
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { dispatchAction(event(dismissEvent)) }) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                dispatchAction(
                                    event(
                                        name = saveEvent,
                                        context = mapOf(
                                            "title" to title,
                                            "content" to content,
                                            "mood" to selectedMood.displayName,
                                            "banner" to banner,
                                        ),
                                    ),
                                )
                            },
                            enabled = title.isNotBlank() && content.isNotBlank(),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text("Save Entry")
                        }
                    }
                }
            }
        }
    }
}

object EntryDetailDialogComponent : A2uiComponent {
    private val openProp = A2uiProperty.dynamicBoolean("open", required = true, description = "Whether the dialog is visible.")
    private val entryIdProp = A2uiProperty.dynamicString("entryId", required = true, description = "Id of the open entry.")
    private val titleProp = A2uiProperty.dynamicString("title", required = true, description = "Entry title.")
    private val contentProp = A2uiProperty.dynamicString("content", required = true, description = "Entry body.")
    private val moodProp = A2uiProperty.dynamicString("mood", required = true, description = "Mood display name.")
    private val emojiProp = A2uiProperty.dynamicString("emoji", required = true, description = "Mood emoji.")
    private val dateProp = A2uiProperty.dynamicString("date", required = true, description = "Formatted date.")
    private val bannerProp = A2uiProperty.dynamicString("banner", required = true, description = "Banner image URL.")
    private val closeEventProp = A2uiProperty.string(
        key = "closeEvent",
        required = true,
        description = "Event emitted when the dialog closes.",
    )
    private val deleteEventProp = A2uiProperty.string(
        key = "deleteEvent",
        required = true,
        description = "Event emitted when delete is tapped. Context contains entryId.",
    )

    override val name: String = "EntryDetailDialog"
    override val description: String = "Read-only detail dialog for one journal entry."
    override val properties = listOf(
        openProp,
        entryIdProp,
        titleProp,
        contentProp,
        moodProp,
        emojiProp,
        dateProp,
        bannerProp,
        closeEventProp,
        deleteEventProp,
    )

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val open = properties.bind(openProp) == true
        if (!open) return
        val entryId = properties.bind(entryIdProp).orEmpty()
        val title = properties.bind(titleProp).orEmpty()
        val content = properties.bind(contentProp).orEmpty()
        val mood = properties.bind(moodProp).orEmpty()
        val emoji = properties.bind(emojiProp).orEmpty()
        val date = properties.bind(dateProp).orEmpty()
        val banner = properties.bind(bannerProp).orEmpty()
        val closeEvent = properties[closeEventProp] ?: return
        val deleteEvent = properties[deleteEventProp] ?: return

        Dialog(onDismissRequest = { dispatchAction(event(closeEvent)) }) {
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    Modifier
                        .padding(24.dp)
                        .heightIn(max = 520.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "$emoji $mood".trim(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (banner.isNotBlank()) {
                        AsyncImage(
                            model = banner,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Text(content, style = MaterialTheme.typography.bodyLarge)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(
                            onClick = {
                                dispatchAction(event(deleteEvent, mapOf("entryId" to entryId)))
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text("Delete")
                        }
                        Button(
                            onClick = { dispatchAction(event(closeEvent)) },
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyJournalState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Eco,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No journal entries found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Take a moment to record your peaceful thoughts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            )
        }
    }
}

private fun event(name: String, context: Map<String, Any?> = emptyMap()): Map<String, Any?> {
    return mapOf(
        "event" to mapOf(
            "name" to name,
            "context" to context,
        ),
    )
}
