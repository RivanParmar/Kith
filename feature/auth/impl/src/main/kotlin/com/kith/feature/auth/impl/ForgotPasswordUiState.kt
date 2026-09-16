package com.kith.feature.auth.impl

// NOTE: reuses the KithColors object already declared in SignInScreen.kt
// (same package, so no import needed).

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val errorMessage: String = "",
    val isSuccess: Boolean = false
)