package com.piappstudio.a2ui.di

import android.content.Context
import androidx.a2ui.compose.ui.A2uiCatalog
import com.piappstudio.a2ui.a2ui.agent.MockAgentBackend
import com.piappstudio.a2ui.a2ui.catalog.createBankingCatalog
import com.piappstudio.a2ui.data.repository.BankingRepository
import com.piappstudio.a2ui.data.repository.BankingRepositoryImpl

class AppContainer(context: Context) {
    val bankingRepository: BankingRepository = BankingRepositoryImpl()
    val agentBackend: MockAgentBackend = MockAgentBackend(bankingRepository)
    val catalog: A2uiCatalog = createBankingCatalog(context)
}
