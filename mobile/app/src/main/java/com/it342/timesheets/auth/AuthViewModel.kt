package com.it342.timesheets.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.it342.timesheets.data.ApiClient
import com.it342.timesheets.data.TokenStore
import com.it342.timesheets.data.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: UserResponse? = null,
    val loading: Boolean = true,
    val loginError: String? = null,
    val registerError: String? = null,
    val employerOptions: List<UserResponse> = emptyList(),
    val loadingEmployers: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenStore = TokenStore(application)
    private val repository = AuthRepository(tokenStore)

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val token = repository.getStoredToken()
            if (token != null) {
                ApiClient.setToken(token)
                val user = repository.fetchCurrentUser()
                _state.value = _state.value.copy(
                    currentUser = user ?: repository.getStoredUser(),
                    loading = false
                )
            } else {
                _state.value = _state.value.copy(loading = false, currentUser = null)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loginError = null)
            when (val result = repository.login(username, password)) {
                is AuthResult.Success ->
                    _state.value = _state.value.copy(currentUser = result.user, loginError = null)
                is AuthResult.Error ->
                    _state.value = _state.value.copy(loginError = result.message)
            }
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        role: String,
        employerUsername: String?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(registerError = null)
            when (val result = repository.register(username, email, password, role, employerUsername)) {
                is AuthResult.Success ->
                    _state.value = _state.value.copy(currentUser = result.user, registerError = null)
                is AuthResult.Error ->
                    _state.value = _state.value.copy(registerError = normalizeRegisterError(result.message))
            }
        }
    }

    fun searchEmployers(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loadingEmployers = true)
            val options = repository.searchEmployers(query)
            _state.value = _state.value.copy(
                employerOptions = options,
                loadingEmployers = false
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.value = _state.value.copy(currentUser = null)
        }
    }

    fun clearLoginError() { _state.value = _state.value.copy(loginError = null) }
    fun clearRegisterError() { _state.value = _state.value.copy(registerError = null) }

    private fun normalizeRegisterError(message: String?): String {
        if (message == "User already exists" || message == "Email already exists") {
            return "User already exists."
        }
        return message ?: "Registration failed."
    }
}
