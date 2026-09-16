package com.kith.feature.auth.impl

sealed interface AuthUiEvent {
    data class ShowToast(val message: String) : AuthUiEvent
    data object NavigateToHome : AuthUiEvent
    data object NavigateToSignUp : AuthUiEvent
    data object NavigateToForgotPassword : AuthUiEvent
}

