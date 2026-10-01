package com.kith.core.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

fun horizontalSlideMetadata() = metadata {
    val slideSpec = tween<IntOffset>(durationMillis = 350, easing = FastOutSlowInEasing)
    val fadeSpec = tween<Float>(durationMillis = 350, easing = FastOutSlowInEasing)

    put(NavDisplay.TransitionKey) {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = slideSpec
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = slideSpec
        ) + fadeOut(animationSpec = fadeSpec)
    }

    put(NavDisplay.PopTransitionKey) {
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = slideSpec
        ) + fadeIn(animationSpec = fadeSpec) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = slideSpec
        )
    }

    put(NavDisplay.PredictivePopTransitionKey) {
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = slideSpec
        ) + fadeIn(animationSpec = fadeSpec) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = slideSpec
        )
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