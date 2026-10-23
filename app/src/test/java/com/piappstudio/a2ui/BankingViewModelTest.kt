package com.piappstudio.a2ui

import com.piappstudio.a2ui.a2ui.agent.BankingSurfaceAgent
import com.piappstudio.a2ui.data.repository.BankingRepositoryImpl
import com.piappstudio.a2ui.ui.viewmodel.BankingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BankingViewModelTest {

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
    fun `viewModel initializes with message processor and banking accounts`() = runTest {
        val repo = BankingRepositoryImpl()
        val viewModel = BankingViewModel(repository = repo)

        val state = viewModel.uiState.value
        assertNotNull(viewModel.messageProcessor)
        assertTrue(state.accounts.isNotEmpty())
        assertTrue(state.transactions.isNotEmpty())
        assertNull(viewModel.protocolError.value)
        val surface = viewModel.activeSurfaces.value
            .firstOrNull { it.id == BankingSurfaceAgent.SURFACE_ID_DASHBOARD }
        assertNotNull(surface)
    }

    @Test
    fun `submitting high risk transfer triggers verification state`() = runTest {
        val repo = BankingRepositoryImpl()
        val viewModel = BankingViewModel(repository = repo)

        viewModel.submitTransferDraft(
            recipient = "Swiss Bank AG",
            amount = "2500.0",
            isInternational = true,
            execute = true
        )

        val state = viewModel.uiState.value
        assertTrue(state.isHighRisk)
        assertTrue(state.isVerificationStep)
    }

    @Test
    fun `opening transfer page updates current surface`() = runTest {
        val repo = BankingRepositoryImpl()
        val viewModel = BankingViewModel(repository = repo)

        viewModel.openTransferPage()
        val state = viewModel.uiState.value
        assertTrue(state.currentSurfaceId == BankingSurfaceAgent.SURFACE_ID_TRANSFER)
    }

    @Test
    fun `selecting transaction updates current surface and selected transaction`() = runTest {
        val repo = BankingRepositoryImpl()
        val viewModel = BankingViewModel(repository = repo)

        val txId = viewModel.uiState.value.transactions.first().id
        viewModel.selectTransaction(txId)
        val state = viewModel.uiState.value
        assertTrue(state.currentSurfaceId == BankingSurfaceAgent.SURFACE_ID_TRANSACTION_DETAIL)
        assertNotNull(state.selectedTransaction)
    }
}
