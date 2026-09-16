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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SignUpUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = newConfirmPassword) }
    }

    fun onSignUpClicked() {
        val currentState = _uiState.value

        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            viewModelScope.launch {
//                _uiEvent.send(SignUpUiEvent.ShowError("Please fill in all fields"))
            }
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            viewModelScope.launch {
//                _uiEvent.send(SignUpUiEvent.ShowError("Passwords don't match"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            authRepository.signUp(
                email = currentState.email,
                password = currentState.password,null
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
//                _uiEvent.send(SignUpUiEvent.NavigateToHome)
                Log.d("SIGN_UP", "Success!")
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
//                _uiEvent.send(
//                    SignUpUiEvent.ShowError(throwable.message ?: "Sign up failed, try again")
//                )
                Log.d("SIGN_UP", "Failed!")
                Log.d("SIGN_UP", throwable.stackTraceToString())
            }
        }
    }
}