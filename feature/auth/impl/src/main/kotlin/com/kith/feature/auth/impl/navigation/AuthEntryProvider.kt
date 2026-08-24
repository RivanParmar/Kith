package com.kith.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.auth.api.navigation.LoginNavKey
import com.kith.feature.auth.impl.AuthScreen

fun EntryProviderScope<NavKey>.authEntry(navigator: Navigator) {
    entry<LoginNavKey> {
        AuthScreen()
    }
}