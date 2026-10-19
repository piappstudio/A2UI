package com.piappstudio.a2ui

import com.piappstudio.a2ui.a2ui.agent.JournalSurfaceAgent
import com.piappstudio.a2ui.data.model.Mood
import com.piappstudio.a2ui.data.repository.JournalRepositoryImpl
import com.piappstudio.a2ui.ui.viewmodel.JournalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel initializes with message processor and sample entries`() = runTest {
        val repo = JournalRepositoryImpl()
        val viewModel = JournalViewModel(repository = repo)

        val state = viewModel.uiState.value
        assertNotNull(viewModel.messageProcessor)
        assertTrue(state.entries.isNotEmpty())
        assertNull(viewModel.protocolError.value)
        val surface = viewModel.activeSurfaces.value
            .firstOrNull { it.id == JournalSurfaceAgent.SURFACE_ID }
        assertNotNull(surface)
    }

    @Test
    fun `selecting mood filter updates ui state`() = runTest {
        val repo = JournalRepositoryImpl()
        val viewModel = JournalViewModel(repository = repo)

        viewModel.selectMoodFilter(Mood.SERENE)

        assertEquals(Mood.SERENE, viewModel.uiState.value.selectedMood)
    }

    @Test
    fun `adding journal entry creates entry and updates state`() = runTest {
        val repo = JournalRepositoryImpl()
        val viewModel = JournalViewModel(repository = repo)

        viewModel.addJournalEntry(
            title = "Quiet Evening Reflection",
            content = "The sunset glow faded into soft stars.",
            mood = Mood.REFLECTIVE
        )

        val entries = viewModel.uiState.value.entries
        val addedEntry = entries.find { it.title == "Quiet Evening Reflection" }
        assertNotNull(addedEntry)
        assertEquals(Mood.REFLECTIVE, addedEntry?.mood)
    }

    @Test
    fun `deleting journal entry removes entry`() = runTest {
        val repo = JournalRepositoryImpl()
        val viewModel = JournalViewModel(repository = repo)

        val firstEntryId = viewModel.uiState.value.entries.first().id

        viewModel.deleteJournalEntry(firstEntryId)

        val remainingEntries = viewModel.uiState.value.entries
        assertNull(remainingEntries.find { it.id == firstEntryId })
    }
}
