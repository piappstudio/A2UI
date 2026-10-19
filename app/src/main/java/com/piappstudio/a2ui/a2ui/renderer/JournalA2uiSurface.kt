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
 * Hosts one A2UI surface inside the app's Compose tree.
 *
 * The surface model is the only UI input. Component lookup, data binding, and
 * loading or error states come from the renderer, not from hand-built widgets
 * that read the journal repository directly.
 */
@Composable
fun JournalA2uiSurface(
    surfaceModel: A2uiSurfaceModel,
    modifier: Modifier = Modifier,
) {
    val coreSurface = surfaceModel as? A2uiCoreSurfaceModel
    val catalog = coreSurface?.catalog as? A2uiCatalog
    if (coreSurface == null || catalog == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "This surface cannot be rendered.",
                color = MaterialTheme.colorScheme.error,
            )
        }
        return
    }

    val readinessEvaluator = remember(catalog) { catalog.asReadinessEvaluator() }
    CompositionLocalProvider(LocalA2uiReadinessEvaluator provides readinessEvaluator) {
        when (val rootState = observeA2uiComponentState(surfaceModel)) {
            A2uiComponentState.Loading -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is A2uiComponentState.Error -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = rootState.exception.message ?: "The journal surface failed to load.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
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
