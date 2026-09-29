package com.kith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.MainActivityUiState.Loading
import com.kith.MainActivityUiState.Success
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.data.repository.UserDataRepository
import com.kith.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
    authRepository: AuthRepository,
    communityRepository: CommunityRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = combine(
        userDataRepository.userData,
        authRepository.sessionStatus,
        communityRepository.hasJoinedAnyCommunity,
    ) { userData, isSignedIn, hasJoinedCommunity ->
        Success(userData, isSignedIn, hasJoinedCommunity)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Loading,
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val userData: UserData,
        val isSignedIn: Boolean,
        val hasJoinedCommunity: Boolean,
    ) : MainActivityUiState {
        override val shouldShowOnboarding: Boolean =
            !userData.shouldHideOnboarding
    }

    fun shouldKeepSplashScreen() = this is Loading

    val shouldShowOnboarding: Boolean get() = false
}