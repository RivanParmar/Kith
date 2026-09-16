package com.kith.feature.auth.impl

sealed interface SignUpUiEvent {
    data class ShowToast(val message: String) : SignUpUiEvent
    data object NavigateToHome : SignUpUiEvent
    data object NavigateToSignIn : SignUpUiEvent
}
data class SignUpUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)