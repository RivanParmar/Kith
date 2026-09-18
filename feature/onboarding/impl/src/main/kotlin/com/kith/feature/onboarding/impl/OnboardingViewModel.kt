package com.kith.feature.onboarding.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    fun onCompleteOnboarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnboarding(true)
        }
    }
}