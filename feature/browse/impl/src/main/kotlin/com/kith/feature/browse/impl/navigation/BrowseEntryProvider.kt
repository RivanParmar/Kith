package com.kith.feature.browse.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.browse.api.navigation.BrowseNavKey
import com.kith.feature.browse.impl.BrowseScreen

fun EntryProviderScope<NavKey>.browseEntry(navigator: Navigator) {
    entry<BrowseNavKey> {
        BrowseScreen()
    }
}