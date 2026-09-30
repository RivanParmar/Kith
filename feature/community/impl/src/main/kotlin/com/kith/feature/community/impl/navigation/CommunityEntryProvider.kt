package com.kith.feature.community.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.community.api.navigation.CommunityListNavKey
import com.kith.feature.community.api.navigation.CreateCommunityNavKey
import com.kith.feature.community.api.navigation.JoinCommunityNavKey
import com.kith.feature.community.api.navigation.CommunityDetailNavKey // <-- ADD THIS IMPORT
import com.kith.feature.community.impl.CommunitiesScreen
import com.kith.feature.community.impl.CreateCommunityScreen
import com.kith.feature.community.impl.JoinCommunityScreen
import com.kith.feature.community.impl.CommunityDetailScreen // <-- ADD THIS IMPORT

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



    entry<CommunityListNavKey>{
        CommunitiesScreen(
            // 1. Tell the list screen where to go when a card is clicked
            onCommunityClick = { communityId ->
                navigator.navigate(CommunityDetailNavKey(communityId))
            }
        )
    }

    // 2. Add the Detail Screen to your routing graph
    entry<CommunityDetailNavKey> { key -> // <--- Grab the key here!
        CommunityDetailScreen(
            communityId = key.communityId, // <--- Pass it into the screen
            onBackClick = { navigator.goBack() },
            onNavigateUp = { navigator.goBack() }
        )
    }
}