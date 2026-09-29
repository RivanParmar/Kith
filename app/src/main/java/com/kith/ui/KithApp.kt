package com.kith.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.kith.core.designsystem.component.KithNavigationSuiteScaffold
import com.kith.core.navigation.Navigator
import com.kith.core.navigation.toEntries
import com.kith.feature.auth.impl.navigation.authEntry
import com.kith.feature.browse.impl.navigation.browseEntry
import com.kith.feature.community.impl.navigation.communityEntry
import com.kith.feature.home.impl.navigation.homeEntry
import com.kith.feature.leaderboard.impl.navigation.leaderboardEntry
import com.kith.feature.onboarding.impl.navigation.onboardingEntry
import com.kith.feature.post.impl.navigation.postEntry
import com.kith.feature.profile.impl.navigation.profileEntry
import com.kith.navigation.TOP_LEVEL_NAV_ITEMS

import com.kith.feature.post.impl.LocalMediaPickerHelper as PostLocalMediaPicker
import com.kith.feature.post.impl.MediaPickerHelper as PostMediaPicker

import com.kith.feature.profile.impl.LocalMediaPickerHelper as ProfileLocalMediaPicker
import com.kith.feature.profile.impl.MediaPickerHelper as ProfileMediaPicker

@Composable
fun KithApp(
    appState: KithAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2(),
) {
    val navigator = remember { Navigator(appState.navigationState) }
    val isTopLevelDestination = appState.navigationState.currentKey in TOP_LEVEL_NAV_ITEMS.keys

    // ==========================================
    // ALL LAUNCHERS DEFINED STRICTLY IN KITHAPP
    // ==========================================
    var photoCallback by remember { mutableStateOf<((List<Uri>) -> Unit)?>(null) }
    var singlePhotoCallback by remember { mutableStateOf<((Uri?) -> Unit)?>(null) } // NEW
    var pdfCallback by remember { mutableStateOf<((Uri?) -> Unit)?>(null) }
    var audioCallback by remember { mutableStateOf<((Uri?) -> Unit)?>(null) }

    // Multi-Picker for Posts (Max 5)
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            photoCallback?.invoke(uris)
            photoCallback = null
        }
    )

    // NEW: Single-Picker for Profile
    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            singlePhotoCallback?.invoke(uri)
            singlePhotoCallback = null
        }
    )

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            pdfCallback?.invoke(uri)
            pdfCallback = null
        }
    )

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            audioCallback?.invoke(uri)
            audioCallback = null
        }
    )

    // Helper for CreatePostScreen
    val postMediaHelper = remember {
        PostMediaPicker(
            launchPhotoPicker = { callback ->
                photoCallback = callback
                photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            launchPdfPicker = { callback ->
                pdfCallback = callback
                pdfLauncher.launch("application/pdf")
            },
            launchAudioPicker = { callback ->
                audioCallback = callback
                audioLauncher.launch("audio/*")
            }
        )
    }

    // Helper for EditProfileDialog
    val profileMediaHelper = remember {
        ProfileMediaPicker(
            launchPhotoPicker = { callback ->
                singlePhotoCallback = callback
                // Uses the single image launcher!
                singlePhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
    }

    CompositionLocalProvider(
        PostLocalMediaPicker provides postMediaHelper,
        ProfileLocalMediaPicker provides profileMediaHelper
    ) {
        KithNavigationSuiteScaffold(
            showNavigation = isTopLevelDestination,
            navigationSuiteItems = {
                TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                    val selected = navKey == appState.navigationState.currentTopLevelKey
                    item(
                        selected = selected,
                        onClick = { navigator.navigate(navKey) },
                        icon = { Icon(imageVector = navItem.unselectedIcon, contentDescription = null) },
                        selectedIcon = { Icon(imageVector = navItem.selectedIcon, contentDescription = null) },
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
                        communityEntry(navigator)
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
}