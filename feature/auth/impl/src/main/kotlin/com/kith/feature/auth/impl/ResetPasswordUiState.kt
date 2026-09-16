package com.kith.feature.auth.impl

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmVisible: Boolean = false,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val submitError: String? = null
) {
    val canSubmit: Boolean
        get() = password.isNotBlank() && confirmPassword.isNotBlank() && !isLoading
}