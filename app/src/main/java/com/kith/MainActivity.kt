package com.kith

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.kith.feature.home.api.navigation.HomeNavKey
import com.kith.feature.onboarding.api.navigation.OnboardingNavKey
import com.kith.ui.KithApp
import com.kith.ui.rememberKithAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
        enableEdgeToEdge()

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    uiState = it
                }
            }
        }

        var isMinSplashTimeElapsed by mutableStateOf(savedInstanceState != null)

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                delay(2000L.milliseconds)
                isMinSplashTimeElapsed = true
            }
        }

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.shouldKeepSplashScreen() || !isMinSplashTimeElapsed
        }

        setContent {
            KithTheme {
                when (val state = uiState) {
                    is MainActivityUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LoadingWheel(
                                contentDesc = "Loading",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    is MainActivityUiState.Success -> {
                        val startNavKey = if (state.userData.shouldHideOnboarding) {
                            HomeNavKey
                        } else {
                            OnboardingNavKey
                        }

                        val appState = rememberKithAppState(
                            startNavKey = startNavKey,
                            networkMonitor = networkMonitor,
                        )
                        KithApp(appState)
                    }
                }
            }
        }
    }
}
