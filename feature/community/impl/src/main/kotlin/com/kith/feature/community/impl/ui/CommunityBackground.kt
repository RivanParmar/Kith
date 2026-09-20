package com.kith.feature.community.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter

@Composable
internal fun MeshGradientBackground(
    content: @Composable BoxScope.() -> Unit,
) {
    val meshGradientPainter = remember {
        MeshGradientPainter(4, 4) {
            setVertex(0, 0, Offset(0f, 0f), Color(0xFF5B8DF9))
            setVertex(0, 1, Offset(0.33f, 0f), Color(0xFF5B8DF9))
            setVertex(0, 2, Offset(0.66f, 0f), Color(0xFF0841BE))
            setVertex(0, 3, Offset(1f, 0f), Color(0xFF002A88))

            setVertex(1, 0, Offset(0f, 0.33f), Color(0xFF003ABA))
            setVertex(1, 1, Offset(0.33f, 0.33f), Color(0xFF5B8DF9))
            setVertex(1, 2, Offset(0.66f, 0.33f), Color(0xFF5B8DF9))
            setVertex(1, 3, Offset(1f, 0.33f), Color(0xFF5B8DF9))

            setVertex(2, 0, Offset(0f, 0.66f), Color(0xFF2563EB))
            setVertex(2, 1, Offset(0.33f, 0.66f), Color(0xFF608DEF))
            setVertex(2, 2, Offset(0.66f, 0.66f), Color(0xFF2E60CC))
            setVertex(2, 3, Offset(1f, 0.66f), Color(0xFF608DEF))

            setVertex(3, 0, Offset(0f, 1f), Color(0xFF5B8DF9))
            setVertex(3, 1, Offset(0.33f, 1f), Color(0xFF4D75CC))
            setVertex(3, 2, Offset(0.66f, 1f), Color(0xFF1243AE))
            setVertex(3, 3, Offset(1f, 1f), Color(0xFF0036AD))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painter = meshGradientPainter),
    ) {
        content()
    }
}