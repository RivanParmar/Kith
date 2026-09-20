package com.kith.feature.auth.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SignInNavKey : NavKey

@Serializable
data object SignUpNavKey : NavKey

@Serializable
data object ForgotPasswordNavKey : NavKey

@Serializable
data object ResetPasswordNavKey : NavKey