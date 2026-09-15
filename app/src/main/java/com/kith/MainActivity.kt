package com.kith

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kith.core.designsystem.theme.KithTheme
import com.kith.feature.profile.impl.ProfileRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val splashStartTime = System.currentTimeMillis()
        val splashDurationMillis = 2000L
        splashScreen.setKeepOnScreenCondition {
            System.currentTimeMillis() - splashStartTime < splashDurationMillis
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KithTheme {
                ProfileRoute(onWalletClick = {})
            }
        }
    }
}
