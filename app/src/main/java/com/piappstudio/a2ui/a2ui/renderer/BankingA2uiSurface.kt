package com.piappstudio.a2ui.a2ui.renderer

import androidx.a2ui.compose.runtime.A2uiComponentState
import androidx.a2ui.compose.runtime.LocalA2uiReadinessEvaluator
import androidx.a2ui.compose.runtime.observeA2uiComponentState
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.a2ui.compose.ui.asReadinessEvaluator
import androidx.a2ui.engine.model.A2uiCoreSurfaceModel
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A unified, reusable Compose screen (Generic Surface Host) that takes any [A2uiSurfaceModel]
 * and renders it via the A2UI catalog, supporting dynamic navigation between surfaces by ID.
 */
@Composable
fun A2UISurfaceHost(
    surfaceModel: A2uiSurfaceModel?,
    modifier: Modifier = Modifier,
    onError: @Composable (String) -> Unit = { errorMsg ->
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    },
    onLoading: @Composable () -> Unit = {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    },
) {
    if (surfaceModel == null) {
        onError("Surface not found or not initialized.")
        return
    }

    val coreSurface = surfaceModel as? A2uiCoreSurfaceModel
    val catalog = coreSurface?.catalog as? A2uiCatalog
    if (coreSurface == null || catalog == null) {
        onError("This surface cannot be rendered.")
        return
    }

    val readinessEvaluator = remember(catalog) { catalog.asReadinessEvaluator() }
    CompositionLocalProvider(LocalA2uiReadinessEvaluator provides readinessEvaluator) {
        when (val rootState = observeA2uiComponentState(surfaceModel)) {
            A2uiComponentState.Loading -> {
                onLoading()
            }
            is A2uiComponentState.Error -> {
                onError(rootState.exception.message ?: "The surface failed to load.")
            }
            is A2uiComponentState.Success -> {
                A2uiComponent(
                    component = rootState.component,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
fun GenericSurfaceScreen(
    surfaceModel: A2uiSurfaceModel?,
    modifier: Modifier = Modifier,
    onError: @Composable (String) -> Unit = { errorMsg ->
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    },
    onLoading: @Composable () -> Unit = {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    },
) {
    A2UISurfaceHost(
        surfaceModel = surfaceModel,
        modifier = modifier,
        onError = onError,
        onLoading = onLoading,
    )
}

@Composable
fun BankingA2uiSurface(
    surfaceModel: A2uiSurfaceModel,
    modifier: Modifier = Modifier,
) {
    A2UISurfaceHost(surfaceModel = surfaceModel, modifier = modifier)
}
