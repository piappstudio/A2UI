package com.piappstudio.a2ui.a2ui.renderer

import androidx.a2ui.compose.runtime.A2uiComponentReference
import androidx.a2ui.compose.runtime.A2uiComponentScope
import androidx.a2ui.compose.runtime.A2uiComponentState
import androidx.a2ui.compose.runtime.observeA2uiComponentState
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun A2uiComponentScope.A2uiSlot(
    id: String?,
    modifier: Modifier = Modifier,
    onLoading: @Composable () -> Unit = { CircularProgressIndicator() },
) {
    if (id.isNullOrBlank()) return
    when (val state = observeA2uiComponentState(id)) {
        A2uiComponentState.Loading -> onLoading()
        is A2uiComponentState.Error -> {
            Text(
                text = state.exception.message ?: "This section could not be rendered.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        is A2uiComponentState.Success -> A2uiComponent(state.component, modifier)
    }
}

@Composable
fun A2uiComponentScope.A2uiSlot(
    reference: A2uiComponentReference,
    modifier: Modifier = Modifier,
) {
    when (val state = observeA2uiComponentState(reference)) {
        A2uiComponentState.Loading -> CircularProgressIndicator()
        is A2uiComponentState.Error -> {
            Text(
                text = state.exception.message ?: "This section could not be rendered.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        is A2uiComponentState.Success -> A2uiComponent(state.component, modifier)
    }
}
