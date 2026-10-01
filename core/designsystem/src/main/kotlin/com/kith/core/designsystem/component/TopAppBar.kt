package com.kith.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kith.core.designsystem.theme.OutfitFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KithMediumTopAppBar(
    @StringRes titleRes: Int,
    navigationIcon: ImageVector?,
    navigationIconContentDescription: String?,
    actionIcon: ImageVector?,
    actionIconContentDescription: String?,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        scrolledContainerColor = MaterialTheme.colorScheme.background,
    ),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    fontFamily: FontFamily = OutfitFontFamily,
    fontWeight: FontWeight = FontWeight.ExtraBold
) {
    MediumFlexibleTopAppBar(
        title = { Text(text = stringResource(id = titleRes), fontFamily = fontFamily, fontWeight = fontWeight) },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                    )
                }
            }
        },
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = colors,
        modifier = modifier,
    )
}