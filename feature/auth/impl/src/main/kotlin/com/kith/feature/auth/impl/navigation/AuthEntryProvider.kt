package com.kith.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.core.navigation.horizontalSlideMetadata
import com.kith.feature.auth.api.navigation.ForgotPasswordNavKey
import com.kith.feature.auth.api.navigation.ResetPasswordNavKey
import com.kith.feature.auth.api.navigation.SignInNavKey
import com.kith.feature.auth.api.navigation.SignUpNavKey
import com.kith.feature.auth.impl.ForgotPasswordScreen
import com.kith.feature.auth.impl.ResetPasswordScreen
import com.kith.feature.auth.impl.SignInScreen
import com.kith.feature.auth.impl.SignUpScreen
import com.kith.feature.home.api.navigation.HomeNavKey

fun EntryProviderScope<NavKey>.authEntry(navigator: Navigator) {
    entry<SignInNavKey> {
        SignInScreen(
            onCreateAccountClicked = { navigator.navigate(SignUpNavKey) },
            onForgotPasswordClicked = { navigator.navigate(ForgotPasswordNavKey) },
            onSignInComplete = { navigator.navigate(HomeNavKey) }
        )
    }

    entry<SignUpNavKey>(
        metadata = horizontalSlideMetadata()
    ) {
        SignUpScreen(
            onSignInClicked = { navigator.navigate(SignInNavKey) }
        )
    }

    entry<ForgotPasswordNavKey>(
        metadata = horizontalSlideMetadata()
    ) {
        ForgotPasswordScreen()
    }

    entry<ResetPasswordNavKey>(
        metadata = horizontalSlideMetadata()
    ) {
        ResetPasswordScreen(
            onResetSuccess = { /* Handle completion */ }
        )
    }
}