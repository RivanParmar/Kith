package com.kith

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kith.core.data.util.NetworkMonitor
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.designsystem.theme.KithTheme
import com.kith.feature.auth.api.navigation.SignInNavKey
import com.kith.feature.community.api.navigation.JoinCommunityNavKey
import com.kith.feature.home.api.navigation.HomeNavKey
import com.kith.feature.onboarding.api.navigation.OnboardingNavKey
import com.kith.ui.KithApp
import com.kith.ui.rememberKithAppState
import com.kith.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Removed static enableEdgeToEdge() here; it will be called dynamically in the flow below.

        // Initialize with default/system values to prevent flashes before state loads
        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme, // Or set a default based on your UI state
                disableDynamicTheming = true, // Or set a default based on your UI state
            )
        )

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)
        var isMinSplashTimeElapsed by mutableStateOf(savedInstanceState != null)

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                delay(2000L.milliseconds)
                isMinSplashTimeElapsed = true
            }
        }

        // Update the uiState and ThemeSettings dynamically
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(), // Listens to system theme changes
                    viewModel.uiState,
                ) { systemDark, state ->
                    uiState = state
                    ThemeSettings(
                        // Assumes you have these properties on MainActivityUiState like NiA does
                        darkTheme = state.shouldUseDarkTheme(systemDark),
                        disableDynamicTheming = state.shouldDisableDynamicTheming,
                    )
                }
                    .onEach { themeSettings = it }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        // Dynamically update EdgeToEdge when theme preference changes
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                lightScrim = lightScrim,
                                darkScrim = darkScrim,
                            ) { darkTheme },
                        )
                    }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            uiState.shouldKeepSplashScreen() || !isMinSplashTimeElapsed
        }

        setContent {
            // Pass the dynamically updated settings to your theme
            KithTheme(
                darkTheme = themeSettings.darkTheme,
                disableDynamicTheming = themeSettings.disableDynamicTheming,
            ) {
                when (val state = uiState) {
                    is MainActivityUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LoadingWheel(
                                contentDesc = "Loading", modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    is MainActivityUiState.Success -> {
                        val startNavKey = if (!state.userData.shouldHideOnboarding) {
                            OnboardingNavKey
                        } else if (!state.isSignedIn) {
                            SignInNavKey
                        } else if (!state.hasJoinedCommunity) {
                            JoinCommunityNavKey
                        } else {
                            HomeNavKey
                        }

                        AnimatedContent(
                            targetState = startNavKey,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                                    animationSpec = tween(500)
                                )
                            },
                            label = "RootGraphTransition",
                        ) { targetKey ->
                            val appState = rememberKithAppState(
                                startNavKey = targetKey,
                                networkMonitor = networkMonitor,
                            )
                            KithApp(appState)
                        }
                    }
                }
            }
        }
    }
}

/**
 * The default light scrim, as defined by androidx and the platform.
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform.
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/**
 * Class for the system theme settings.
 */
data class ThemeSettings(
    val darkTheme: Boolean,
    val disableDynamicTheming: Boolean,
)