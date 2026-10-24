package com.piappstudio.a2ui.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.piappstudio.a2ui.ui.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    state: AuthUiState,
    onSignIn: (String, String) -> Unit,
    onDemo: () -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("alex.rivera") }
    var password by rememberSaveable { mutableStateOf("demo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Rounded.AccountBalance,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text("TrustBank", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Secure demo banking with A2UI", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "MOCK AUTHENTICATION · No real bank credentials",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Demo username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Demo password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )
        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onSignIn(username, password) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Sign in to demo session")
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onDemo,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Continue as demo customer")
        }
    }
}
