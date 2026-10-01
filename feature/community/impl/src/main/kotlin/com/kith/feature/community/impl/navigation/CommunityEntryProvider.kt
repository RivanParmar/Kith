package com.kith.feature.community.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.core.navigation.horizontalSlideMetadata
import com.kith.core.navigation.verticalSlideMetadata
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

    entry<CreateCommunityNavKey>(
        metadata = verticalSlideMetadata()
    ) {
        CreateCommunityScreen(
            onBack = { navigator.goBack() },
            onCreated = {},
        )
    }

    entry<CommunityListNavKey>{
        CommunitiesScreen(
            onCommunityClick = { communityId ->
                navigator.navigate(CommunityDetailNavKey(communityId))
            },
            onJoinClick = { navigator.navigate(JoinCommunityNavKey) },
            onBackClick = { navigator.goBack() }
        )
    }

    entry<CommunityDetailNavKey>(
        metadata = horizontalSlideMetadata()
    ) { key ->
        CommunityDetailScreen(
            communityId = key.communityId,
            onBackClick = { navigator.goBack() },
            onNavigateUp = { navigator.goBack() }
        )
    }
}