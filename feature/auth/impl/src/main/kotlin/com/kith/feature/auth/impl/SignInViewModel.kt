package com.kith.feature.auth.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                errorMessage = ""
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                errorMessage = ""
            )
        }
    }

    fun onSignInClicked() {
        val currentState = _uiState.value
        val isEmailValid = validateEmail(currentState.email)
        val isPasswordValid = validatePassword(currentState.password)

        if (!isEmailValid || !isPasswordValid) {
            _uiState.update {
                it.copy(
                    emailError = if (!isEmailValid) "Invalid email format" else null,
                    passwordError = if (!isPasswordValid) "Password cannot be empty" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            authRepository.signIn(
                email = currentState.email,
                password = currentState.password
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                }
                Log.d("SIGN_IN", "Success!")
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Sign in failed"
                    )
                }
                Log.d("SIGN_IN", "Failed!")
                Log.d("SIGN_IN", throwable.stackTraceToString())
            }
        }
    }

    fun onAppleSignInClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Execute Apple OAuth flow
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onGoogleSignInClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Execute Google OAuth flow
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 6
    }
}

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val passwordError: String? = null,
    val emailError: String? = null,
    val errorMessage: String = "",
)