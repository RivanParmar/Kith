package com.kith.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

fun horizontalSlideMetadata() = metadata {
    put(NavDisplay.TransitionKey) {
        slideInHorizontally(initialOffsetX = { it }) togetherWith fadeOut()
    }
    put(NavDisplay.PopTransitionKey) {
        fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it })
    }
    put(NavDisplay.PredictivePopTransitionKey) {
        fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it })
    }
}

fun verticalSlideMetadata() = metadata {
    put(NavDisplay.TransitionKey) {
        slideInVertically(initialOffsetY = { it }) togetherWith fadeOut()
    }
    put(NavDisplay.PopTransitionKey) {
        fadeIn() togetherWith slideOutVertically(targetOffsetY = { it })
    }
    put(NavDisplay.PredictivePopTransitionKey) {
        fadeIn() togetherWith slideOutVertically(targetOffsetY = { it })
    }
}