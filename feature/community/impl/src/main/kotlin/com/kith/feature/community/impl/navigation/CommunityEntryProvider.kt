package com.kith.feature.community.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.community.api.navigation.CreateCommunityNavKey
import com.kith.feature.community.api.navigation.JoinCommunityNavKey
import com.kith.feature.community.impl.CreateCommunityScreen
import com.kith.feature.community.impl.JoinCommunityScreen

fun EntryProviderScope<NavKey>.communityEntry(navigator: Navigator) {
    entry<JoinCommunityNavKey> {
        JoinCommunityScreen(
            onBack = {},
            onJoined = {},
            onFindCommunity = {},
            onCreateCommunity = { navigator.navigate(CreateCommunityNavKey) },
        )
    }

    entry<CreateCommunityNavKey> {
        CreateCommunityScreen(
            onBack = { navigator.goBack() },
            onCreated = {},
            onPickIcon = {},
        )
    }
}