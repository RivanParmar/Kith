package com.kith.feature.auth.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, passwordError = null, submitError = null) }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, confirmError = null, submitError = null) }

    fun onResetClick() {
        val state = _uiState.value
        if (state.isLoading) return

        val passwordError = validate(state.password)
        val confirmError = when {
            state.confirmPassword.isBlank() -> "Confirm your new password"
            state.confirmPassword != state.password -> "Passwords don't match"
            else -> null
        }

        if (passwordError != null || confirmError != null) {
            _uiState.update { it.copy(passwordError = passwordError, confirmError = confirmError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, submitError = null) }

            authRepository.updateUser(state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    Log.d("RESET_PASS", "Success!")
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            submitError = error.message ?: "Couldn't update password. Try again."
                        )
                    }
                    Log.d("RESET_PASS", "Failed!")
                }
        }
    }

    fun onSuccessHandled() = _uiState.update { it.copy(isSuccess = false) }

    fun onSubmitErrorShown() = _uiState.update { it.copy(submitError = null) }

    private fun validate(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Use at least 8 characters"
        password.none { it.isDigit() } -> "Add at least one number"
        password.none { it.isUpperCase() } -> "Add at least one uppercase letter"
        else -> null
    }
}

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val submitError: String? = null
) {
    val canSubmit: Boolean
        get() = password.isNotBlank() && confirmPassword.isNotBlank() && !isLoading
}