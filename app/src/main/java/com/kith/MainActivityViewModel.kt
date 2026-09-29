package com.kith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.data.repository.UserDataRepository
import com.kith.core.model.data.DarkThemeConfig
import com.kith.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
    authRepository: AuthRepository,
    communityRepository: CommunityRepository,
) : ViewModel() {

//    val uiState: StateFlow<MainActivityUiState> = combine(
//        userDataRepository.userData,
//        authRepository.sessionStatus,
//        communityRepository.hasJoinedAnyCommunity,
//    ) { userData, isSignedIn, hasJoinedCommunity ->
//        Success(userData, isSignedIn, hasJoinedCommunity)
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = Loading,
//    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MainActivityUiState> = combine(
        userDataRepository.userData,
        authRepository.sessionStatus
    ) { userData, isSignedIn ->
        Pair(userData, isSignedIn)
    }.flatMapLatest { (userData, isSignedIn) ->
        if (!isSignedIn) {
            flowOf(MainActivityUiState.Success(userData, isSignedIn = false, hasJoinedCommunity = false))
        } else {
            flow {
                val hasJoinedLocally = communityRepository.hasJoinedAnyCommunitySync()

                if (!hasJoinedLocally) {
                    communityRepository.syncJoinedCommunities()
                }

                emitAll(
                    communityRepository.hasJoinedAnyCommunity.map { hasJoinedLive ->
                        MainActivityUiState.Success(
                            userData = userData,
                            isSignedIn = true,
                            hasJoinedCommunity = hasJoinedLive,
                        )
                    }
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState.Loading,
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val userData: UserData,
        val isSignedIn: Boolean,
        val hasJoinedCommunity: Boolean,
    ) : MainActivityUiState {
        override val shouldDisableDynamicTheming = !userData.useDynamicColor

        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean =
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }

        override val shouldShowOnboarding: Boolean =
            !userData.shouldHideOnboarding
    }

    fun shouldKeepSplashScreen() = this is Loading

    val shouldDisableDynamicTheming: Boolean get() = true

    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme

    val shouldShowOnboarding: Boolean get() = false
}