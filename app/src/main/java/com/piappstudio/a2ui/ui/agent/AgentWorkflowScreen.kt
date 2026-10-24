package com.piappstudio.a2ui.ui.agent

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.piappstudio.a2ui.a2ui.renderer.DynamicA2uiScreen
import com.piappstudio.a2ui.ui.viewmodel.A2uiSessionViewModel
import com.piappstudio.a2ui.ui.viewmodel.AgentSessionUiState

@Composable
fun AgentWorkflowScreen(
    viewModel: A2uiSessionViewModel,
    uiState: AgentSessionUiState,
    onClose: () -> Unit,
) {
    val surfaces by viewModel.activeSurfaces.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "document"
            viewModel.onDocumentPicked(name)
        } else {
            viewModel.consumeDocumentPicker()
        }
    }

    LaunchedEffect(uiState.closeRequested) {
        if (uiState.closeRequested) onClose()
    }
    LaunchedEffect(uiState.pickDocument) {
        if (uiState.pickDocument) {
            picker.launch(arrayOf("application/pdf", "image/*"))
        }
    }
    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { snackbar.showSnackbar(it) }
    }

    val surface = surfaces.firstOrNull { it.id == uiState.workflow.surfaceId } ?: surfaces.firstOrNull()

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            DynamicA2uiScreen(
                surfaceModel = surface,
                isLoading = uiState.isLoading,
                errorMessage = uiState.error,
            )
        }
    }
}
