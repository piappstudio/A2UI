package com.piappstudio.a2ui.a2ui.renderer

import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.a2ui.A2uiSurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DynamicA2uiScreen(
    surfaceModel: A2uiSurfaceModel?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    Box(modifier.fillMaxSize()) {
        when {
            surfaceModel != null -> {
                A2uiSurface(
                    surfaceModel = surfaceModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            }
            else -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        if (isLoading && surfaceModel != null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
            )
        }
    }
}
