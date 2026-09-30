package com.kith.feature.auth.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

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
            _uiState.update {
                it.copy(errorMessage = "Please fill in all the fields")
            }
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            _uiState.update {
                it.copy(confirmPasswordError = "Passwords don't match")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            authRepository.signUp(
                email = currentState.email,
                password = currentState.password,null
            ).onSuccess {
                fetchAndSyncFcmToken()
                _uiState.update { it.copy(isLoading = false) }
                Log.d("SIGN_UP", "Success!")
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                Log.d("SIGN_UP", "Failed!")
                Log.d("SIGN_UP", throwable.stackTraceToString())
            }
        }
    }

    suspend fun fetchAndSyncFcmToken() {
        try {
            // 1. Manually grab the existing token from Firebase
            val token = FirebaseMessaging.getInstance().token.await()

            // 2. WAIT for Supabase triggers and auth session to settle
            delay(1500)

            // 3. Push it to Supabase now that the user row definitely exists
            userRepository.syncFcmToken(token)

            Log.d("FCM_SYNC", "Token dispatched to repository")
        } catch (e: Exception) {
            Log.e("FCM_SYNC", "Failed to fetch Firebase token", e)
        }
    }
}

data class SignUpUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)