package com.piappstudio.a2ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.piappstudio.a2ui.data.model.BankingCustomer
import com.piappstudio.a2ui.data.repository.BankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val customer: BankingCustomer? = null,
)

class SessionViewModel(
    private val repository: BankingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun signInDemo() {
        _state.update { it.copy(isLoading = true, error = null) }
        _state.update {
            it.copy(
                isLoading = false,
                isAuthenticated = true,
                customer = repository.customer(),
            )
        }
    }

    fun signIn(username: String, password: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        if (username.isBlank()) {
            _state.update { it.copy(isLoading = false, error = "Enter a demo username.") }
            return
        }
        if (password != "demo") {
            _state.update {
                it.copy(isLoading = false, error = "Use the demo password “demo”, or continue as a demo customer.")
            }
            return
        }
        signInDemo()
    }

    fun signOut() {
        _state.value = AuthUiState()
    }
}
