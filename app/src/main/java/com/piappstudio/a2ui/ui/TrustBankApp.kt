package com.piappstudio.a2ui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.piappstudio.a2ui.TrustBankApplication
import com.piappstudio.a2ui.a2ui.agent.AgentWorkflow
import com.piappstudio.a2ui.ui.agent.AgentWorkflowScreen
import com.piappstudio.a2ui.ui.auth.LoginScreen
import com.piappstudio.a2ui.ui.dashboard.DashboardScreen
import com.piappstudio.a2ui.ui.navigation.AgentRoute
import com.piappstudio.a2ui.ui.navigation.DashboardRoute
import com.piappstudio.a2ui.ui.navigation.LoginRoute
import com.piappstudio.a2ui.ui.viewmodel.A2uiSessionViewModel
import com.piappstudio.a2ui.ui.viewmodel.DashboardViewModel
import com.piappstudio.a2ui.ui.viewmodel.SessionViewModel
import com.piappstudio.a2ui.ui.viewmodel.TrustBankViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustBankApp() {
    val app = LocalContext.current.applicationContext as TrustBankApplication
    val container = app.container
    val sessionViewModel: SessionViewModel = viewModel(
        factory = TrustBankViewModelFactory(container),
    )
    val auth by sessionViewModel.state.collectAsState()
    val backStack = rememberNavBackStack(LoginRoute)

    LaunchedEffect(auth.isAuthenticated) {
        if (auth.isAuthenticated && backStack.lastOrNull() is LoginRoute) {
            backStack.add(DashboardRoute)
        }
        if (!auth.isAuthenticated) {
            while (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
            if (backStack.lastOrNull() != LoginRoute) {
                backStack.clear()
                backStack.add(LoginRoute)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<LoginRoute> {
                LoginScreen(
                    state = auth,
                    onSignIn = sessionViewModel::signIn,
                    onDemo = sessionViewModel::signInDemo,
                )
            }
            entry<DashboardRoute> {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = TrustBankViewModelFactory(container),
                )
                val dashState by dashboardViewModel.uiState.collectAsState()
                Scaffold(
                    topBar = {
                        TrustBankTopBar(
                            title = "TrustBank",
                            showBack = false,
                            onBack = {},
                            onSignOut = sessionViewModel::signOut,
                        )
                    },
                ) { padding ->
                    DashboardScreen(
                        state = dashState,
                        contentPadding = padding,
                        onOpenWorkflow = { workflow, txId ->
                            backStack.add(AgentRoute(workflow.name, txId))
                        },
                    )
                }
            }
            entry<AgentRoute> { route ->
                val workflow = route.workflow
                val agentViewModel: A2uiSessionViewModel = viewModel(
                    key = "${route.workflowName}:${route.transactionId}",
                    factory = TrustBankViewModelFactory(container, workflow, route.transactionId),
                )
                val agentState by agentViewModel.uiState.collectAsState()
                Scaffold(
                    topBar = {
                        TrustBankTopBar(
                            title = workflow.title,
                            showBack = true,
                            onBack = {
                                agentViewModel.cancel()
                                backStack.removeLastOrNull()
                            },
                            onSignOut = null,
                        )
                    },
                ) { padding ->
                    Box(
                        Modifier.padding(padding),
                    ) {
                        AgentWorkflowScreen(
                            viewModel = agentViewModel,
                            uiState = agentState,
                            onClose = { backStack.removeLastOrNull() },
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrustBankTopBar(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
    onSignOut: (() -> Unit)?,
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (onSignOut != null) {
                TextButton(onClick = onSignOut) { Text("Sign out") }
            }
        },
    )
}
