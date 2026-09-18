package com.kith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.MainActivityUiState.Loading
import com.kith.MainActivityUiState.Success
import com.kith.core.data.repository.UserDataRepository
import com.kith.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(val userData: UserData) : MainActivityUiState {
        override val shouldShowOnboarding: Boolean =
            !userData.shouldHideOnboarding
    }

    fun shouldKeepSplashScreen() = this is Loading

    val shouldShowOnboarding: Boolean get() = false
}