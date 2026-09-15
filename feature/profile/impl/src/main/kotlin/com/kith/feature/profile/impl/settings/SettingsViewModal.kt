package com.kith.feature.profile.impl.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DynamicColorOption {
    YES,
    NO
}

enum class DarkModePreference {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK
}

data class SettingsUiState(
    val theme: AppTheme = AppTheme.DEFAULT,
    val dynamicColor: DynamicColorOption = DynamicColorOption.YES,
    val darkMode: DarkModePreference = DarkModePreference.SYSTEM_DEFAULT,
    val notificationsEnabled: Boolean = false // NOTIFICATION SETTING
)

@HiltViewModel // NOTIFICATION SETTING
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository // NOTIFICATION SETTING
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // NOTIFICATION SETTING
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { userData ->
                _uiState.value = _uiState.value.copy(
                    notificationsEnabled = userData.pushNotificationsEnabled
                )
            }
        }
    }

    fun setDynamicColor(option: DynamicColorOption) {
        _uiState.value = _uiState.value.copy(
            dynamicColor = option
        )
    }

    fun setDarkMode(mode: DarkModePreference) {
        _uiState.value = _uiState.value.copy(
            darkMode = mode
        )
    }

    // NOTIFICATION SETTING
    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPushNotificationsEnabled(enabled)
        }
    }
}
