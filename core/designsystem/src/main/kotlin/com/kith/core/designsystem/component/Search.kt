package com.kith.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KithSearchBar(
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState = rememberSearchBarState(),
    inputField: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    SearchBar(
        state = searchBarState,
        inputField = inputField,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
        content = content,
    )
}