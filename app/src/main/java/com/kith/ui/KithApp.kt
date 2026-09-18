package com.kith.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.kith.core.designsystem.component.KithNavigationSuiteScaffold
import com.kith.core.navigation.Navigator
import com.kith.core.navigation.toEntries
import com.kith.feature.auth.impl.navigation.authEntry
import com.kith.feature.browse.impl.navigation.browseEntry
import com.kith.feature.home.impl.navigation.homeEntry
import com.kith.feature.leaderboard.impl.navigation.leaderboardEntry
import com.kith.feature.onboarding.impl.navigation.onboardingEntry
import com.kith.feature.post.impl.navigation.postEntry
import com.kith.feature.profile.impl.navigation.profileEntry
import com.kith.navigation.TOP_LEVEL_NAV_ITEMS

@Composable
fun KithApp(
    appState: KithAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2(),
) {
    val navigator = remember { Navigator(appState.navigationState) }

    val isTopLevelDestination = appState.navigationState.currentKey in TOP_LEVEL_NAV_ITEMS.keys

    KithNavigationSuiteScaffold(
        showNavigation = isTopLevelDestination,
        navigationSuiteItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val selected = navKey == appState.navigationState.currentTopLevelKey
                item(
                    selected = selected,
                    onClick = { navigator.navigate(navKey) },
                    icon = {
                        Icon(
                            imageVector = navItem.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = navItem.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(navItem.iconTextId)) },
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                val entryProvider = entryProvider {
                    authEntry(navigator)
                    browseEntry(navigator)
                    homeEntry(navigator)
                    leaderboardEntry(navigator)
                    onboardingEntry(navigator)
                    postEntry(navigator)
                    profileEntry(navigator)
                }

                NavDisplay(
                    entries = appState.navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                )
            }
        }
    }
}
