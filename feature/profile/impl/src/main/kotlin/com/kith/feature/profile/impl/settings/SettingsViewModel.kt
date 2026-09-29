package com.kith.feature.profile.impl.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.UserDataRepository
import com.kith.core.model.data.DarkThemeConfig
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
    val dynamicColor: DynamicColorOption = DynamicColorOption.YES,
    val darkMode: DarkModePreference = DarkModePreference.SYSTEM_DEFAULT,
    val notificationsEnabled: Boolean = false // NOTIFICATION SETTING
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository // NOTIFICATION SETTING
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // NOTIFICATION SETTING & THEME SETTINGS
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { userData ->
                _uiState.value = _uiState.value.copy(
                    notificationsEnabled = userData.pushNotificationsEnabled,
                    darkMode = when (userData.darkThemeConfig) {
                        DarkThemeConfig.FOLLOW_SYSTEM -> DarkModePreference.SYSTEM_DEFAULT
                        DarkThemeConfig.LIGHT -> DarkModePreference.LIGHT
                        DarkThemeConfig.DARK -> DarkModePreference.DARK
                    },
                    dynamicColor = if (userData.useDynamicColor) DynamicColorOption.YES else DynamicColorOption.NO
                )
            }
        }
    }

    fun setDynamicColor(option: DynamicColorOption) {
        _uiState.value = _uiState.value.copy(
            dynamicColor = option
        )
        viewModelScope.launch {
            userDataRepository.setDynamicColorPreference(option == DynamicColorOption.YES)
        }
    }

    fun setDarkMode(mode: DarkModePreference) {
        _uiState.value = _uiState.value.copy(
            darkMode = mode
        )
        viewModelScope.launch {
            val config = when (mode) {
                DarkModePreference.SYSTEM_DEFAULT -> DarkThemeConfig.FOLLOW_SYSTEM
                DarkModePreference.LIGHT -> DarkThemeConfig.LIGHT
                DarkModePreference.DARK -> DarkThemeConfig.DARK
            }
            userDataRepository.setDarkThemeConfig(config)
        }
    }

    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPushNotificationsEnabled(enabled)
        }
    }
}
