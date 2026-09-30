package com.kith.feature.auth.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.designsystem.theme.LightBlue

@Composable
internal fun AuthLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val screenHeight = LocalWindowInfo.current.containerDpSize.height

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        WaveHeader(
            modifier = Modifier.height(screenHeight * 0.35f)
        )

        Column(
            modifier = Modifier
//                .weight(1f)
                .heightIn(min = screenHeight * 0.65f)
                .fillMaxWidth()
                .background(LightBlue)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

@Preview
@Composable
private fun AuthLayoutPreview() {
    KithTheme {
        AuthLayout { }
    }
}